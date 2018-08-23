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
import org.lirazs.chatty.event.service.UserServiceEvent;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.model.realm.DBUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by mac on 2/17/17.
 */

public class UsersService implements ApplicationEventListener, AuthenticationEventListener {

    private DatabaseReference databaseReference;

    private Context context;
    private AuthService authService;

    private List<ChildEventListener> childEventListeners;

    public UsersService(Context context, AuthService authService) {
        this.context = context;
        this.authService = authService;

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

    public DBUser getUserById(String objectId) {
        Realm realm = Realm.getDefaultInstance();
        DBUser dbUser = realm.where(DBUser.class).equalTo(DBUser.USER_OBJECTID, objectId).findFirst();

        realm.close();

        return dbUser;
    }

    public List<DBUser> getUsersByIds(List<String> userIds) {
        List<DBUser> users = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        List<DBUser> dbUsers = realm.where(DBUser.class).findAllSorted(DBUser.USER_FULL_NAME, Sort.ASCENDING);

        for (DBUser dbUser : dbUsers) {
            if(userIds.contains(dbUser.getObjectId())) {
                users.add(dbUser);
            }
        }
        realm.close();

        return users;
    }

    public List<DBUser> filterExcludedUserNames(List<DBUser> users, List<String> excludeUserNames) {
        List<DBUser> usersResult = new ArrayList<>(users);
        for (String excludeUserName : excludeUserNames) {

            for (int i = 0; i < usersResult.size(); i++) {
                DBUser dbUser = usersResult.get(i);
                if(dbUser.getSafeName().equals(excludeUserName)
                        || (dbUser.getEmail() != null && dbUser.getEmail().equals(excludeUserName))) {
                    usersResult.remove(i);
                    break;
                }
            }
        }
        return usersResult;
    }

    public String getUserNames(List<String> members, String excludeUserId) {
        List<String> names = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        List<DBUser> dbUsers = realm.where(DBUser.class).findAllSorted(DBUser.USER_FULL_NAME, Sort.ASCENDING);

        for (DBUser dbUser : dbUsers) {

            if(members.contains(dbUser.getObjectId())) {
                if(!dbUser.getObjectId().equals(excludeUserId)) {
                    names.add(dbUser.getSafeName());
                }
            }
        }
        realm.close();

        return TextUtils.join(", ", names);
    }

    private void initObservers() {
        if(authService.isLoggedIn()) {

            if(databaseReference == null) {
                createObservers();
            }
        }
    }


    private void createObservers() {
        Date lastUpdatedAt = DBUser.getLastUpdatedAt();

        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseUserObject.USER_PATH);
        Query query = databaseReference.orderByChild(FirebaseUserObject.UPDATED_AT_KEY);
        if(lastUpdatedAt != null) {
            query.startAt(lastUpdatedAt.getTime() + 1);
        }

        childEventListeners.add(query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseUserObject user = new FirebaseUserObject((Map<String, Object>)dataSnapshot.getValue());

                if(user.getSafeName() != null) {
                    updateRealm(user);

                    EventBus.getDefault().post(new UserServiceEvent(UserServiceEvent.Type.USERS_UPDATED));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseUserObject user = new FirebaseUserObject((Map<String, Object>)dataSnapshot.getValue());

                if(user.getSafeName() != null && AuthService.getCurrentId() != null) {
                    updateRealm(user);

                    EventBus.getDefault().post(new UserServiceEvent(UserServiceEvent.Type.USERS_UPDATED));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        }));
    }


    private void updateRealm(FirebaseUserObject user) {

        DBUser temp = new DBUser(user);

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
