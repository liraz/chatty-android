package org.lirazs.chatty.model.realm;

import org.lirazs.chatty.model.enums.KeepMedia;
import org.lirazs.chatty.model.enums.LoginMethod;
import org.lirazs.chatty.model.enums.NetworkMode;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mac on 2/18/17.
 */

public class DBUser extends RealmObject implements RealmModel {

    public static String USER_OBJECTID = "objectId";

    public static String USER_CREATED_AT = "createdAt";
    public static String USER_UPDATED_AT = "updatedAt";

    public static String USER_EMAIL = "email";
    public static String USER_PHONE = "phone";

    public static String USER_FIRST_NAME = "firstName";
    public static String USER_LAST_NAME = "lastName";
    public static String USER_FULL_NAME = "fullName";
    public static String USER_COUNTRY = "country";
    public static String USER_LOCATION = "location";
    public static String USER_STATUS = "status";

    public static String USER_PICTURE = "picture";
    public static String USER_THUMBNAIL = "thumbnail";

    public static String USER_KEEP_MEDIA = "keepMedia";
    public static String USER_NETWORK_IMAGE = "networkImage";
    public static String USER_NETWORK_VIDEO = "networkVideo";
    public static String USER_NETWORK_AUDIO = "networkAudio";
    public static String USER_AUTO_SAVE_MEDIA = "autoSaveMedia";
    public static String USER_WALLPAPER = "wallpaper";

    public static String USER_LOGIN_METHOD = "loginMethod";
    public static String USER_ONE_SIGNAL_ID = "oneSignalId";

    public static String USER_LAST_ACTIVE = "lastActive";
    public static String USER_LAST_TERMINATE = "lastTerminate";

    public static Date getLastUpdatedAt() {
        Date updatedAt = null;

        try {
            Realm realm = Realm.getDefaultInstance();
            DBUser dbUser = realm.where(DBUser.class).findAllSorted(USER_UPDATED_AT, Sort.ASCENDING).last();

            updatedAt = dbUser.getUpdatedAt();
        } catch (Exception ignore) {
        }

        return updatedAt;
    }

    public String getInitials() {

        if(firstName != null && firstName.length() > 0 && (lastName == null || lastName.isEmpty()))
            return firstName.substring(0, 1);
        if(lastName != null && lastName.length() > 0 && (firstName == null || firstName.isEmpty()))
            return lastName.substring(0, 1);

        if(firstName != null && firstName.length() > 0 && lastName != null && lastName.length() > 0) {
            return String.format("%s%s", firstName.substring(0 , 1), lastName.substring(0, 1));
        }
        return null;
    }

    @PrimaryKey
    private String objectId;

    private Date createdAt;
    private Date updatedAt;

    private String email;
    private String phone;

    private String firstName;
    private String lastName;
    private String fullName;
    private String country;
    private String location;
    private String status;

    private String picture;
    private String thumbnail;

    private Long keepMedia;
    private Long networkImage;
    private Long networkVideo;
    private Long networkAudio;
    private Boolean autoSaveMedia;
    private String wallpaper;

    private String loginMethod;
    private String oneSignalId;

    private Date lastActive;
    private Date lastTerminate;

    public DBUser() {
    }

    public DBUser(FirebaseUserObject userObject) {
        setObjectId(userObject.getObjectId());

        setCreatedAt(userObject.getCreatedAt());
        setUpdatedAt(userObject.getUpdatedAt());

        setEmail(userObject.getEmail());
        setPhone(userObject.getPhone());

        setFirstName(userObject.getFirstName());
        setLastName(userObject.getLastName());
        setFullName(userObject.getFullName());
        setCountry(userObject.getCountry());
        setLocation(userObject.getLocation());
        setStatus(userObject.getStatus());

        setPicture(userObject.getPicture());
        setThumbnail(userObject.getThumbnail());

        setKeepMedia(userObject.getKeepMedia());
        setNetworkImage(userObject.getNetworkImage());
        setNetworkVideo(userObject.getNetworkVideo());
        setNetworkAudio(userObject.getNetworkAudio());
        setAutoSaveMedia(userObject.isAutoSaveMedia());
        setWallpaper(userObject.getWallpaper());

        setLoginMethod(userObject.getLoginMethod());
        setOneSignalId(userObject.getOneSignalId());

        setLastActive(userObject.getLastActive());
        setLastTerminate(userObject.getLastTerminate());
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSafeName() {
        if(fullName != null) {
            return fullName;
        } else if(email != null) {
            return email;
        } else if(phone != null) {
            return phone;
        }
        return null;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public KeepMedia getKeepMedia() {
        return keepMedia != null ? KeepMedia.getByValue(keepMedia) : null;
    }
    public void setKeepMedia(KeepMedia keepMedia) {
        this.keepMedia = keepMedia != null ? keepMedia.getValue() : null;
    }

    public NetworkMode getNetworkImage() {
        return networkImage != null ? NetworkMode.getByValue(networkImage) : null;
    }
    public void setNetworkImage(NetworkMode networkMode) {
        this.networkImage = networkMode != null ? networkMode.getValue() : null;
    }

    public NetworkMode getNetworkVideo() {
        return networkVideo != null ? NetworkMode.getByValue(networkVideo) : null;
    }
    public void setNetworkVideo(NetworkMode networkMode) {
        this.networkVideo = networkMode != null ? networkMode.getValue() : null;
    }

    public NetworkMode getNetworkAudio() {
        return networkAudio != null ? NetworkMode.getByValue(networkAudio) : null;
    }
    public void setNetworkAudio(NetworkMode networkMode) {
        this.networkAudio = networkMode != null ? networkMode.getValue() : null;
    }


    public Boolean getAutoSaveMedia() {
        return autoSaveMedia;
    }

    public void setAutoSaveMedia(Boolean autoSaveMedia) {
        this.autoSaveMedia = autoSaveMedia;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public LoginMethod getLoginMethod() {
        return loginMethod != null ? LoginMethod.getByName(loginMethod) : null;
    }
    public void setLoginMethod(LoginMethod loginMethod) {
        this.loginMethod = loginMethod != null ? loginMethod.getName() : null;
    }



    public String getOneSignalId() {
        return oneSignalId;
    }

    public void setOneSignalId(String oneSignalId) {
        this.oneSignalId = oneSignalId;
    }

    public Date getLastActive() {
        return lastActive;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }

    public Date getLastTerminate() {
        return lastTerminate;
    }

    public void setLastTerminate(Date lastTerminate) {
        this.lastTerminate = lastTerminate;
    }

}
