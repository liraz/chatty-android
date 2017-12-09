package org.lirazs.chatty.activity.auth;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.lirazs.chatty.R;
import org.lirazs.chatty.activity.BaseActivity;
import org.lirazs.chatty.activity.RecentChatsActivity;
import org.lirazs.chatty.facebook.FacebookLoginPermission;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.service.AuthService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;


public class FacebookLoginActivity extends BaseActivity {

    private static final String TAG = "FacebookLogin";

    @Inject
    public AuthService authService;

    @BindView(R.id.button_facebook_login)
    public FancyButton loginButton;

    @BindView(R.id.terms_and_policy_txt)
    public TextView termsAndPolicyTxt;

    @BindView(R.id.login_txt)
    public TextView loginTxt;

    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        if(authService.isLoggedIn()) { // already logged in
            moveToRecentChatsPage();
            return;
        }

        setContentView(R.layout.activity_facebook);
        injectViews();

        termsAndPolicyTxt.setText(Html.fromHtml("By signing up, you agree to our <u><b>terms</b></u> &amp; <u><b>privacy policy</b></u>"));
        loginTxt.setText(Html.fromHtml("Already have an account? <b>Log in</b>."));

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback< LoginResult >() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                    }
                }
        );
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(
                        FacebookLoginActivity.this,
                        Arrays.asList(
                                FacebookLoginPermission.EMAIL.toString(),
                                FacebookLoginPermission.PUBLIC_PROFILE.toString(),
                                FacebookLoginPermission.USER_FRIENDS.toString())
                );
            }
        });

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+

                // update the login button text if the device already holds a facebook account
                AccountManager manager = AccountManager.get(FacebookLoginActivity.this.getApplicationContext());
                if(manager != null) {
                    Account[] accounts = manager.getAccountsByType("com.facebook.auth.login");

                    if(accounts!= null) {
                        for (Account account : accounts) {
                            Log.e(account.name, account.type);

                            if (emailPattern.matcher(account.name).matches()) {
                                loginButton.setText("Continue as " + account.name);
                            }
                        }
                    }
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };

        // Try to get "GET_ACCOUNTS" permission from user
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.GET_ACCOUNTS)
                .check();
    }

    @OnClick(R.id.button_signup)
    public void onSignUpClick() {
        startActivity(new Intent(this, EmailAndPhoneSignupActivity.class));
    }

    @OnClick(R.id.login_txt)
    public void onLoginClick() {
        startActivity(new Intent(this, EmailAndPhoneLoginActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        authService.signInWithFacebook(token, new AuthService.CompletionListener() {
            @Override
            public void onComplete(FirebaseUserObject userObject, Exception exception) {
                hideProgressDialog();

                if(exception != null) {
                    Toast.makeText(FacebookLoginActivity.this, exception.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    moveToRecentChatsPage();
                }
            }
        });
    }

    private void moveToRecentChatsPage() {
        startActivity(new Intent(FacebookLoginActivity.this, RecentChatsActivity.class));
        finish();
    }
}
