package org.lirazs.chatty.service;

import android.content.Context;

import org.lirazs.chatty.event.ApplicationEvent;
import org.lirazs.chatty.event.ApplicationEventListener;
import org.lirazs.chatty.event.AuthenticationEvent;
import org.lirazs.chatty.event.AuthenticationEventListener;
import org.lirazs.chatty.model.firebase.FirebaseUserStatusObject;
import org.lirazs.chatty.model.realm.DBUserStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collection;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by mac on 2/17/17.
 */

public class UserStatusesService implements ApplicationEventListener, AuthenticationEventListener {

    private DatabaseReference databaseReference;

    private Context context;
    private AuthService authService;

    private ValueEventListener valueEventListener;

    public UserStatusesService(Context context, AuthService authService) {
        this.context = context;
        this.authService = authService;

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

    private void initObservers() {
        if(authService.isLoggedIn()) {

            if(databaseReference == null) {
                createObservers();
            }
        }
    }


    private void createObservers() {

        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseUserStatusObject.USERSTATUS_PATH);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map value = (Map) dataSnapshot.getValue();
                    Collection<Map<String, Object>> values = value.values();

                    for (Map<String, Object> properties : values) {
                        FirebaseUserStatusObject firebaseUserStatusObject = new FirebaseUserStatusObject(properties);
                        updateRealm(firebaseUserStatusObject);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }


    private void updateRealm(FirebaseUserStatusObject userStatusObject) {

        DBUserStatus temp = new DBUserStatus(userStatusObject);

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

        databaseReference.removeEventListener(valueEventListener);
        databaseReference = null;
    }
}
