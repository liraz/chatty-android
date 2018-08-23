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

package org.lirazs.chatty.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.lirazs.chatty.R;
import org.lirazs.chatty.model.chatkit.DBUserDialogDecorator;
import org.lirazs.chatty.model.firebase.FirebaseGroupObject;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.model.request.ChatRequest;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.service.ChatService;
import org.lirazs.chatty.service.UsersService;
import org.lirazs.chatty.util.FileUtils;
import org.lirazs.chatty.view.AvatarViewUtil;
import org.lirazs.chatty.view.CustomDialogViewHolder;
import org.lirazs.chatty.view.ImageViewUtil;
import com.bumptech.glide.Glide;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.hdodenhof.circleimageview.CircleImageView;


public class NewGroupDetailsActivity extends BaseActivity {
    public static String USER_IDS_KEY = "USER_IDS_KEY";

    private static final int IMAGE_GALLERY_REQUEST = 1;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.dialogs_list)
    public DialogsList dialogsList;

    @BindView(R.id.camera_icon)
    public FloatingActionButton cameraIcon;

    @BindView(R.id.letter_icon)
    public MaterialLetterIcon letterIcon;

    @BindView(R.id.image_icon)
    public CircleImageView imageIcon;

    @BindView(R.id.name)
    public EditText nameText;

    @BindView(R.id.members_count)
    public TextView membersCount;

    @Inject
    public UsersService usersService;

    @Inject
    public ChatService chatService;

    private ImageLoader imageLoader;
    private DialogsListAdapter<DBUserDialogDecorator> dialogsAdapter;

    private MenuItem saveItem;

    private List<String> userIds;
    private String linkPicture = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_new_group_details);
        injectViews();

        userIds = getIntent().getStringArrayListExtra(USER_IDS_KEY);
        if(userIds == null) {
            userIds = savedInstanceState.getStringArrayList(USER_IDS_KEY);
        }
        // something went wrong - finish the activity..
        if(userIds == null) {
            finish();
            return;
        }

        // setting the support toolbar
        toolbar.setTitle("New Group Details");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Drawable myFabSrc = getResources().getDrawable(R.drawable.ic_camera_alt_white_24dp);
        Drawable primaryColorTintedDrawable = myFabSrc.getConstantState().newDrawable();
        primaryColorTintedDrawable.mutate().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null), PorterDuff.Mode.SRC_IN);
        cameraIcon.setImageDrawable(primaryColorTintedDrawable);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if (imageView != null && url != null && !url.isEmpty()) {
                    Glide.with(NewGroupDetailsActivity.this).load(url).into(imageView);
                }
            }
        };

        dialogsAdapter = new DialogsListAdapter<>(R.layout.item_custom_dialog, CustomDialogViewHolder.class, imageLoader);
        dialogsList.setAdapter(dialogsAdapter);

        List<DBUser> users = usersService.getUsersByIds(userIds);
        dialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(users));

        membersCount.setText(users.size() + " Members");
    }

    @OnClick(R.id.letter_icon)
    public void onLetterIconClick() {
        onButtonCameraClick();
    }

    @OnClick(R.id.camera_icon)
    public void onButtonCameraClick() {
        ImageViewUtil.openImageChooser(this, IMAGE_GALLERY_REQUEST);
    }

    @OnTextChanged(R.id.name)
    public void onNameTextChange() {
        imageIcon.setVisibility(linkPicture != null ? View.VISIBLE : View.GONE);
        cameraIcon.setVisibility(linkPicture != null ? View.GONE : View.VISIBLE);

        boolean textNotEmpty = nameText.getText() != null && nameText.getText().length() > 0;
        if(textNotEmpty && linkPicture == null) {
            letterIcon.setVisibility(View.VISIBLE);
            cameraIcon.setVisibility(View.GONE);

            AvatarViewUtil.renderInitialsUserAvatar(this, letterIcon, nameText.getText().toString());
        } else {
            letterIcon.setVisibility(View.GONE);
        }

        saveItem.setVisible(textNotEmpty);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_message, menu);

        saveItem = menu.findItem(R.id.action_save);
        Drawable icon = saveItem.getIcon();
        icon.mutate();
        icon.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null), PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                openChatWithGroup();
                return true;

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

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(FileUtils.FIREBASE_STORAGE)
                            .child(FileUtils.generateFilename("group", "jpg"));

                    try {
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        if (bmp != null) {
                            bmp = Bitmap.createScaledBitmap(bmp, 80, 80, true);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);

                            imageIcon.setImageBitmap(bmp);

                            UploadTask uploadTask = storageRef.putBytes(baos.toByteArray());
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            linkPicture = taskSnapshot.getDownloadUrl().toString();
                                            onNameTextChange();
                                        }
                                    });
                        }

                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private void openChatWithGroup() {
        showProgressDialog();

        final FirebaseGroupObject groupObject = new FirebaseGroupObject();
        groupObject.setUserId(AuthService.getCurrentId());
        groupObject.setName(nameText.getText().toString());

        List<String> members = new ArrayList<>(userIds);
        members.add(0, AuthService.getCurrentId());

        groupObject.setMembers(members);
        groupObject.setDeleted(false);

        if(linkPicture != null) {
            groupObject.setPicture(linkPicture);
        }

        groupObject.saveInBackground(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null) {
                    hideProgressDialog();

                    ChatRequest chatRequest = chatService.generateGroupChatRequest(groupObject);

                    // Start intent for chat activity
                    Intent intent = new Intent(NewGroupDetailsActivity.this, ChatActivity.class);
                    intent.putExtra(ChatRequest.class.getCanonicalName(), chatRequest);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(NewGroupDetailsActivity.this, "Network error.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
