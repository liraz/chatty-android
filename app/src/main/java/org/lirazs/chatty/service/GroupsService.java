/**
 * Copyright Liraz Shilkrot
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.lirazs.chatty.service;

import android.content.Context;
import android.text.TextUtils;

import org.lirazs.chatty.event.ApplicationEvent;
import org.lirazs.chatty.event.ApplicationEventListener;
import org.lirazs.chatty.event.AuthenticationEvent;
import org.lirazs.chatty.event.AuthenticationEventListener;
import org.lirazs.chatty.event.service.GroupServiceEvent;
import org.lirazs.chatty.model.firebase.FirebaseGroupObject;
import org.lirazs.chatty.model.realm.DBGroup;
import org.lirazs.chatty.util.FetchCallback;
import org.lirazs.chatty.util.SecurityUtils;
import org.lirazs.chatty.util.VoidCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by mac on 2/17/17.
 */

public class GroupsService implements ApplicationEventListener, AuthenticationEventListener {

    private DatabaseReference databaseReference;

    private Context context;
    private AuthService authService;
    private ChatService chatService;
    private RecentService recentService;

    private List<ChildEventListener> childEventListeners;


    public static String generateGroupIdByMembers(List<String> members) {
        Collections.sort(members);

        return SecurityUtils.md5OfString(TextUtils.join("", members));
    }


    public GroupsService(Context context, AuthService authService, ChatService chatService,
                         RecentService recentService) {
        this.context = context;
        this.authService = authService;
        this.chatService = chatService;
        this.recentService = recentService;

        childEventListeners = new ArrayList<>();

        EventBus.getDefault().register(this);
    }


    @Subscribe
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        if(applicationEvent.getEventType() == ApplicationEvent.ApplicationEventType.APPLICATION_STARTED) {
            initObservers();
        }
    }

    @Subscribe
    public void onAuthenticationEvent(AuthenticationEvent authenticationEvent) {

        if(authenticationEvent.getEventType() == AuthenticationEvent.Type.USER_LOGGED_IN_SUCCESS) {
            initObservers();
        }

        else if(authenticationEvent.getEventType() == AuthenticationEvent.Type.USER_LOGGED_OUT_SUCCESS) {
            actionCleanup();
        }
    }

    public DBGroup getGroupById(String objectId) {
        Realm realm = Realm.getDefaultInstance();
        DBGroup dbGroup = realm.where(DBGroup.class).equalTo(DBGroup.GROUP_OBJECTID, objectId).findFirst();

        realm.close();

        return dbGroup;
    }


    public void deleteGroupItem(String objectId) {
        FirebaseGroupObject firebaseGroupObject = new FirebaseGroupObject();

        firebaseGroupObject.setObjectId(objectId);
        firebaseGroupObject.setDeleted(true);

        firebaseGroupObject.updateInBackground();
    }

    public void fetchGroup(String groupObjectId, final FetchCallback<FirebaseGroupObject> completion) {
        final FirebaseGroupObject groupObject = new FirebaseGroupObject();
        groupObject.setObjectId(groupObjectId);
        groupObject.fetchInBackground(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null) {
                    if(completion != null) {
                        completion.complete(groupObject);
                    }
                } else {
                    //TODO: Notify error
                }
            }
        });
    }


    public void addGroupMembers(String groupObjectId, final List<String> userIds,
                                final VoidCallback completion) {
        fetchGroup(groupObjectId, new FetchCallback<FirebaseGroupObject>() {
            @Override
            public void complete(final FirebaseGroupObject group) {
                List<String> members = group.getMembers();
                List<String> membersToAdd = new ArrayList<>();

                for (String userId : userIds) {
                    if(!members.contains(userId)) {
                        membersToAdd.add(userId);
                    }
                }
                if (membersToAdd.size() > 0) {

                    members.addAll(membersToAdd);
                    group.setMembers(members);

                    group.saveInBackground(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null) {
                                chatService.generateGroupChatRequest(group);
                                recentService.updateRecentMembersByGroup(group);

                                if(completion != null) {
                                    completion.complete();
                                }

                            } else {
                                //TODO: Notify error
                            }
                        }
                    });
                } else {
                    if(completion != null) {
                        completion.complete();
                    }
                }
            }
        });
    }


    private void initObservers() {
        if(authService.isLoggedIn()) {

            if(databaseReference == null) {
                createObservers();
            }
        }
    }


    private void createObservers() {
        Date lastUpdatedAt = DBGroup.getLastUpdatedAt();

        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseGroupObject.GROUP_PATH);
        Query query = databaseReference.orderByChild(FirebaseGroupObject.UPDATED_AT_KEY);
        if(lastUpdatedAt != null) {
            query.startAt(lastUpdatedAt.getTime() + 1);
        }

        childEventListeners.add(query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseGroupObject group = new FirebaseGroupObject((Map<String, Object>)dataSnapshot.getValue());
                updateRealm(group);

                EventBus.getDefault().post(new GroupServiceEvent(GroupServiceEvent.Type.GROUPS_UPDATED));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseGroupObject group = new FirebaseGroupObject((Map<String, Object>)dataSnapshot.getValue());
                updateRealm(group);

                EventBus.getDefault().post(new GroupServiceEvent(GroupServiceEvent.Type.GROUPS_UPDATED));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        }));
    }

    private void updateRealm(FirebaseGroupObject group) {

        DBGroup temp = new DBGroup(group);

        Realm realm = Realm.getDefaultInstance();

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(temp);
            realm.commitTransaction();
        } finally {
            realm.close();
        }
    }

    private void actionCleanup() {
        for (ChildEventListener childEventListener : childEventListeners) {
            databaseReference.removeEventListener(childEventListener);
        }
        childEventListeners.clear();
        databaseReference = null;
    }
}
