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
package org.lirazs.chatty.model.realm;

import org.lirazs.chatty.model.enums.MessageStatus;
import org.lirazs.chatty.model.enums.MessageType;
import org.lirazs.chatty.model.firebase.FirebaseMessageObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mac on 2/18/17.
 */

public class DBMessage extends RealmObject implements RealmModel {

    public static String MESSAGE_OBJECTID = "objectId";

    public static String MESSAGE_CREATED_AT = "createdAt";
    public static String MESSAGE_UPDATED_AT = "updatedAt";

    public static String MESSAGE_GROUP_ID = "groupId";
    public static String MESSAGE_SENDER_ID = "senderId";
    public static String MESSAGE_SENDER_NAME = "senderName";
    public static String MESSAGE_SENDER_THUMBNAIL = "senderThumbnail";
    public static String MESSAGE_INITIALS = "senderInitials";

    public static String MESSAGE_TYPE = "type";
    public static String MESSAGE_TEXT = "text";

    public static String MESSAGE_PICTURE = "picture";
    public static String MESSAGE_PICTURE_WIDTH = "pictureWidth";
    public static String MESSAGE_PICTURE_HEIGHT = "pictureHeight";
    public static String MESSAGE_PICTURE_MD5 = "pictureMD5";

    public static String MESSAGE_VIDEO = "video";
    public static String MESSAGE_VIDEO_DURATION = "videoDuration";
    public static String MESSAGE_VIDEO_MD5 = "videoMD5";

    public static String MESSAGE_AUDIO = "audio";
    public static String MESSAGE_AUDIO_DURATION = "audioDuration";
    public static String MESSAGE_AUDIO_MD5 = "audioMD5";

    public static String MESSAGE_LATITUDE = "latitude";
    public static String MESSAGE_LONGITUDE = "longitude";

    public static String MESSAGE_STATUS = "status";
    public static String MESSAGE_IS_DELETED = "isDeleted";


    public static Date getLastUpdatedAt(String groupId) {
        Date updatedAt = null;

        DBMessage messageDB = null;
        try {
            Realm realm = Realm.getDefaultInstance();
            messageDB = realm.where(DBMessage.class).equalTo("groupId", groupId).findAllSorted("updatedAt", Sort.ASCENDING).last();

            updatedAt = messageDB.getUpdatedAt();
        } catch (Exception ignore) {
        }

        return updatedAt;
    }

    @PrimaryKey
    private String objectId;

    private Date createdAt;
    private Date updatedAt;

    private String groupId;
    private String senderId;
    private String senderName;
    private String senderThumbnail;
    private String senderInitials;

    private String type;
    private String text;

    private String picture;
    private Long pictureWidth;
    private Long pictureHeight;
    private String pictureMD5;

    private String video;
    private Long videoDuration;
    private String videoMD5;

    private String audio;
    private Long audioDuration;
    private String audioMD5;

    private Double latitude;
    private Double longitude;

    private String status;
    private Boolean isDeleted;

    public DBMessage() {
    }

    public DBMessage(FirebaseMessageObject firebaseMessageObject) {
        setObjectId(firebaseMessageObject.getObjectId());

        setCreatedAt(firebaseMessageObject.getCreatedAt());
        setUpdatedAt(firebaseMessageObject.getUpdatedAt());

        setGroupId(firebaseMessageObject.getGroupId());
        setSenderId(firebaseMessageObject.getSenderId());
        setSenderName(firebaseMessageObject.getSenderName());
        setSenderThumbnail(firebaseMessageObject.getSenderThumbnail());
        setSenderInitials(firebaseMessageObject.getSenderInitials());

        setType(firebaseMessageObject.getType());
        setText(firebaseMessageObject.getText());

        setPicture(firebaseMessageObject.getPicture());
        setPictureWidth(firebaseMessageObject.getPictureWidth());
        setPictureHeight(firebaseMessageObject.getPictureHeight());
        setPictureMD5(firebaseMessageObject.getPictureMD5());

        setVideo(firebaseMessageObject.getVideo());
        setVideoDuration(firebaseMessageObject.getVideoDuration());
        setVideoMD5(firebaseMessageObject.getVideoMD5());

        setAudio(firebaseMessageObject.getAudio());
        setAudioDuration(firebaseMessageObject.getAudioDuration());
        setAudioMD5(firebaseMessageObject.getAudioMD5());

        if (firebaseMessageObject.getLatitude() != null) {
            setLatitude(firebaseMessageObject.getLatitude().doubleValue());
        }
        if (firebaseMessageObject.getLongitude() != null) {
            setLongitude(firebaseMessageObject.getLongitude().doubleValue());
        }

        setStatus(firebaseMessageObject.getStatus());
        setDeleted(firebaseMessageObject.getDeleted());
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderThumbnail() {
        return senderThumbnail;
    }

    public void setSenderThumbnail(String senderThumbnail) {
        this.senderThumbnail = senderThumbnail;
    }

    public String getSenderInitials() {
        return senderInitials;
    }

    public void setSenderInitials(String senderInitials) {
        this.senderInitials = senderInitials;
    }



    public MessageType getType() {
        return type != null ? MessageType.getByName(type) : null;
    }
    public void setType(MessageType messageType) {
        type = messageType != null ? messageType.getName() : null;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Long getPictureWidth() {
        return pictureWidth;
    }

    public void setPictureWidth(Long pictureWidth) {
        this.pictureWidth = pictureWidth;
    }

    public Long getPictureHeight() {
        return pictureHeight;
    }

    public void setPictureHeight(Long pictureHeight) {
        this.pictureHeight = pictureHeight;
    }

    public String getPictureMD5() {
        return pictureMD5;
    }

    public void setPictureMD5(String pictureMD5) {
        this.pictureMD5 = pictureMD5;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoMD5() {
        return videoMD5;
    }

    public void setVideoMD5(String videoMD5) {
        this.videoMD5 = videoMD5;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Long getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(Long audioDuration) {
        this.audioDuration = audioDuration;
    }

    public String getAudioMD5() {
        return audioMD5;
    }

    public void setAudioMD5(String audioMD5) {
        this.audioMD5 = audioMD5;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public MessageStatus getStatus() {
        return status != null ? MessageStatus.getByName(status) : null;
    }
    public void setStatus(MessageStatus messageStatus) {
        status = messageStatus != null ? messageStatus.getName() : null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
