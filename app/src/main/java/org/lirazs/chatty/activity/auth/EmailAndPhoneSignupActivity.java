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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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

import org.lirazs.chatty.R;
import org.lirazs.chatty.activity.BaseActivity;
import org.lirazs.chatty.model.enums.UserVerificationType;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.util.ImageUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.heetch.countrypicker.Country;
import com.heetch.countrypicker.CountryPickerCallbacks;
import com.heetch.countrypicker.CountryPickerDialog;
import com.heetch.countrypicker.Utils;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class EmailAndPhoneSignupActivity extends BaseActivity {

    private static final String TAG = "EmailPassword";

    public static String SELECTED_TOGGLE_ID_EXTRA = "selectedToggleId";

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

    @BindView(R.id.phone_name_text)
    public EditText phoneNameField;

    @BindView(R.id.phone_text)
    public EditText phoneField;

    @BindView(R.id.email_text)
    public EditText emailField;

    @BindView(R.id.full_name_text)
    public EditText fullNameField;

    @BindView(R.id.password_text)
    public EditText passwordField;

    @BindView(R.id.create_account_button)
    public Button createAccountButton;

    @BindView(R.id.login_txt)
    public TextView loginTxt;

    // israel is default for now
    private Country selectedPhoneCountry = new Country("US", "+1");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_email_phone_signup);
        injectViews();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // if user is waiting for verification - don't let him access the sign up page
        // and transfer the user to the verification page.
        // relevant only for email - phone is not creating a user until we verify the code sent from SMS
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isEmailVerified()) {

            Intent intent = new Intent(this, EmailAndPhoneVerificationActivity.class);
            intent.putExtra(EmailAndPhoneVerificationActivity.VERIFICATION_TYPE_EXTRA, UserVerificationType.EMAIL);
            intent.putExtra(EmailAndPhoneVerificationActivity.VERIFICATION_INPUT_EXTRA, user.getEmail());
            startActivity(intent);
            return;
        }

        loginTxt.setText(Html.fromHtml("Already have an account? <b>Log in</b>."));

        toggleGroup.check(R.id.phone_btn);

        phoneButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    emailFieldsContainer.setVisibility(View.GONE);
                    phoneFieldsContainer.setVisibility(View.VISIBLE);

                    phoneButton.setTextColor(getResources().getColor(R.color.white));
                    emailButton.setTextColor(getResources().getColor(R.color.colorPrimary));

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

                    emailButton.setTextColor(getResources().getColor(R.color.white));
                    phoneButton.setTextColor(getResources().getColor(R.color.colorPrimary));

                    buttonView.post(new Runnable() {
                        @Override
                        public void run() {
                            phoneButton.setChecked(false);
                        }
                    });
                }
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailButton.isChecked() && validateEmailForm()) {
                    String email = emailField.getText().toString();
                    String password = passwordField.getText().toString();
                    String fullName = fullNameField.getText().toString();

                    // with email - we start the signup immediately - then waiting for the user to verify his email
                    createEmailAccount(email, password, fullName);

                } else if (phoneButton.isChecked() && validatePhoneForm()) {
                    // try and get a picture from the user
                    moveToSignupPicturePage();
                }
            }
        });

        phoneAreaCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(EmailAndPhoneSignupActivity.this, new CountryPickerCallbacks() {
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
    }

    @OnClick(R.id.login_txt)
    public void onLoginClick() {
        startActivity(new Intent(this, EmailAndPhoneLoginActivity.class));
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

    private void createEmailAccount(String email, String password, final String fullName) {
        Log.d(TAG, "createEmailAccount:" + email);

        showProgressDialog();

        authService.createUserWithEmail(email, password, new AuthService.CompletionListener() {
            @Override
            public void onComplete(FirebaseUserObject userObject, Exception exception) {
                if(exception != null) { // exception
                    Toast toast = Toast.makeText(EmailAndPhoneSignupActivity.this,
                            exception.getMessage(), Toast.LENGTH_LONG);
                    toast.show();

                } else if(userObject != null) { // success
                    userObject.setFullName(fullName);
                    authService.saveLocalIfCurrent(userObject);
                    userObject.saveInBackground();

                    // try and get a picture from the user
                    moveToSignupPicturePage();
                }
                hideProgressDialog();
            }
        });
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

        String fullName = fullNameField.getText().toString();
        if (TextUtils.isEmpty(fullName)) {
            fullNameField.setError("Required.");
            valid = false;
        } else {
            fullNameField.setError(null);
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

    private boolean validatePhoneForm() {
        boolean valid = true;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        String phoneName = phoneNameField.getText().toString();
        if (TextUtils.isEmpty(phoneName)) {
            phoneNameField.setError("Required.");
            valid = false;
        } else {
            phoneNameField.setError(null);
        }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SELECTED_TOGGLE_ID_EXTRA, toggleGroup.getCheckedRadioButtonId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int checkedRadioButtonId = savedInstanceState.getInt(SELECTED_TOGGLE_ID_EXTRA);
        toggleGroup.check(checkedRadioButtonId);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EmailAndPhoneSignupPictureActivity.USER_PICTURE_URI_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri userPictureUri = data.getParcelableExtra(EmailAndPhoneSignupPictureActivity.USER_PICTURE_URI_EXTRA);

                Intent intent = new Intent(this, EmailAndPhoneVerificationActivity.class);

                if(phoneButton.isChecked()) {
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    Phonenumber.PhoneNumber phoneNumber = null;

                    try {
                        phoneNumber = phoneUtil.parse(phoneField.getText().toString(), selectedPhoneCountry.getIsoCode());
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }

                    if (phoneNumber != null) {
                        String phone = "+" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber();
                        String fullName = phoneNameField.getText().toString();

                        intent.putExtra(EmailAndPhoneVerificationActivity.VERIFICATION_TYPE_EXTRA, UserVerificationType.SMS);
                        intent.putExtra(EmailAndPhoneVerificationActivity.VERIFICATION_INPUT_EXTRA, phone);
                        intent.putExtra(EmailAndPhoneVerificationActivity.USER_FULL_NAME_EXTRA, fullName);
                        intent.putExtra(EmailAndPhoneVerificationActivity.USER_PICTURE_URI_EXTRA, userPictureUri);
                    }

                } else if(emailButton.isChecked()) {
                    String email = emailField.getText().toString();

                    // saving the profile picture for the email user
                    if (userPictureUri != null) {
                        ImageUtils.uploadImage(this, userPictureUri, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String link = taskSnapshot.getDownloadUrl().toString();

                                FirebaseUserObject currentUser = authService.getCurrentUser();
                                currentUser.setPicture(link);
                                currentUser.setThumbnail(link);

                                authService.saveLocalIfCurrent(currentUser);
                                currentUser.saveInBackground();
                            }
                        });
                    }

                    intent.putExtra(EmailAndPhoneVerificationActivity.VERIFICATION_TYPE_EXTRA, UserVerificationType.EMAIL);
                    intent.putExtra(EmailAndPhoneVerificationActivity.VERIFICATION_INPUT_EXTRA, email);
                }

                startActivity(intent);
            }
        }
    }

    private void moveToSignupPicturePage() {
        startActivityForResult(new Intent(this, EmailAndPhoneSignupPictureActivity.class),
                EmailAndPhoneSignupPictureActivity.USER_PICTURE_URI_REQUEST);
    }
}
