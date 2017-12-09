package org.lirazs.chatty.service;

import android.Manifest;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;

import org.lirazs.chatty.event.ApplicationEvent;
import org.lirazs.chatty.event.ApplicationEventListener;
import org.lirazs.chatty.event.AuthenticationEvent;
import org.lirazs.chatty.event.AuthenticationEventListener;
import org.lirazs.chatty.event.service.ContactsServiceEvent;
import org.lirazs.chatty.facebook.GraphAPICall;
import org.lirazs.chatty.facebook.GraphAPICallback;
import org.lirazs.chatty.model.realm.DBContact;
import org.lirazs.chatty.model.realm.DBUser;
import com.facebook.FacebookRequestError;
import com.facebook.GraphResponse;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by mac on 2/17/17.
 */

public class ContactsService implements ApplicationEventListener, AuthenticationEventListener {

    private Context context;
    private AuthService authService;

    public ContactsService(Context context, AuthService authService) {
        this.context = context;
        this.authService = authService;

        EventBus.getDefault().register(this);

        if(authService.isLoggedIn()) {
            requestAccess();
            loadContactsFromFacebook();
        }
    }


    @Subscribe
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        if(applicationEvent.getEventType() == ApplicationEvent.ApplicationEventType.APPLICATION_STARTED) {
            requestAccess();
        }
    }

    @Subscribe
    public void onAuthenticationEvent(AuthenticationEvent authenticationEvent) {

        if(authenticationEvent.getEventType() == AuthenticationEvent.Type.USER_LOGGED_IN_SUCCESS) {
            requestAccess();
            loadContactsFromFacebook();
        }
    }

    private void loadContactsFromFacebook() {
        // getting contacts from the facebook friends list
        GraphAPICall apiCall = GraphAPICall.callMeFriends("id,name,first_name,last_name,email", new GraphAPICallback() {
            @Override
            public void handleResponse(GraphResponse response) {
                JSONArray friendsData = GraphAPICall.getDataFromResponse(response);

                if (friendsData != null) {
                    for (int i = 0; i < friendsData.length(); i++) {
                        JSONObject friend = friendsData.optJSONObject(i);

                        DBContact dbContact = new DBContact();
                        dbContact.setObjectId(friend.optString("id"));
                        dbContact.setFullName(friend.optString("name"));
                        dbContact.setFirstName(friend.optString("first_name"));
                        dbContact.setLastName(friend.optString("last_name"));
                        dbContact.setEmail(friend.optString("email"));

                        dbContact.setDeleted(false);

                        // save into DB
                        updateRealm(dbContact);
                    }
                }
            }

            @Override
            public void handleError(FacebookRequestError error) {
            }
        });
        if (apiCall != null) {
            apiCall.executeAsync();
        }
    }

    public RealmResults<DBUser> findUsersFromContacts() {
        Realm realm = Realm.getDefaultInstance();

        ArrayList<String> contactsPhoneNumbers = new ArrayList<>();
        ArrayList<String> contactsEmails = new ArrayList<>();
        ArrayList<String> contactsFullNames = new ArrayList<>();

        RealmResults<DBContact> contacts = realm.where(DBContact.class).equalTo(DBContact.CONTACT_IS_DELETED, false)
                .findAll();

        for (DBContact dbContact : contacts) {
            if(dbContact.getHomePhone() != null) {
                contactsPhoneNumbers.add(dbContact.getHomePhone());
            }
            if(dbContact.getMobilePhone() != null) {
                contactsPhoneNumbers.add(dbContact.getMobilePhone());
            }
            if(dbContact.getWorkPhone() != null) {
                contactsPhoneNumbers.add(dbContact.getWorkPhone());
            }
            if(dbContact.getEmail() != null) {
                contactsEmails.add(dbContact.getEmail());
            }
            if(dbContact.getFullName() != null) {
                contactsFullNames.add(dbContact.getFullName());
            }
        }

        String[] phoneNumbers = contactsPhoneNumbers.toArray(new String[contactsPhoneNumbers.size()]);
        String[] emails = contactsEmails.toArray(new String[contactsEmails.size()]);
        String[] fullNames = contactsFullNames.toArray(new String[contactsFullNames.size()]);

        // look for users that have these phone numbers - or emails - or names
        RealmQuery<DBUser> query = realm.where(DBUser.class)
                .notEqualTo(DBUser.USER_OBJECTID, AuthService.getCurrentId());

        if(phoneNumbers.length > 0) {
            query.in(DBUser.USER_PHONE, phoneNumbers);
        }
        if(emails.length > 0) {
            if(phoneNumbers.length > 0) {
                query.or();
            }
            query.in(DBUser.USER_EMAIL, emails);
        }
        if(fullNames.length > 0) {
            if(phoneNumbers.length > 0 || emails.length > 0) {
                query.or();
            }
            query.in(DBUser.USER_FULL_NAME, fullNames);
        }

        RealmResults<DBUser> users = query.findAllSorted(DBUser.USER_FULL_NAME, Sort.ASCENDING);
        realm.close();

        return users;
    }

    private void requestAccess() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                loadContacts();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };

        // Try to get "READ_CONTACTS" permission from user
        new TedPermission(context)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_CONTACTS)
                .check();
    }

    private void loadContacts() {
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient providerClient = resolver.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);

        List<String> contactIdsInDevice = new ArrayList<>();

        // saving contacts into DB from device
        if (providerClient != null) {
            try {
                Cursor cursor = providerClient.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();

                    while (cursor.moveToNext()) {
                        DBContact dbContact = new DBContact();

                        String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        if (contactId != null) {
                            dbContact.setObjectId(contactId);
                            contactIdsInDevice.add(contactId);

                            // fill all the phone numbers of contact into contactDb object
                            fillContactPhoneNumbers(dbContact, contactId);
                            // get email contact address
                            fillContactEmail(dbContact, contactId);
                        }

                        if(displayName != null) {
                            // if display name can be split to first/last name
                            if(displayName.contains(" ")) {
                                String[] strings = displayName.split(" ");
                                dbContact.setFirstName(strings[0]);
                                dbContact.setLastName(strings[1]);
                            }
                            dbContact.setFullName(displayName);
                        }

                        dbContact.setDeleted(false);

                        // save into DB
                        updateRealm(dbContact);
                    }
                    cursor.close();
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                providerClient.release();
            }
        }

        // remove all unused contacts from DB
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DBContact> contactsInDb = realm.where(DBContact.class).findAll();

        for (DBContact dbContact : contactsInDb) {
            // contact is not used anymore - dispose it from DB
            if(!contactIdsInDevice.contains(dbContact.getObjectId())) {
                deleteContact(dbContact);
            }
        }
        realm.close();

        // dispatch update event
        EventBus.getDefault().post(new ContactsServiceEvent(ContactsServiceEvent.Type.CONTACTS_UPDATED));
    }

    private void fillContactPhoneNumbers(DBContact dbContact, String contactId) {
        ContentResolver resolver = context.getContentResolver();

        Cursor phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

        if (phones != null) {
            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        dbContact.setHomePhone(number);
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        dbContact.setMobilePhone(number);
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        dbContact.setWorkPhone(number);
                        break;
                }
            }
            phones.close();
        }
    }

    private void fillContactEmail(DBContact dbContact, String contactId) {
        ContentResolver resolver = context.getContentResolver();

        Cursor emails = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);

        if (emails != null) {
            while (emails.moveToNext()) {
                String emailsString = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                int type = emails.getInt(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                switch (type) {
                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                    case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                    case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                        dbContact.setEmail(emailsString);
                        break;
                }
            }
            emails.close();
        }
    }

    private void deleteContact(DBContact dbContact) {
        Realm realm = Realm.getDefaultInstance();

        try {
            realm.beginTransaction();
            dbContact.setDeleted(true);
            realm.commitTransaction();
        } finally {
            realm.close();
        }
    }


    private void updateRealm(DBContact dbContact) {

        Realm realm = Realm.getDefaultInstance();

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(dbContact);
            realm.commitTransaction();
        } finally {
            realm.close();
        }
    }
}
