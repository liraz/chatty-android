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

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import org.lirazs.chatty.R;
import org.lirazs.chatty.activity.BaseActivity;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.view.ImageViewUtil;
import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EmailAndPhoneSignupPictureActivity extends BaseActivity {

    private static final String TAG = "EmailPassword";

    public static int USER_PICTURE_URI_REQUEST = 1;
    public static String USER_PICTURE_URI_EXTRA = "userPictureUri";

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;

    @Inject
    public AuthService authService;

    @BindView(R.id.profile_picture_container)
    public RelativeLayout profilePictureContainer;

    @BindView(R.id.profile_image)
    public CircleImageView profilePicture;

    @BindView(R.id.add_picture_button)
    public Button addPictureButton;

    @BindView(R.id.skip_text)
    public TextView skipText;

    private File filePathImageCamera;
    private Uri userPictureUriResult = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_email_phone_signup_picture);
        injectViews();

        profilePictureContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelectorDialog();
            }
        });

        addPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userPictureUriResult == null) {
                    openImageSelectorDialog();
                } else { // submit the picture uri result
                    Intent data = new Intent();
                    data.putExtra(USER_PICTURE_URI_EXTRA, userPictureUriResult);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private void openImageSelectorDialog() {
        new MaterialDialog.Builder(EmailAndPhoneSignupPictureActivity.this)
                .items(Arrays.asList("From camera", "From gallery"))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0: // opening the camera
                                onCameraClick();
                                break;
                            case 1: // taking from gallery
                                onGalleryClick();
                                break;
                        }
                    }
                })
                .show();
    }

    private void onCameraClick() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                String pictureName = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();

                filePathImageCamera = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        pictureName + "camera.jpg");

                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                it.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(EmailAndPhoneSignupPictureActivity.this,
                        getApplicationContext().getPackageName() + ".provider", filePathImageCamera));
                startActivityForResult(it, IMAGE_CAMERA_REQUEST);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };

        // Try to get "CAMERA" permission from user
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void onGalleryClick() {
        ImageViewUtil.openImageChooser(this, IMAGE_GALLERY_REQUEST);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_GALLERY_REQUEST){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    userPictureUriResult = selectedImageUri;
                }
            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST){
            if (resultCode == RESULT_OK){
                if (filePathImageCamera != null && filePathImageCamera.exists()){

                    userPictureUriResult = FileProvider.getUriForFile(EmailAndPhoneSignupPictureActivity.this,
                            getApplicationContext().getPackageName() + ".provider", filePathImageCamera);
                }
            }
        }

        if(userPictureUriResult != null) {
            addPictureButton.setText("Use this Photo");

            profilePicture.setVisibility(View.VISIBLE);
            Glide.with(this).load(userPictureUriResult).into(profilePicture);
        } else {
            profilePicture.setVisibility(View.GONE);
            addPictureButton.setText("Add a Photo");
        }
    }
}
