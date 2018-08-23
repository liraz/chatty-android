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

package org.lirazs.chatty.activity.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.lirazs.chatty.R;
import org.lirazs.chatty.activity.BaseActivity;
import org.lirazs.chatty.activity.RecentChatsActivity;
import org.lirazs.chatty.model.enums.UserVerificationType;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.util.ImageUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class EmailAndPhoneVerificationActivity extends BaseActivity {

    private static final String TAG = EmailAndPhoneVerificationActivity.class.getSimpleName();
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public static String VERIFICATION_ID_EXTRA = "verificationId";
    public static String VERIFICATION_TYPE_EXTRA = "verificationType";
    public static String VERIFICATION_INPUT_EXTRA = "verificationInput"; // can be email text or phone text

    public static String USER_FULL_NAME_EXTRA = "userFullName";
    public static String USER_PICTURE_URI_EXTRA = "userPictureUri";

    @Inject
    public AuthService authService;

    @BindView(R.id.verify_status_icon)
    public IconicsImageView verifyStatusIcon;

    @BindView(R.id.verify_status_txt)
    public TextView verifyStatusText;

    @BindView(R.id.verify_description_txt)
    public TextView verifyDescriptionText;

    @BindView(R.id.phone_verify_container)
    public LinearLayout phoneVerifyContainer;

    @BindView(R.id.code_text)
    public EditText codeText;

    @BindView(R.id.verify_code_button)
    public Button verifyCodeButton;

    @BindView(R.id.login_txt)
    public TextView loginText;

    @BindView(R.id.resend_button)
    public Button resendButton;

    @BindView(R.id.next_button)
    public Button nextButton;

    private UserVerificationType verificationType;
    private String verificationInput;

    private String userFullName;
    private Uri userPictureUri;

    private boolean sentVerificationOnStart;

    private String verificationId;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken phoneResendToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_email_phone_verification);
        injectViews();

        verificationType = (UserVerificationType) getIntent().getSerializableExtra(VERIFICATION_TYPE_EXTRA);
        if(verificationType == null && savedInstanceState != null) {
            verificationType = (UserVerificationType) savedInstanceState.getSerializable(VERIFICATION_TYPE_EXTRA);
        }
        verificationInput = getIntent().getStringExtra(VERIFICATION_INPUT_EXTRA);
        if(verificationInput == null && savedInstanceState != null) {
            verificationInput = savedInstanceState.getString(VERIFICATION_INPUT_EXTRA);
        }

        userFullName = getIntent().getStringExtra(USER_FULL_NAME_EXTRA);
        if(userFullName == null && savedInstanceState != null) {
            userFullName = savedInstanceState.getString(USER_FULL_NAME_EXTRA);
        }
        userPictureUri = getIntent().getParcelableExtra(USER_PICTURE_URI_EXTRA);
        if(userPictureUri == null && savedInstanceState != null) {
            userPictureUri = savedInstanceState.getParcelable(USER_PICTURE_URI_EXTRA);
        }

        loginText.setText(Html.fromHtml("Already have an account? <b>Log in</b>."));

        if(verificationType == UserVerificationType.SMS) {
            verifyDescriptionText.setText("SMS message was sent with verification code.");
            resendButton.setText("Resend SMS");
            phoneVerifyContainer.setVisibility(View.VISIBLE);
        } else if(verificationType == UserVerificationType.EMAIL) {
            verifyDescriptionText.setText("Check your inbox for an email to complete your verification.");
            resendButton.setText("Resend Email");
            phoneVerifyContainer.setVisibility(View.GONE);
        }

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        verificationInput, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        EmailAndPhoneVerificationActivity.this, // Activity (for callback binding)
                        phoneVerificationCallbacks, // OnVerificationStateChangedCallbacks
                        phoneResendToken); // ForceResendingToken from callbacks
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(EmailAndPhoneVerificationActivity.this,
                        RecentChatsActivity.class));
            }
        });

        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeText.getText().toString();
                verifyPhoneNumberWithCode(code);
            }
        });

        phoneVerificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                showProgressDialog();
                authService.signInWithPhone(credential, onPhoneSignInComplete);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(EmailAndPhoneVerificationActivity.this, "Invalid phone number.",Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(EmailAndPhoneVerificationActivity.this,"Quota exceeded.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                EmailAndPhoneVerificationActivity.this.verificationId = verificationId;
                phoneResendToken = token;
            }
        };
    }

    @OnClick(R.id.login_txt)
    public void onLoginClick() {
        finish();
        startActivity(new Intent(this, FacebookLoginActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (verificationType == UserVerificationType.EMAIL) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser updatedUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (updatedUser != null && updatedUser.isEmailVerified()) {
                            onVerificationSuccess();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (verificationType == UserVerificationType.SMS) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(SMS_RECEIVED);

            registerReceiver(smsReceiver, filter);// define e broadcast receiver to intercept a sms verification code
        }

        // sending verification automatically only on start
        if (!sentVerificationOnStart) {
            sendVerification();
            sentVerificationOnStart = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (verificationType == UserVerificationType.SMS) {
            unregisterReceiver(smsReceiver);
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                phoneVerificationCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void sendVerification() {
        if (verificationType == UserVerificationType.SMS) {
            startPhoneNumberVerification(verificationInput);

        } else if (verificationType == UserVerificationType.EMAIL) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.sendEmailVerification();
            }
        }
    }

    private void onVerificationSuccess() {
        nextButton.setVisibility(View.VISIBLE);
        resendButton.setVisibility(View.GONE);

        verifyStatusText.setText("SUCCESS!");
        verifyDescriptionText.setText("You are verified! click next to continue.");
        verifyStatusIcon.setIcon(GoogleMaterial.Icon.gmd_done);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(VERIFICATION_TYPE_EXTRA, verificationType);
        outState.putString(VERIFICATION_INPUT_EXTRA, verificationInput);
        outState.putString(VERIFICATION_ID_EXTRA, verificationId);

        outState.putString(USER_FULL_NAME_EXTRA, userFullName);
        outState.putParcelable(USER_PICTURE_URI_EXTRA, userPictureUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        verificationType = (UserVerificationType) savedInstanceState.getSerializable(VERIFICATION_TYPE_EXTRA);
        verificationInput = savedInstanceState.getString(VERIFICATION_INPUT_EXTRA);
        verificationId = savedInstanceState.getString(VERIFICATION_ID_EXTRA);

        userFullName = savedInstanceState.getString(USER_FULL_NAME_EXTRA);
        userPictureUri = savedInstanceState.getParcelable(USER_PICTURE_URI_EXTRA);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Intent recieved: " + intent.getAction());

            if (intent.getAction().equals(SMS_RECEIVED)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[])bundle.get("pdus");
                    if (pdus != null) {
                        final SmsMessage[] messages = new SmsMessage[pdus.length];
                        for (int i = 0; i < pdus.length; i++) {
                            messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        }
                        if (messages.length > -1) {
                            Log.i(TAG, "Message recieved: " + messages[0].getMessageBody());

                            String code = messages[0].getMessageBody();
                            verifyPhoneNumberWithCode(code);
                        }
                    }
                }
            }
        }
    };

    private AuthService.CompletionListener onPhoneSignInComplete = new AuthService.CompletionListener() {
        @Override
        public void onComplete(final FirebaseUserObject userObject, Exception exception) {
            if(exception != null) { // exception
                Toast toast = Toast.makeText(EmailAndPhoneVerificationActivity.this,
                        exception.getMessage(), Toast.LENGTH_LONG);
                toast.show();

            } else if(userObject != null) { // success

                userObject.setPhone(verificationInput);
                userObject.setFullName(userFullName);
                authService.saveLocalIfCurrent(userObject);
                userObject.saveInBackground();

                // Save the full name and the picture URL for this user.
                if (userPictureUri != null) {
                    ImageUtils.uploadImage(EmailAndPhoneVerificationActivity.this,
                            userPictureUri, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String link = taskSnapshot.getDownloadUrl().toString();

                            userObject.setPicture(link);
                            userObject.setThumbnail(link);

                            authService.saveLocalIfCurrent(userObject);
                            userObject.saveInBackground();
                        }
                    });
                }

                // Move to the recent chats page
                moveToRecentChatsPage();
            }
            hideProgressDialog();
        }
    };

    private void verifyPhoneNumberWithCode(String code) {
        showProgressDialog();
        authService.signInWithPhone(verificationId, code, onPhoneSignInComplete);
    }

    private void moveToRecentChatsPage() {
        startActivity(new Intent(this, RecentChatsActivity.class));
        finish();
    }
}
