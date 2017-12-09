package org.lirazs.chatty.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.lirazs.chatty.R;
import org.lirazs.chatty.event.AuthenticationEvent;
import org.lirazs.chatty.manager.RealmManager;
import org.lirazs.chatty.model.enums.KeepMedia;
import org.lirazs.chatty.model.enums.LoginMethod;
import org.lirazs.chatty.model.enums.NetworkMode;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.util.ObjectSerializerHelper;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;


/**
 * Created by mac on 1/28/17.
 */
public class AuthService {
    public static String CURRENT_USER_DB_KEY = "CurrentUser";
    public static String ONESIGNALID_DB_KEY = "OneSignalId";

    private Context context;
    private RealmManager realmManager;
    private SharedPreferences sharedPreferences;

    public interface CompletionListener {
        void onComplete(FirebaseUserObject userObject, Exception exception);
    }

    public AuthService(Context context, RealmManager realmManager) {
        this.context = context;
        this.realmManager = realmManager;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isLoggedIn() {
        return getCurrentId() != null;
    }

    public static String getCurrentId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    public FirebaseUserObject getCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            FirebaseUserObject firebaseUserObject = null;

            String userString = sharedPreferences.getString(CURRENT_USER_DB_KEY, null);
            if(userString != null) {
                firebaseUserObject = (FirebaseUserObject) ObjectSerializerHelper.stringToObject(userString);
            }
            return firebaseUserObject;
        }
        return null;
    }

    public void saveLocalIfCurrent(FirebaseUserObject userObject) {
        if(userObject.isCurrent()) {

            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            String userString = ObjectSerializerHelper.objectToString(userObject);

            prefsEditor.putString(CURRENT_USER_DB_KEY, userString);
            prefsEditor.apply();
        }
    }

    public void signInWithEmail(String email, String password, final CompletionListener completion) {
        signInWithEmail(email, password, LoginMethod.EMAIL, completion);
    }

    public void signInWithEmail(String email,
                                String password,
                                final LoginMethod loginMethod,
                                final CompletionListener completion) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    final FirebaseUser firebaseUser = task.getResult().getUser();
                    loadUser(firebaseUser, new CompletionListener() {
                        @Override
                        public void onComplete(FirebaseUserObject userObject, Exception exception) {
                            if(exception == null) { // success
                                onUserLoggedIn(firebaseUser, userObject, loginMethod);

                                if(completion != null) {
                                    completion.onComplete(userObject, null);
                                }
                            } else { // error
                                // only closing FB session without deleting saved user in cache
                                FirebaseAuth.getInstance().signOut();

                                if(completion != null) {
                                    completion.onComplete(null, exception);
                                }
                            }
                        }
                    });

                } else if(completion != null) {
                    completion.onComplete(null, task.getException());
                }
            }
        });
    }

    public void createUserWithEmail(final String email,
                                    String password, final CompletionListener completion) {
        createUserWithEmail(email, password, LoginMethod.EMAIL, completion);
    }

    public void createUserWithEmail(final String email,
                                    String password,
                                    final LoginMethod loginMethod,
                                    final CompletionListener completion) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    final FirebaseUser firebaseUser = task.getResult().getUser();
                    createUser(firebaseUser.getUid(), email, new CompletionListener() {
                        @Override
                        public void onComplete(FirebaseUserObject userObject, Exception exception) {
                            if(exception == null) { // success
                                onUserLoggedIn(firebaseUser, userObject, loginMethod);

                                if(completion != null) {
                                    completion.onComplete(userObject, null);
                                }
                            } else { // error

                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            // only closing FB session without deleting saved user in cache
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    }
                                });

                                if(completion != null) {
                                    completion.onComplete(null, exception);
                                }
                            }
                        }
                    });

                } else if(completion != null) {
                    completion.onComplete(null, task.getException());
                }
            }
        });
    }


    public void signInWithPhone(String verificationId, String code, final CompletionListener completion) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhone(credential, completion);
    }
    public void signInWithPhone(PhoneAuthCredential credential, final CompletionListener completion) {
        signInWithCredential(credential, LoginMethod.PHONE, completion);
    }

    public void signInWithFacebook(AccessToken token, final CompletionListener completion) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithCredential(credential, LoginMethod.FACEBOOK, completion);
    }


    private void signInWithCredential(AuthCredential credential,
                                      final LoginMethod loginMethod, final CompletionListener completion) {

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    final FirebaseUser firebaseUser = task.getResult().getUser();
                    loadUser(firebaseUser, new CompletionListener() {
                        @Override
                        public void onComplete(FirebaseUserObject userObject, Exception exception) {
                            if(exception == null) { // success
                                onUserLoggedIn(firebaseUser, userObject, loginMethod);

                                if(completion != null) {
                                    completion.onComplete(userObject, null);
                                }
                            } else { // error

                                // only closing FB session without deleting saved user in cache
                                FirebaseAuth.getInstance().signOut();

                                if(completion != null) {
                                    completion.onComplete(null, exception);
                                }
                            }
                        }
                    });

                } else if(completion != null) {
                    completion.onComplete(null, task.getException());
                }
            }
        });
    }


    private void onUserLoggedIn(FirebaseUser firebaseUser, FirebaseUserObject loggedInUser, LoginMethod loginMethod) {
        updateUserSettings(firebaseUser, loggedInUser, loginMethod);
        updateOneSignalId();

        //TODO:
        //UpdateLastActive();

        EventBus.getDefault().post(new AuthenticationEvent(
                AuthenticationEvent.Type.USER_LOGGED_IN_SUCCESS));
    }


    private void updateUserSettings(FirebaseUser firebaseUser, FirebaseUserObject loggedInUser, LoginMethod loginMethod) {
        boolean update = false;

        if(loggedInUser.getLoginMethod() == null) {
            update = true;
            loggedInUser.setLoginMethod(loginMethod);
        }
        if(loggedInUser.getKeepMedia() == null) {
            update = true;
            loggedInUser.setKeepMedia(KeepMedia.FOREVER);
        }
        if(loggedInUser.getNetworkImage() == null) {
            update = true;
            loggedInUser.setNetworkImage(NetworkMode.ALL);
        }
        if(loggedInUser.getNetworkVideo() == null) {
            update = true;
            loggedInUser.setNetworkVideo(NetworkMode.ALL);
        }
        if(loggedInUser.getNetworkAudio() == null) {
            update = true;
            loggedInUser.setNetworkAudio(NetworkMode.ALL);
        }
        if(loggedInUser.isAutoSaveMedia() == null) {
            update = true;
            loggedInUser.setAutoSaveMedia(false);
        }

        if(loggedInUser.getLastActive() == null) {
            update = true;
            loggedInUser.setLastActive(new Date());
        }
        if(loggedInUser.getLastTerminate() == null) {
            update = true;
            loggedInUser.setLastTerminate(null);
        }

        if(loginMethod == LoginMethod.FACEBOOK) {
            // find the Facebook profile and get the user's id
            for(UserInfo profile : firebaseUser.getProviderData()) {
                // check if the provider id matches "facebook.com"
                if(profile.getProviderId().equals(context.getString(R.string.facebook_provider_id))) {
                    String facebookUserId = profile.getUid();
                    update = true;

                    // For field 'picture': type must be one of the following values: small, normal, album, large, square
                    String pictureUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?type=normal";
                    String thumbnailUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?type=small";

                    loggedInUser.setPicture(pictureUrl);
                    loggedInUser.setThumbnail(thumbnailUrl);

                    loggedInUser.setFullName(profile.getDisplayName());
                    loggedInUser.setEmail(profile.getEmail());
                    loggedInUser.setPhone(profile.getPhoneNumber());
                    break;
                }
            }
        }

        if(update) {
            saveLocalIfCurrent(loggedInUser);
            loggedInUser.saveInBackground();
        }
    }


    public boolean logOut() {
        boolean success = false;

        try {
            resignOneSignalId();

            //TODO:
            //UpdateLastTerminate(NO);

            FirebaseAuth.getInstance().signOut();

            //TODO: Support for google signin - currently only facebook
            /*if ([[FUser loginMethod] isEqualToString:LOGIN_GOOGLE])
                [[GIDSignIn sharedInstance] signOut];*/

            // clean the facebook authentication - if exists
            Profile facebookProfile = Profile.getCurrentProfile();
            if(facebookProfile != null) {
                LoginManager.getInstance().logOut();
            }

            //TODO: Cache manager?
            //[CacheManager cleanupManual];
            realmManager.cleanupDatabase();

            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            prefsEditor.remove(CURRENT_USER_DB_KEY);
            prefsEditor.apply();

            EventBus.getDefault().post(new AuthenticationEvent(AuthenticationEvent.Type.USER_LOGGED_OUT_SUCCESS));

            success = true;

        } catch (Exception e) {
            e.printStackTrace();

            EventBus.getDefault().post(new AuthenticationEvent(AuthenticationEvent.Type.USER_LOGGED_OUT_ERROR));
        }
        return success;
    }


    private void loadUser(final FirebaseUser user, final CompletionListener completion) {
        final FirebaseUserObject userObject = new FirebaseUserObject(user.getUid());

        saveLocalIfCurrent(userObject);
        userObject.fetchInBackground(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null) {
                    createUser(user.getUid(), user.getEmail(), completion);
                }
                else if(completion != null){
                    completion.onComplete(userObject, null);
                }
            }
        });
    }


    private void createUser(String uid, String email, final CompletionListener completion) {
        final FirebaseUserObject userObject = new FirebaseUserObject(uid);

        if(email != null) {
            userObject.set(FirebaseUserObject.USER_EMAIL_KEY, email);
        }

        saveLocalIfCurrent(userObject);
        userObject.saveInBackground(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    if(completion != null) {
                        completion.onComplete(userObject, null);
                    }

                } else if(completion != null) {
                    completion.onComplete(null, databaseError.toException());
                }
            }
        });
    }



    private void updateOneSignalId() {
        if(getCurrentId() != null) {
            if(sharedPreferences.contains(ONESIGNALID_DB_KEY)) {
                assignOneSignalId();
            } else {
                resignOneSignalId();
            }
        }
    }


    private void assignOneSignalId() {
        FirebaseUserObject currentUser = getCurrentUser();
        String oneSignalId = sharedPreferences.getString(ONESIGNALID_DB_KEY, null);

        if(currentUser.getOneSignalId() == null || !currentUser.getOneSignalId().equals(oneSignalId)) {

            currentUser.setOneSignalId(oneSignalId);
            currentUser.saveInBackground();
        }
    }


    private void resignOneSignalId() {

        FirebaseUserObject currentUser = getCurrentUser();
        if(currentUser != null &&
                currentUser.getOneSignalId() != null && !currentUser.getOneSignalId().isEmpty()) {

            currentUser.setOneSignalId("");
            currentUser.saveInBackground();
        }
    }
}
