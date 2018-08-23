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
package org.lirazs.chatty.model.firebase;


import org.lirazs.chatty.model.enums.MessageStatus;
import org.lirazs.chatty.model.enums.MessageType;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by mac on 2/4/17.
 */

public class FirebaseMessageObject extends FirebaseObject implements Serializable {

    public static String MESSAGE_PATH = "Message";

    public static String MESSAGE_GROUP_ID = "groupId";
    public static String MESSAGE_SENDER_ID = "senderId";
    public static String MESSAGE_SENDER_NAME = "senderName";
    public static String MESSAGE_SENDER_THUMBNAIL = "senderThumbnail";
    public static String MESSAGE_SENDER_INITIALS = "senderInitials";

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


    public FirebaseMessageObject(String groupId) {
        super(MESSAGE_PATH, groupId);
    }
    public FirebaseMessageObject(Map<String, Object> properties) {
        super(MESSAGE_PATH, (String) properties.get(MESSAGE_GROUP_ID), properties);
    }

    public String getGroupId() {
        return get(MESSAGE_GROUP_ID);
    }

    public void setGroupId(String groupId) {
        set(MESSAGE_GROUP_ID, groupId);
    }

    public String getSenderId() {
        return get(MESSAGE_SENDER_ID);
    }

    public void setSenderId(String senderId) {
        set(MESSAGE_SENDER_ID, senderId);
    }

    public String getSenderName() {
        return get(MESSAGE_SENDER_NAME);
    }

    public void setSenderName(String senderName) {
        set(MESSAGE_SENDER_NAME, senderName);
    }

    public String getSenderThumbnail() {
        return get(MESSAGE_SENDER_THUMBNAIL);
    }

    public void setSenderThumbnail(String senderThumbnail) {
        set(MESSAGE_SENDER_THUMBNAIL, senderThumbnail);
    }

    public String getSenderInitials() {
        return get(MESSAGE_SENDER_INITIALS);
    }

    public void setSenderInitials(String senderInitials) {
        set(MESSAGE_SENDER_INITIALS, senderInitials);
    }

    public MessageType getType() {
        String messageType = get(MESSAGE_TYPE);
        return MessageType.getByName(messageType);
    }

    public void setType(MessageType type) {
        set(MESSAGE_TYPE, type.getName());
    }

    public String getText() {
        return get(MESSAGE_TEXT);
    }

    public void setText(String text) {
        set(MESSAGE_TEXT, text);
    }

    public String getPicture() {
        return get(MESSAGE_PICTURE);
    }

    public void setPicture(String picture) {
        set(MESSAGE_PICTURE, picture);
    }

    public Long getPictureWidth() {
        return get(MESSAGE_PICTURE_WIDTH);
    }

    public void setPictureWidth(Long width) {
        set(MESSAGE_PICTURE_WIDTH, width);
    }

    public Long getPictureHeight() {
        return get(MESSAGE_PICTURE_HEIGHT);
    }

    public void setPictureHeight(Long height) {
        set(MESSAGE_PICTURE_HEIGHT, height);
    }

    public String getPictureMD5() {
        return get(MESSAGE_PICTURE_MD5);
    }

    public void setPictureMD5(String md5) {
        set(MESSAGE_PICTURE_MD5, md5);
    }

    public String getVideo() {
        return get(MESSAGE_VIDEO);
    }

    public void setVideo(String video) {
        set(MESSAGE_VIDEO, video);
    }

    public Long getVideoDuration() {
        return get(MESSAGE_VIDEO_DURATION);
    }

    public void setVideoDuration(Long duration) {
        set(MESSAGE_VIDEO_DURATION, duration);
    }

    public String getVideoMD5() {
        return get(MESSAGE_VIDEO_MD5);
    }

    public void setVideoMD5(String md5) {
        set(MESSAGE_VIDEO_MD5, md5);
    }

    public String getAudio() {
        return get(MESSAGE_AUDIO);
    }

    public void setAudio(String audio) {
        set(MESSAGE_AUDIO, audio);
    }

    public Long getAudioDuration() {
        return get(MESSAGE_AUDIO_DURATION);
    }

    public void setAudioDuration(Long duration) {
        set(MESSAGE_AUDIO_DURATION, duration);
    }

    public String getAudioMD5() {
        return get(MESSAGE_AUDIO_MD5);
    }

    public void setAudioMD5(String md5) {
        set(MESSAGE_AUDIO_MD5, md5);
    }

    public Double getLatitude() {
        return get(MESSAGE_LATITUDE);
    }

    public void setLatitude(Double latitude) {
        set(MESSAGE_LATITUDE, latitude);
    }

    public Double getLongitude() {
        return get(MESSAGE_LONGITUDE);
    }

    public void setLongitude(Double longitude) {
        set(MESSAGE_LONGITUDE, longitude);
    }


    public MessageStatus getStatus() {
        String messageStatus = get(MESSAGE_STATUS);
        return MessageStatus.getByName(messageStatus);
    }

    public void setStatus(MessageStatus status) {
        set(MESSAGE_STATUS, status.getName());
    }

    public void setDeleted(Boolean deleted) {
        set(MESSAGE_IS_DELETED, deleted);
    }

    public Boolean getDeleted() {
        return get(MESSAGE_IS_DELETED);
    }
}