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

import org.lirazs.chatty.event.ApplicationEvent;
import org.lirazs.chatty.event.ApplicationEventListener;
import org.lirazs.chatty.event.AuthenticationEvent;
import org.lirazs.chatty.event.AuthenticationEventListener;
import org.lirazs.chatty.event.service.RecentServiceEvent;
import org.lirazs.chatty.model.enums.ChatType;
import org.lirazs.chatty.model.firebase.FirebaseGroupObject;
import org.lirazs.chatty.model.firebase.FirebaseMessageObject;
import org.lirazs.chatty.model.firebase.FirebaseRecentObject;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.model.realm.DBRecent;
import org.lirazs.chatty.util.FetchCallback;
import org.lirazs.chatty.util.SecurityUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by mac on 2/17/17.
 */

public class RecentService implements ApplicationEventListener, AuthenticationEventListener {

    private DatabaseReference databaseReference;

    private Context context;
    private AuthService authService;
    private UsersService usersService;

    private List<ChildEventListener> childEventListeners;

    public RecentService(Context context, AuthService authService, UsersService usersService) {
        this.context = context;
        this.authService = authService;
        this.usersService = usersService;

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


    public void fetchRecentsByGroupId(String groupId, final FetchCallback<List<FirebaseRecentObject>> completion) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseRecentObject.RECENT_PATH);
        Query query = reference.orderByChild(FirebaseRecentObject.RECENT_GROUP_ID).equalTo(groupId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FirebaseRecentObject> recents = new ArrayList<>();

                if(dataSnapshot.exists()) {
                    Map value = (Map) dataSnapshot.getValue();
                    Collection<Map<String, Object>> values = value.values();

                    for (Map<String, Object> properties : values) {
                        FirebaseRecentObject recentObject = new FirebaseRecentObject(properties);
                        recents.add(recentObject);
                    }
                }
                if(completion != null) {
                    completion.complete(recents);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public void fetchRecentGroupMembersIds(String groupId, final FetchCallback<List<String>> completion) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseRecentObject.RECENT_PATH);
        Query query = reference.orderByChild(FirebaseRecentObject.RECENT_GROUP_ID).equalTo(groupId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userIds = new ArrayList<>();

                if(dataSnapshot.exists()) {
                    Map value = (Map) dataSnapshot.getValue();
                    Collection<Map<String, Object>> values = value.values();

                    for (Map<String, Object> properties : values) {
                        FirebaseRecentObject recentObject = new FirebaseRecentObject(properties);
                        userIds.add(recentObject.getUserId());
                    }
                }
                if(completion != null) {
                    completion.complete(userIds);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public List<String> getRecipientNamesFromPrivateChats(List<DBRecent> recents) {
        List<String> names = new ArrayList<>();
        for (DBRecent recent : recents) {
            if (recent.getType() == ChatType.PRIVATE) {
                names.add(recent.getDescription());
            }
        }
        return names;
    }


    public void createPrivateChatRecentItem(String userId, String groupId, String initials, String picture, String description, List<String> members) {

        createRecentItem(userId, groupId, initials, picture, description, members, ChatType.PRIVATE);
    }


    public void createMultipleChatRecentItem(final String groupId, final List<String> members) {

        fetchRecentGroupMembersIds(groupId, new FetchCallback<List<String>>() {
            @Override
            public void complete(List<String> userIds) {

                List<String> createIds = new ArrayList<>(members);

                for (String userId : userIds) {
                    createIds.remove(userId);
                }

                for (String userId : createIds) {
                    String description = usersService.getUserNames(members, userId);

                    FirebaseUserObject currentUser = authService.getCurrentUser();
                    String initials = currentUser.getInitials();
                    String picture = currentUser.getPicture();

                    createRecentItem(userId, groupId, initials, picture, description, members, ChatType.MULTIPLE);
                }
            }
        });
    }


    public void createGroupChatRecentItem(final String groupId, final String picture, final String description, final List<String> members) {

        fetchRecentGroupMembersIds(groupId, new FetchCallback<List<String>>() {
            @Override
            public void complete(List<String> userIds) {
                List<String> createIds = new ArrayList<>(members);

                for (String userId : userIds) {
                    createIds.remove(userId);
                }

                for (String userId : createIds) {
                    FirebaseUserObject currentUser = authService.getCurrentUser();
                    String initials = currentUser.getSafeName();

                    createRecentItem(userId, groupId, initials, picture, description, members, ChatType.GROUP);
                }
            }
        });
    }


    private void createRecentItem(final String userId, final String groupId, final String initials,
                                 final String picture, final String description,
                                 final List<String> members, final ChatType type) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ServerTime");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object serverTimeStamp = dataSnapshot.getValue();

                FirebaseRecentObject object = new FirebaseRecentObject();

                String temp = String.format("%s%s", groupId, userId);
                object.setObjectId(SecurityUtils.md5OfString(temp));

                object.setUserId(userId);
                object.setGroupId(groupId);

                object.setInitials(initials);
                object.setPicture(picture != null ? picture : "");
                object.setDescription(description);
                object.setMembers(members);
                object.setType(type);

                object.setUnreadCounter(0L);
                object.setLastMessage("");
                object.setLastMessageDate(new Date((Long) serverTimeStamp));

                object.setArchived(false);
                object.setDeleted(false);

                object.saveInBackground();
            }

            public void onCancelled(DatabaseError databaseError) { }
        });
        ref.setValue(ServerValue.TIMESTAMP);
    }


    public void updateRecentLastMessage(final FirebaseMessageObject message) {

        fetchRecentsByGroupId(message.getGroupId(), new FetchCallback<List<FirebaseRecentObject>>() {
            @Override
            public void complete(List<FirebaseRecentObject> recents) {

                for (FirebaseRecentObject recent : recents) {
                    updateRecentLastMessage(recent, message.getText());
                }
            }
        });
    }


    public void updateRecentLastMessage(final FirebaseRecentObject recent, final String lastMessage) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ServerTime");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object serverTimeStamp = dataSnapshot.getValue();

                Long counter = recent.getUnreadCounter();

                if(!recent.getUserId().equals(AuthService.getCurrentId())) {
                    counter++;
                }

                recent.setUnreadCounter(counter);
                recent.setLastMessage(lastMessage);
                recent.setLastMessageDate(new Date((Long) serverTimeStamp));

                boolean activate = recent.getMembers().contains(recent.getUserId());
                if(activate) {
                    recent.setArchived(false);
                    recent.setDeleted(false);
                }
                recent.saveInBackground();
            }

            public void onCancelled(DatabaseError databaseError) { }
        });
        ref.setValue(ServerValue.TIMESTAMP);
    }


    public void updateRecentMembersByGroup(final FirebaseGroupObject group) {

        fetchRecentsByGroupId(group.getObjectId(), new FetchCallback<List<FirebaseRecentObject>>() {
            @Override
            public void complete(List<FirebaseRecentObject> recents) {

                for (FirebaseRecentObject recent : recents) {

                    HashSet<String> set1 = new HashSet<>(group.getMembers());
                    HashSet<String> set2 = new HashSet<>(recent.getMembers());

                    if(!set1.equals(set2)) {
                        updateRecentMembers(recent, group.getMembers());
                    }
                }
            }
        });
    }



    public void updateRecentMembers(FirebaseRecentObject recent, List<String> members) {

        if(!members.contains(recent.getUserId())) {
            recent.setDeleted(true);
        }

        recent.setMembers(members);
        recent.saveInBackground();
    }


    public void updateRecentDescriptionByGroup(final FirebaseGroupObject group) {

        fetchRecentsByGroupId(group.getObjectId(), new FetchCallback<List<FirebaseRecentObject>>() {
            @Override
            public void complete(List<FirebaseRecentObject> recents) {

                for (FirebaseRecentObject recent : recents) {
                    recent.setDescription(group.getName());
                    recent.saveInBackground();
                }
            }
        });
    }



    public void updateRecentPictureByGroup(final FirebaseGroupObject group) {

        fetchRecentsByGroupId(group.getObjectId(), new FetchCallback<List<FirebaseRecentObject>>() {
            @Override
            public void complete(List<FirebaseRecentObject> recents) {

                for (FirebaseRecentObject recent : recents) {
                    recent.setPicture(group.getPicture());
                    recent.saveInBackground();
                }
            }
        });
    }



    public void deleteRecentItem(String objectId) {

        FirebaseRecentObject recent = new FirebaseRecentObject();

        recent.setObjectId(objectId);
        recent.setDeleted(true);

        recent.updateInBackground();
    }


    public void archiveRecentItem(String objectId) {

        FirebaseRecentObject recent = new FirebaseRecentObject();

        recent.setObjectId(objectId);
        recent.setArchived(true);

        recent.updateInBackground();
    }

    public void unarchiveRecentItem(String objectId) {

        FirebaseRecentObject recent = new FirebaseRecentObject();

        recent.setObjectId(objectId);
        recent.setArchived(false);

        recent.updateInBackground();
    }


    public void clearUnreadCounterInGroupRecents(String groupId) {

        fetchRecentsByGroupId(groupId, new FetchCallback<List<FirebaseRecentObject>>() {
            @Override
            public void complete(List<FirebaseRecentObject> recents) {

                for (FirebaseRecentObject recent : recents) {

                    if(recent.getUserId().equals(AuthService.getCurrentId())) {
                        recent.setUnreadCounter(0L);
                        recent.saveInBackground();
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

        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRecentObject.RECENT_PATH);
        Query query = databaseReference.orderByChild(FirebaseRecentObject.RECENT_USER_ID).equalTo(AuthService.getCurrentId());

        childEventListeners.add(query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseRecentObject recent = new FirebaseRecentObject((Map<String, Object>)dataSnapshot.getValue());

                //TODO: [Password set:recent[FRECENT_PASSWORD] groupId:recent[FRECENT_GROUPID]];
                /*String groupId = recent.getGroupId();
                recent.setPassword(SecurityUtils.generatePassword(groupId));*/

                updateRealm(recent);

                EventBus.getDefault().post(new RecentServiceEvent(RecentServiceEvent.Type.RECENTS_UPDATED));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseRecentObject recent = new FirebaseRecentObject((Map<String, Object>)dataSnapshot.getValue());
                updateRealm(recent);

                EventBus.getDefault().post(new RecentServiceEvent(RecentServiceEvent.Type.RECENTS_UPDATED));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }));
    }

    private void updateRealm(FirebaseRecentObject recent) {

        DBRecent temp = new DBRecent(recent);

        if(recent.getGroupId() != null && recent.getLastMessage() != null && !recent.getLastMessage().isEmpty()) {
            temp.setLastMessage(SecurityUtils.decryptText(recent.getLastMessage(), recent.getGroupId()));
        }

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
