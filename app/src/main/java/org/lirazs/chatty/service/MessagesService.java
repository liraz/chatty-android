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
package org.lirazs.chatty.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import org.lirazs.chatty.event.service.MessagesServiceEvent;
import org.lirazs.chatty.model.enums.MessageStatus;
import org.lirazs.chatty.model.enums.MessageType;
import org.lirazs.chatty.model.firebase.FirebaseMessageObject;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.model.realm.DBMessage;
import org.lirazs.chatty.util.EmojiUtils;
import org.lirazs.chatty.util.FileUtils;
import org.lirazs.chatty.util.MediaFileUtils;
import org.lirazs.chatty.util.Reachability;
import org.lirazs.chatty.util.SecurityUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by mac on 2/17/17.
 */

public class MessagesService {

    private DatabaseReference databaseReference;

    private Context context;
    private AuthService authService;
    private RecentService recentService;
    private PushNotificationService pushNotificationService;

    private String currentGroupId;
    private List<ChildEventListener> childEventListeners;

    public MessagesService(Context context, AuthService authService,
                           RecentService recentService, PushNotificationService pushNotificationService) {
        this.context = context;
        this.authService = authService;
        this.recentService = recentService;
        this.pushNotificationService = pushNotificationService;

        childEventListeners = new ArrayList<>();

        //EventBus.getDefault().register(this);
    }


    public void startListeningToGroup(String groupId) {
        currentGroupId = groupId;

        createObservers();
    }

    public void stopListeningToGroup() {
        currentGroupId = null;
        actionCleanup();
    }

    public RealmResults<DBMessage> getGroupMessages() {
        RealmResults<DBMessage> messages = null;

        if(currentGroupId != null) {
            Realm realm = Realm.getDefaultInstance();

            messages = realm.where(DBMessage.class)
                    .equalTo(DBMessage.MESSAGE_GROUP_ID, currentGroupId)
                    .equalTo(DBMessage.MESSAGE_IS_DELETED, false)
                    .findAllSorted(DBMessage.MESSAGE_CREATED_AT, Sort.ASCENDING);

            realm.close();
        }
        return messages;
    }

    public void sendStatusMessage(String status) {
        FirebaseMessageObject messageObject = generateSelfFirebaseMessage();
        String groupId = messageObject.getGroupId();

        messageObject.setType(MessageType.STATUS);
        messageObject.setText(SecurityUtils.encryptText(status, groupId));

        sendMessage(messageObject);
    }

    public void sendTextMessage(String text) {
        FirebaseMessageObject messageObject = generateSelfFirebaseMessage();
        String groupId = messageObject.getGroupId();

        messageObject.setType(EmojiUtils.isOnlyEmojis(text) ? MessageType.EMOJI : MessageType.TEXT);
        messageObject.setText(SecurityUtils.encryptText(text, groupId));

        sendMessage(messageObject);
    }

    public void sendPictureMessage(Uri imageUri, OnFailureListener failureListener,
                                   OnProgressListener<UploadTask.TaskSnapshot> progressListener) {

        final FirebaseMessageObject messageObject = generateSelfFirebaseMessage();
        String groupId = messageObject.getGroupId();

        messageObject.setType(MessageType.PICTURE);
        messageObject.setText(SecurityUtils.encryptText("[Picture message]", groupId));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(FileUtils.FIREBASE_STORAGE)
                .child(FileUtils.generateFilename("message_image", "jpg"));

        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            final int imageWidth = bmp.getWidth();
            final int imageHeight = bmp.getHeight();

            //TODO: Encrypting the image - need to rethink this - encryption needs to be equal in iphone & android
            //NSData *cryptedPicture = [Cryptor encryptData:dataPicture groupId:groupId];

            //TODO: Fail message
            // [ProgressHUD showError:@"Message sending failed."];

            //TODO: Deal with progress
            // hud.progress = (float) snapshot.progress.completedUnitCount / (float) snapshot.progress.totalUnitCount;

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(failureListener)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String link = taskSnapshot.getDownloadUrl().toString();
                    String md5Hash = taskSnapshot.getMetadata().getMd5Hash();

                    messageObject.setPicture(link);
                    messageObject.setPictureWidth((long) imageWidth);
                    messageObject.setPictureHeight((long) imageHeight);
                    messageObject.setPictureMD5(md5Hash);

                    sendMessage(messageObject);
                }
            }).addOnProgressListener(progressListener);

        } catch (IOException e) {
            failureListener.onFailure(e);
        }
    }


    public void sendVideoMessage(Uri videoUri, OnFailureListener failureListener,
                                   OnProgressListener<UploadTask.TaskSnapshot> progressListener) {

        final FirebaseMessageObject messageObject = generateSelfFirebaseMessage();
        String groupId = messageObject.getGroupId();

        messageObject.setType(MessageType.VIDEO);
        messageObject.setText(SecurityUtils.encryptText("[Video message]", groupId));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(FileUtils.FIREBASE_STORAGE)
                .child(FileUtils.generateFilename("message_video", "mp4"));

        //TODO: Encrypting the video - need to rethink this - encryption needs to be equal in iphone & android
        //NSData *cryptedVideo = [Cryptor encryptData:dataVideo groupId:groupId];

        //TODO: Fail message
        //[ProgressHUD showError:@"Message sending failed."];

        //TODO: Deal with progress
        //hud.progress = (float) snapshot.progress.completedUnitCount / (float) snapshot.progress.totalUnitCount;

        final long duration = MediaFileUtils.getDuration(context, videoUri);

        UploadTask uploadTask = storageRef.putFile(videoUri);
        uploadTask.addOnFailureListener(failureListener)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String link = taskSnapshot.getDownloadUrl().toString();
                        String md5Hash = taskSnapshot.getMetadata().getMd5Hash();

                        messageObject.setVideo(link);
                        messageObject.setVideoDuration(duration);
                        messageObject.setVideoMD5(md5Hash);

                        sendMessage(messageObject);
                    }
                }).addOnProgressListener(progressListener);
    }


    public void sendAudioMessage(Uri audioUri, OnFailureListener failureListener,
                                 OnProgressListener<UploadTask.TaskSnapshot> progressListener) throws IOException {

        final FirebaseMessageObject messageObject = generateSelfFirebaseMessage();
        String groupId = messageObject.getGroupId();

        messageObject.setType(MessageType.VIDEO);
        messageObject.setText(SecurityUtils.encryptText("[Audio message]", groupId));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(FileUtils.FIREBASE_STORAGE)
                .child(FileUtils.generateFilename("message_audio", "m4a"));

        //TODO: Encrypting the audio - need to rethink this - encryption needs to be equal in iphone & android
        //NSData *cryptedAudio = [Cryptor encryptData:dataAudio groupId:groupId];

        //TODO: Fail message
        //[ProgressHUD showError:@"Message sending failed."];

        //TODO: Deal with progress
        //hud.progress = (float) snapshot.progress.completedUnitCount / (float) snapshot.progress.totalUnitCount;

        final long duration = MediaFileUtils.getDuration(context, audioUri);

        UploadTask uploadTask = storageRef.putFile(audioUri);
        uploadTask.addOnFailureListener(failureListener)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String link = taskSnapshot.getDownloadUrl().toString();
                        String md5Hash = taskSnapshot.getMetadata().getMd5Hash();

                        messageObject.setAudio(link);
                        messageObject.setAudioDuration(duration);
                        messageObject.setAudioMD5(md5Hash);

                        sendMessage(messageObject);
                    }
                }).addOnProgressListener(progressListener);
    }


    public void sendLocationMessage(Double latitude, Double longitude) {

        final FirebaseMessageObject messageObject = generateSelfFirebaseMessage();
        String groupId = messageObject.getGroupId();

        messageObject.setType(MessageType.LOCATION);
        messageObject.setText(SecurityUtils.encryptText("[Location message]", groupId));

        messageObject.setLatitude(latitude);
        messageObject.setLongitude(longitude);

        sendMessage(messageObject);
    }


    public void markMessageStatusAsRead(String groupId, String messageId) {
        FirebaseMessageObject firebaseMessageObject = new FirebaseMessageObject(groupId);

        firebaseMessageObject.setObjectId(messageId);
        firebaseMessageObject.setStatus(MessageStatus.TEXT_READ);

        firebaseMessageObject.updateInBackground();
    }


    public void deleteMessageItem(String groupId, String messageId) {
        FirebaseMessageObject firebaseMessageObject = new FirebaseMessageObject(groupId);

        firebaseMessageObject.setObjectId(messageId);
        firebaseMessageObject.setDeleted(true);

        firebaseMessageObject.updateInBackground();
    }


    public void deleteMessageItem(DBMessage dbMessage) {

        if(dbMessage.getStatus().equals(MessageStatus.TEXT_QUEUED.getName())) {

            Realm realm = Realm.getDefaultInstance();

            try {
                realm.beginTransaction();
                dbMessage.deleteFromRealm();
                realm.commitTransaction();
            } finally {
                realm.close();
            }

            EventBus.getDefault().post(new MessagesServiceEvent(MessagesServiceEvent.Type.MESSAGE_DELETED, dbMessage));
        } else {
            deleteMessageItem(dbMessage.getGroupId(), dbMessage.getObjectId());
        }
    }


    private void createObservers() {
        final Date lastUpdatedAt = DBMessage.getLastUpdatedAt(currentGroupId);

        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseMessageObject.MESSAGE_PATH).child(currentGroupId);

        childEventListeners.add(databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseMessageObject message = new FirebaseMessageObject((Map<String, Object>)dataSnapshot.getValue());
                if(lastUpdatedAt == null || message.getUpdatedAt().after(lastUpdatedAt)) {

                    updateIncoming(message);
                    DBMessage dbMessage = updateRealm(message);

                    EventBus.getDefault().post(new MessagesServiceEvent(MessagesServiceEvent.Type.MESSAGE_ADDED, dbMessage));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseMessageObject message = new FirebaseMessageObject((Map<String, Object>)dataSnapshot.getValue());
                DBMessage dbMessage = updateRealm(message);

                EventBus.getDefault().post(new MessagesServiceEvent(MessagesServiceEvent.Type.MESSAGE_UPDATED, dbMessage));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        }));
    }


    private DBMessage updateRealm(FirebaseMessageObject message) {

        DBMessage temp = new DBMessage(message);

        if(message.getGroupId() != null) {
            String text = SecurityUtils.decryptText(message.getText(), message.getGroupId());
            temp.setText(text);
        }

        Realm realm = Realm.getDefaultInstance();

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(temp);
            realm.commitTransaction();
        } finally {
            realm.close();
        }
        return temp;
    }

    private void updateIncoming(FirebaseMessageObject message) {

        if(!AuthService.getCurrentId().equals(message.getSenderId())) {

            if(message.getStatus().equals(MessageStatus.TEXT_SENT)) {

                message.setStatus(MessageStatus.TEXT_READ);
                message.updateInBackground();

                //TODO: playMessageReceivedSound
            }
        }
    }

    //TODO: Delete message
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    /*+ (void)deleteItem:(NSString *)groupId messageId:(NSString *)messageId
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        FObject *object = [FObject objectWithPath:FMESSAGE_PATH Subpath:groupId];
        //---------------------------------------------------------------------------------------------------------------------------------------------
        object[FMESSAGE_OBJECTID] = messageId;
        object[FMESSAGE_ISDELETED] = @YES;
        //---------------------------------------------------------------------------------------------------------------------------------------------
        [object updateInBackground];
    }*/

    //TODO: Delete message
    /*//-------------------------------------------------------------------------------------------------------------------------------------------------
    + (void)deleteItem:(DBMessage *)dbmessage
    //-------------------------------------------------------------------------------------------------------------------------------------------------
    {
        if ([dbmessage.status isEqualToString:TEXT_QUEUED])
        {
            RLMRealm *realm = [RLMRealm defaultRealm];
            [realm beginWriteTransaction];
            [realm deleteObject:dbmessage];
            [realm commitWriteTransaction];
            [NotificationCenter post:NOTIFICATION_REFRESH_MESSAGES1];
        }
	    else [self deleteItem:dbmessage.groupId messageId:dbmessage.objectId];
    }*/

    private FirebaseMessageObject generateSelfFirebaseMessage() {
        FirebaseMessageObject messageObject = new FirebaseMessageObject(currentGroupId);

        FirebaseUserObject currentUser = authService.getCurrentUser();

        messageObject.setGroupId(currentGroupId);
        messageObject.setSenderId(currentUser.getObjectId());
        messageObject.setSenderName(currentUser.getSafeName());
        messageObject.setSenderThumbnail(currentUser.getThumbnail());
        messageObject.setSenderInitials(currentUser.getInitials());

        messageObject.setStatus(MessageStatus.TEXT_SENT);
        messageObject.setDeleted(false);

        return messageObject;
    }


    private void sendMessage(final FirebaseMessageObject messageObject) {
        // if user has internet - and can send this message
        if(Reachability.isNetworkingAvailable(context)) {

            messageObject.saveInBackground(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        // updating the recent for message
                        recentService.updateRecentLastMessage(messageObject);

                        if(messageObject.getType() != MessageType.STATUS) {
                            //TODO: Play message outgoing sound..
                        }
                        // send push notification to all users
                        pushNotificationService.sendPushNotificationByMessage(messageObject);
                    } else {
                        //TODO: Dispatch message send error event
                    }
                }
            });
        } else {
            //TODO: Support for pending messages that user tried send while offline
        }
    }

    //TODO: SoundService listens to messages update
    /*if (playMessageReceivedSound)
    {
        [JSQSystemSoundPlayer jsq_playMessageReceivedSound];
        playMessageReceivedSound = NO;
    }*/

    private void actionCleanup() {
        for (ChildEventListener childEventListener : childEventListeners) {
            databaseReference.removeEventListener(childEventListener);
        }
        childEventListeners.clear();
        databaseReference = null;
    }
}
