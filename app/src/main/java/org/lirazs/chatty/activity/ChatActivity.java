package org.lirazs.chatty.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lirazs.chatty.R;
import org.lirazs.chatty.event.service.MessagesServiceEvent;
import org.lirazs.chatty.model.chatkit.DBMessageDecorator;
import org.lirazs.chatty.model.realm.DBGroup;
import org.lirazs.chatty.model.realm.DBMessage;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.model.request.ChatRequest;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.service.GroupsService;
import org.lirazs.chatty.service.MessagesService;
import org.lirazs.chatty.service.RecentService;
import org.lirazs.chatty.service.UsersService;
import org.lirazs.chatty.util.DateUtils;
import org.lirazs.chatty.view.AvatarViewUtil;
import org.lirazs.chatty.view.CustomIncomingTextMessageViewHolder;
import org.lirazs.chatty.view.CustomOutcomingTextMessageViewHolder;
import org.lirazs.chatty.view.ImageViewUtil;
import com.bumptech.glide.Glide;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Liraz on 26/05/2017.
 */

public class ChatActivity extends BaseActivity implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener, DateFormatter.Formatter {

    private static final int TOTAL_MESSAGES_COUNT = 100;

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;

    private static String TYPING_PATH = "Typing";

    @BindView(R.id.contentRoot)
    public View contentRoot;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.messages_list)
    public MessagesList messagesList;

    @BindView(R.id.button_emoji)
    public ImageView buttonEmojiImageView;

    @BindView(R.id.button_keyboard)
    public ImageView buttonKeyboardImageView;

    @BindView(R.id.edit_text_message)
    public EmojiEditText editText;

    @BindView(R.id.button_message)
    public ImageView buttonMessageImageView;

    @BindView(R.id.attach_container)
    public LinearLayout attachContainer;

    @BindView(R.id.title_details_container)
    public LinearLayout titleDetailsContainer;

    @BindView(R.id.typing_indicator)
    public LinearLayout typingIndicator;


    @Inject
    public AuthService authService;

    @Inject
    public UsersService usersService;

    @Inject
    public RecentService recentService;

    @Inject
    public GroupsService groupsService;

    @Inject
    public MessagesService messagesService;

    private EmojiPopup emojiPopup;

    private ChatRequest chatRequest;

    private boolean automaticallyScrollsToMostRecentMessage = true;
    private int typingCounter;
    ChildEventListener typingChildEventListener;

    private DatabaseReference typingDatabaseReference;

    private File filePathImageCamera;

    protected ImageLoader imageLoader;
    protected MessagesListAdapter<DBMessageDecorator> messagesAdapter;

    private int selectionCount;
    private Date lastLoadedDate;

    private Realm realm;
    private RealmResults<DBMessage> messagesRealmResults;
    private RealmChangeListener<RealmResults<DBMessage>> messagesRealmResultsChangeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_chat);
        injectViews();

        chatRequest = (ChatRequest) getIntent().getSerializableExtra(ChatRequest.class.getCanonicalName());
        if(chatRequest == null) {
            chatRequest = (ChatRequest) savedInstanceState.getSerializable(ChatRequest.class.getCanonicalName());
        }
        // something went wrong - finish the activity..
        if(chatRequest == null) {
            finish();
            return;
        }

        // setting the support toolbar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // update page title
        updateTitleDetails();

        //TODO: Support for blocking users
        //isBlocker = (userId != nil) ? [Blocked isBlocker:userId] : NO;

        //TODO: Setting background depends on user setting
        /*FirebaseUserObject currentUser = authService.getCurrentUser();
        if(currentUser.getWallpaper()) {
        }*/

        // reset unread counter to zero
        recentService.clearUnreadCounterInGroupRecents(chatRequest.getGroupId());

        // start listening to message updates on group
        messagesService.startListeningToGroup(chatRequest.getGroupId());

        // setup typing observer
        typingDatabaseReference = FirebaseDatabase.getInstance().getReference(TYPING_PATH).child(chatRequest.getGroupId());
        createTypingObservers();

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if (imageView != null && url != null && !url.isEmpty()) {
                    Glide.with(ChatActivity.this).load(url).into(imageView);
                }
            }
        };

        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message);

        messagesAdapter = new MessagesListAdapter<>(AuthService.getCurrentId(), holdersConfig, imageLoader);

        messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.setDateHeadersFormatter(this);
        messagesList.setAdapter(messagesAdapter);

        // initialize the emoticons keyboard
        emojiPopup = EmojiPopup.Builder.fromRootView(contentRoot).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                buttonKeyboardImageView.setVisibility(View.GONE);
                buttonEmojiImageView.setVisibility(View.VISIBLE);
            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                buttonKeyboardImageView.setVisibility(View.VISIBLE);
                buttonEmojiImageView.setVisibility(View.GONE);
            }
        }).build(editText);


        realm = Realm.getDefaultInstance();

        // get group messages
        messagesRealmResults = messagesService.getGroupMessages();

        messagesAdapter.clear();
        messagesAdapter.addToEnd(DBMessageDecorator.decorateMessageList(messagesRealmResults), true);


        titleDetailsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(chatRequest.isPrivateChat()) {
                    String userId = chatRequest.getUserIdForPrivateChat();

                    Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
                    intent.putExtra(UserProfileActivity.USER_ID_DATA, userId);
                    startActivity(intent);

                } else if(chatRequest.isGroupChat()) {
                    Intent intent = new Intent(ChatActivity.this, GroupProfileActivity.class);
                    intent.putExtra(GroupProfileActivity.GROUP_OBJECT_ID_DATA, chatRequest.getGroupId());
                    startActivity(intent);
                }
            }
        });
    }

    @Subscribe
    public void onMessagesServiceEvent(MessagesServiceEvent messagesServiceEvent) {

        MessagesServiceEvent.Type eventType = messagesServiceEvent.getEventType();
        DBMessage message = messagesServiceEvent.getDbMessage();

        if(eventType == MessagesServiceEvent.Type.MESSAGE_ADDED) {
            messagesAdapter.addToStart(new DBMessageDecorator(message), true);
        }
        else if(eventType == MessagesServiceEvent.Type.MESSAGE_DELETED) {
            messagesAdapter.deleteById(message.getObjectId());
        }
        else if(eventType == MessagesServiceEvent.Type.MESSAGE_UPDATED) {
            messagesAdapter.update(message.getObjectId(), new DBMessageDecorator(message));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;

        //TODO: Menu actions - delete & copy actions
        //menu.findItem(R.id.action_delete).setVisible(count > 0);
        //menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() { //imitation of internet connection
            @Override
            public void run() {
                //TODO: Load more logic
                /*ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
                lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
                messagesAdapter.addToEnd(messages, false);*/
            }
        }, 1000);
    }

    @OnTextChanged(R.id.edit_text_message)
    public void onMessageTextChange() {
        boolean hasText = editText.getText().length() > 0;

        attachContainer.setVisibility(hasText ? View.GONE : View.VISIBLE);
        buttonMessageImageView.setVisibility(hasText ? View.VISIBLE : View.GONE);

        typingIndicatorStart();
    }

    @OnClick(R.id.button_message)
    public void onButtonMessageClick() {

        String text = editText.getText().toString();
        messagesService.sendTextMessage(text);

        editText.setText("");
    }

    @OnClick(R.id.button_attach)
    public void onButtonAttachClick() {
        ImageViewUtil.openImageChooser(this, IMAGE_GALLERY_REQUEST);
    }

    @OnClick(R.id.button_camera)
    public void onButtonCameraClick() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                String pictureName = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();

                filePathImageCamera = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        pictureName + "camera.jpg");

                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                it.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ChatActivity.this, getApplicationContext().getPackageName() + ".provider", filePathImageCamera));
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

    @OnClick(R.id.button_location)
    public void onButtonLocationClick() {

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button_emoji)
    public void onButtonEmojiClick() {
        emojiPopup.toggle();
    }

    @OnClick(R.id.button_keyboard)
    public void onButtonKeyboardClick() {
        if (emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        }
    }

    /*@Override
    public void onClickUserAvatar(View view, int position, DBMessage message) {

        //TODO: Opening the profile view on the selected the user
        *//*NSString *senderId = dbmessage.senderId;
        //---------------------------------------------------------------------------------------------------------------------------------------------
        if ([senderId isEqualToString:[FUser currentId]] == NO)
        {
            ProfileView *profileView = [[ProfileView alloc] initWith:senderId Chat:NO];
		    [self.navigationController pushViewController:profileView animated:YES];
        }*//*
    }*/

    /*@Override
    public void onClickVideoMessage(View view, int position, DBMessage message) {
        //TODO: Playing the video
        *//*VideoMediaItem *mediaItem = (VideoMediaItem *)jsqmessage.media;
        if (mediaItem.status == STATUS_MANUAL)
        {
			[MediaManager loadVideoManual:mediaItem dbmessage:dbmessage collectionView:collectionView];
			[collectionView reloadData];
        }
        if (mediaItem.status == STATUS_SUCCEED)
        {
            VideoView *videoView = [[VideoView alloc] initWith:mediaItem.fileURL];
			[self presentViewController:videoView animated:YES completion:nil];
        }*//*
    }*/

    /*@Override
    public void onClickAudioMessage(View view, int position, DBMessage message) {
        //TODO: Playing the audio
        *//*AudioMediaItem *mediaItem = (AudioMediaItem *)jsqmessage.media;
        if (mediaItem.status == STATUS_MANUAL)
        {
			[MediaManager loadAudioManual:mediaItem dbmessage:dbmessage collectionView:collectionView];
			[collectionView reloadData];
        }*//*
    }*/

    /*@Override
    public void onClickLocationMessage(View view, int position, DBMessage message) {
        Double longitude = message.getLongitude();
        Double latitude = message.getLatitude();

        String uri = String.format("geo:%s,%s?z=17&q=%s,%s", latitude, longitude, latitude, longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        startActivity(intent);
    }*/

    /*@Override
    public void onClickPictureMessage(View view, int position, DBMessage message) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);

        intent.putExtra("nameUser", message.getSenderName());
        intent.putExtra("urlPhotoUser", message.getSenderThumbnail());
        intent.putExtra("urlPhotoClick", message.getPicture());

        startActivity(intent);
    }*/

    /*@Override
    public void onClickMessage(View view, int position, DBMessage message) {
        //TODO: Should do something?
    }*/

    //TODO: Support for loading messages on demand - instead of all messages (there might be thousands of messages)
    //TODO: - check ChatKit library for implementation
    /*- (void)insertMessages
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        insertCounter += INSERT_MESSAGES;
        //---------------------------------------------------------------------------------------------------------------------------------------------
	    [self refreshCollectionView2];
    }*/

    //TODO: Action to copy the message content?
    /*- (void)actionCopy:(NSIndexPath *)indexPath
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        DBMessage *dbmessage = [self dbmessage:indexPath];
	    [[UIPasteboard generalPasteboard] setString:dbmessage.text];
    }*/

    //TODO: Option to save the selected message - video, picture, audio
    /*- (void)actionSave:(NSIndexPath *)indexPath
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        DBMessage *dbmessage = [self dbmessage:indexPath];
        JSQMessage *jsqmessage = [self jsqmessage:indexPath];
        //---------------------------------------------------------------------------------------------------------------------------------------------
        if ([dbmessage.type isEqualToString:MESSAGE_PICTURE])
        {
            PhotoMediaItem *mediaItem = (PhotoMediaItem *)jsqmessage.media;
            if (mediaItem.status == STATUS_SUCCEED)
                UIImageWriteToSavedPhotosAlbum(mediaItem.image, self, @selector(video:didFinishSavingWithError:contextInfo:), nil);
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------
        if ([dbmessage.type isEqualToString:MESSAGE_VIDEO])
        {
            VideoMediaItem *mediaItem = (VideoMediaItem *)jsqmessage.media;
            if (mediaItem.status == STATUS_SUCCEED)
                UISaveVideoAtPathToSavedPhotosAlbum(mediaItem.fileURL.path, self, @selector(video:didFinishSavingWithError:contextInfo:), nil);
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------
        if ([dbmessage.type isEqualToString:MESSAGE_AUDIO])
        {
            AudioMediaItem *mediaItem = (AudioMediaItem *)jsqmessage.media;
            if (mediaItem.status == STATUS_SUCCEED)
            {
                NSString *path = [File temp:@"mp4"];
			[mediaItem.audioData writeToFile:path atomically:NO];
                UISaveVideoAtPathToSavedPhotosAlbum(path, self, @selector(video:didFinishSavingWithError:contextInfo:), nil);
            }
        }
    }*/

    //TODO: Ability for user to record audio
    /*- (void)actionAudio
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        IQAudioRecorderViewController *controller = [[IQAudioRecorderViewController alloc] init];
        controller.delegate = self;
        controller.title = @"Recorder";
        controller.maximumRecordDuration = AUDIO_LENGTH;
        controller.allowCropping = NO;

	    [self.tabBarController presentBlurredAudioRecorderViewControllerAnimated:controller];
    }*/

    //TODO: Special stickers view?
    /*- (void)actionStickers
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        StickersView *stickersView = [[StickersView alloc] init];
        stickersView.delegate = self;
        NavigationController *navController = [[NavigationController alloc] initWithRootViewController:stickersView];
	    [self presentViewController:navController animated:YES completion:nil];
    }*/

    private void updateTitleDetails() {

        RelativeLayout dialogAvatarContainer = (RelativeLayout) findViewById(R.id.dialogAvatarContainer);
        CircleImageView dialogAvatar = (CircleImageView) findViewById(R.id.dialogAvatar);
        MaterialLetterIcon dialogAvatarIcon = (MaterialLetterIcon) findViewById(R.id.dialogAvatarIcon);

        TextView dialogName = (TextView) findViewById(R.id.dialogName);
        TextView dialogDate = (TextView) findViewById(R.id.dialogDate);


        if(chatRequest.isPrivateChat()) {
            String userId = chatRequest.getUserIdForPrivateChat();
            DBUser dbUser = usersService.getUserById(userId);

            if(dbUser.getPicture() != null && !dbUser.getPicture().isEmpty()) {
                dialogAvatar.setVisibility(View.VISIBLE);
                dialogAvatarIcon.setVisibility(View.GONE);

                Glide.with(this).load(dbUser.getPicture()).into(dialogAvatar);
            } else {
                dialogAvatar.setVisibility(View.GONE);
                dialogAvatarIcon.setVisibility(View.VISIBLE);

                AvatarViewUtil.renderInitialsUserAvatar(this, dialogAvatarIcon, dbUser.getSafeName());
            }

            dialogName.setText(dbUser.getSafeName());
            dialogDate.setText("Last seen at " + format(dbUser.getLastActive()));

        } else if(chatRequest.isGroupChat()) {
            DBGroup dbGroup = groupsService.getGroupById(chatRequest.getGroupId());

            String[] members = dbGroup.getMembers().split(",");

            if(dbGroup.getPicture() != null && !dbGroup.getPicture().isEmpty()) {
                dialogAvatar.setVisibility(View.VISIBLE);
                dialogAvatarIcon.setVisibility(View.GONE);

                Glide.with(this).load(dbGroup.getPicture()).into(dialogAvatar);
            } else {
                dialogAvatar.setVisibility(View.GONE);
                dialogAvatarIcon.setVisibility(View.VISIBLE);

                AvatarViewUtil.renderInitialsUserAvatar(this, dialogAvatarIcon, dbGroup.getName());
            }

            dialogName.setText(dbGroup.getName());
            dialogDate.setText(members.length + " Members");

        } else if(chatRequest.isMultipleChat()) {
            dialogName.setText("Multiple");
            dialogDate.setText(chatRequest.getMembers().size() + " Members");

            dialogAvatarContainer.setVisibility(View.GONE);
        }
    }





    private void createTypingObservers() {
        typingChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey() != null &&
                        !dataSnapshot.getKey().equals(AuthService.getCurrentId())) {

                    Boolean typing = (Boolean) dataSnapshot.getValue();
                    if (typing != null) {
                        TextView dialogDate = (TextView) findViewById(R.id.dialogDate);

                        dialogDate.setVisibility(typing ? View.GONE : View.VISIBLE);
                        typingIndicator.setVisibility(typing ? View.VISIBLE : View.GONE);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        typingDatabaseReference.addChildEventListener(typingChildEventListener);
    }


    private void typingIndicatorStart() {
        typingCounter++;

        typingIndicatorSave(true);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                typingIndicatorStop();
            }
        }, 2000);
    }

    private void typingIndicatorStop() {
        typingCounter--;

        if(typingCounter == 0) {
            typingIndicatorSave(false);
        }
    }

    private void typingIndicatorSave(boolean typing) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(AuthService.getCurrentId(), typing);

        typingDatabaseReference.updateChildren(map);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ChatRequest.class.getCanonicalName(), chatRequest);
    }

    //TODO: creating actions menu
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO: creating actions menu
        /*switch (item.getItemId()) {
            case R.id.action_delete:
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                AppUtils.showToast(this, R.string.copied_message, true);
                break;
        }*/

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

                    messagesService.sendPictureMessage(selectedImageUri, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //TODO: Handle failure
                        }
                    }, new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //TODO: Handle progress of image upload
                        }
                    });
                }
            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST){
            if (resultCode == RESULT_OK){
                if (filePathImageCamera != null && filePathImageCamera.exists()){

                    messagesService.sendPictureMessage(FileProvider.getUriForFile(ChatActivity.this, getApplicationContext().getPackageName() + ".provider", filePathImageCamera), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //TODO: Handle failure
                        }
                    }, new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //TODO: Handle progress of camera image upload
                        }
                    });
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                if (place != null) {
                    LatLng latLng = place.getLatLng();

                    messagesService.sendLocationMessage(
                            latLng.latitude,
                            latLng.longitude
                    );
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        messagesService.stopListeningToGroup();

        if (typingChildEventListener != null) {
            typingDatabaseReference.removeEventListener(typingChildEventListener);
        }
        realm.close();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if(emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public String format(Date date) {
        return DateUtils.format(this, date);
    }

    private MessagesListAdapter.Formatter<DBMessageDecorator> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<DBMessageDecorator>() {
            @Override
            public String format(DBMessageDecorator message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }
}
