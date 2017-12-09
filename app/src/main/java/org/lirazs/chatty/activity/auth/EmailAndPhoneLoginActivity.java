/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lirazs.chatty.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.heetch.countrypicker.Country;
import com.heetch.countrypicker.CountryPickerCallbacks;
import com.heetch.countrypicker.CountryPickerDialog;
import com.heetch.countrypicker.Utils;

import org.json.JSONObject;
import org.lirazs.chatty.R;
import org.lirazs.chatty.activity.BaseActivity;
import org.lirazs.chatty.activity.RecentChatsActivity;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.service.AuthService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class EmailAndPhoneLoginActivity extends BaseActivity {

    private static final String TAG = "EmailPassword";

    @Inject
    public AuthService authService;

    @BindView(R.id.toggleGroup)
    public RadioGroup toggleGroup;

    @BindView(R.id.phone_btn)
    public ToggleButton phoneButton;

    @BindView(R.id.email_btn)
    public ToggleButton emailButton;

    @BindView(R.id.email_fields_container)
    public LinearLayout emailFieldsContainer;

    @BindView(R.id.phone_fields_container)
    public LinearLayout phoneFieldsContainer;

    @BindView(R.id.phone_area_code)
    public LinearLayout phoneAreaCode;

    @BindView(R.id.phone_area_code_text)
    public TextView phoneAreaCodeText;

    @BindView(R.id.phone_area_code_image)
    public ImageView phoneAreaCodeImage;

    @BindView(R.id.phone_text)
    public EditText phoneField;

    @BindView(R.id.email_text)
    public EditText emailField;

    @BindView(R.id.password_text)
    public EditText passwordField;

    @BindView(R.id.login_button)
    public Button loginButton;

    @BindView(R.id.signup_txt)
    public TextView signupText;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationCallbacks;

    // israel is default for now
    private Country selectedPhoneCountry = new Country("IL", "+972");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_email_phone_login);
        injectViews();

        signupText.setText(Html.fromHtml("Don't have an account? <b>Sign up</b>."));

        toggleGroup.check(R.id.phone_btn);

        phoneButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    emailFieldsContainer.setVisibility(View.GONE);
                    phoneFieldsContainer.setVisibility(View.VISIBLE);

                    buttonView.post(new Runnable() {
                        @Override
                        public void run() {
                            emailButton.setChecked(false);
                        }
                    });
                }
            }
        });
        emailButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    emailFieldsContainer.setVisibility(View.VISIBLE);
                    phoneFieldsContainer.setVisibility(View.GONE);

                    buttonView.post(new Runnable() {
                        @Override
                        public void run() {
                            phoneButton.setChecked(false);
                        }
                    });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailButton.isChecked() && validateEmailForm()) {
                    String email = emailField.getText().toString();
                    String password = passwordField.getText().toString();

                    // with email
                    signInWithEmail(email, password);

                } else if (phoneButton.isChecked() && validatePhoneForm()) {
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    Phonenumber.PhoneNumber phoneNumber = null;

                    try {
                        phoneNumber = phoneUtil.parse(phoneField.getText().toString(), selectedPhoneCountry.getIsoCode());
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }

                    if (phoneNumber != null) {
                        String phone = "+" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber();
                        // verify the phone number
                        startPhoneNumberVerification(phone);
                    }
                }
            }
        });

        phoneAreaCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(EmailAndPhoneLoginActivity.this, new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country country, int flagResId) {
                                phoneAreaCodeText.setText(country.getIsoCode() + " +" + country.getDialingCode());
                                phoneAreaCodeImage.setImageResource(flagResId);

                                selectedPhoneCountry = country;
                            }
                        });
                countryPicker.show();
            }
        });
        phoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        updateDefaultCountryForPhone();

        phoneVerificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                showProgressDialog();

                authService.signInWithPhone(credential, new AuthService.CompletionListener() {
                    @Override
                    public void onComplete(final FirebaseUserObject userObject, Exception exception) {
                        if(exception != null) { // exception
                            Toast toast = Toast.makeText(EmailAndPhoneLoginActivity.this,
                                    exception.getMessage(), Toast.LENGTH_LONG);
                            toast.show();

                        } else if(userObject != null) { // success
                            // Move to the recent chats page
                            moveToRecentChatsPage();
                        }
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(EmailAndPhoneLoginActivity.this, "Invalid phone number.",Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(EmailAndPhoneLoginActivity.this,"Quota exceeded.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
            }
        };
    }

    @OnClick(R.id.signup_txt)
    public void onSignupTextClick() {
        onBackPressed();
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

    private void updateDefaultCountryForPhone() {
        JSONObject countriesJSON = Utils.getCountriesJSON(this);
        List<Country> countries = Utils.parseCountries(countriesJSON);

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = manager.getNetworkCountryIso();

        for (Country country : countries) {
            if(country.getIsoCode().toUpperCase().equals(countryIso.toUpperCase())) {
                phoneAreaCodeText.setText(country.getIsoCode() + " +" + country.getDialingCode());
                phoneAreaCodeImage.setImageResource(Utils.getMipmapResId(this,
                        country.getIsoCode().toLowerCase() + "_flag"));

                selectedPhoneCountry = country;
                break;
            }
        }
    }

    private void signInWithEmail(String email, String password) {
        showProgressDialog();

        authService.signInWithEmail(email, password, new AuthService.CompletionListener() {
            @Override
            public void onComplete(FirebaseUserObject userObject, Exception exception) {
                hideProgressDialog();

                if(exception != null) {
                    Toast.makeText(EmailAndPhoneLoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    moveToRecentChatsPage();
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                phoneVerificationCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private boolean validatePhoneForm() {
        boolean valid = true;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        String phone = phoneField.getText().toString();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, selectedPhoneCountry.getIsoCode());

            if (TextUtils.isEmpty(phone)) {
                phoneField.setError("Required.");
                valid = false;
            } else if (!phoneUtil.isValidNumberForRegion(phoneNumber, selectedPhoneCountry.getIsoCode())) {
                phoneField.setError("Invalid phone number.");
                valid = false;
            } else {
                phoneField.setError(null);
            }

        } catch (NumberParseException ignore) {
            phoneField.setError("Invalid phone number.");
            valid = false;
        }

        return valid;
    }

    private boolean validateEmailForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    private void moveToRecentChatsPage() {
        startActivity(new Intent(this, RecentChatsActivity.class));
        finish();
    }
}
