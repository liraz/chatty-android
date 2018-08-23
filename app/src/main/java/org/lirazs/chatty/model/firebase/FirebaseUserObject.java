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


import org.lirazs.chatty.model.enums.KeepMedia;
import org.lirazs.chatty.model.enums.LoginMethod;
import org.lirazs.chatty.model.enums.NetworkMode;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.util.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by mac on 2/4/17.
 */

public class FirebaseUserObject extends FirebaseObject implements Serializable {

    public static final long serialVersionUID = 1L;

    public static String USER_PATH = "User";

    public static String USER_EMAIL_KEY = "email";
    public static String USER_PHONE_KEY = "phone";
    public static String USER_FIRST_NAME_KEY = "firstname";
    public static String USER_LAST_NAME_KEY = "lastname";
    public static String USER_FULL_NAME_KEY = "fullname";
    public static String USER_COUNTRY_KEY = "country";
    public static String USER_LOCATION_KEY = "location";
    public static String USER_STATUS_KEY = "status";
    public static String USER_LOGIN_METHOD_KEY = "loginMethod";
    public static String USER_WALLPAPER_KEY = "wallpaper";
    public static String USER_ONE_SIGNAL_ID_KEY = "oneSignalId";

    public static String USER_PICTURE_KEY = "picture";
    public static String USER_THUMBNAIL_KEY = "thumbnail";

    public static String USER_KEEP_MEDIA_KEY = "keepMedia";
    public static String USER_NETWORK_IMAGE_KEY = "networkImage";
    public static String USER_NETWORK_VIDEO_KEY = "networkVideo";
    public static String USER_NETWORK_AUDIO_KEY = "networkAudio";

    public static String USER_AUTO_SAVE_MEDIA_KEY = "autoSaveMedia";

    public static String USER_LASTACTIVE_KEY = "lastActive";
    public static String USER_LASTTERMINATE_KEY = "lastTerminate";

    public FirebaseUserObject() {
        super(USER_PATH);
    }

    public FirebaseUserObject(String userId) {
        super(USER_PATH);
        setObjectId(userId);
    }

    public FirebaseUserObject(Map<String, Object> properties) {
        super(USER_PATH, properties);
    }

    public boolean isCurrent() {
        String currentId = AuthService.getCurrentId();
        return currentId != null && currentId.equals(getObjectId());
    }

    public String getSafeName() {
        if(getFullName() != null) {
            return getFullName();
        } else if(getEmail() != null) {
            return getEmail();
        } else if(getPhone() != null) {
            return getPhone();
        }
        return null;
    }

    public String getFullName() {
        return get(USER_FULL_NAME_KEY);
    }
    public void setFullName(String fullName) {
        if(fullName != null && fullName.contains(" ")) {
            String[] strings = fullName.split(" ");
            if(strings.length > 1) {
                setFirstName(strings[0]);
                setLastName(strings[1]);
            }
        }
        set(USER_FULL_NAME_KEY, fullName);
    }
    public String getCountry() {
        return get(USER_COUNTRY_KEY);
    }
    public String getLocation() {
        return get(USER_LOCATION_KEY);
    }

    public String getEmail() {
        return get(USER_EMAIL_KEY);
    }
    public void setEmail(String email) {
        set(USER_EMAIL_KEY, email);
    }

    public String getPhone() {
        return get(USER_PHONE_KEY);
    }
    public void setPhone(String phone) {
        set(USER_PHONE_KEY, phone);
    }

    public String getFirstName() {
        return get(USER_FIRST_NAME_KEY);
    }
    public void setFirstName(String firstName) {
        set(USER_FIRST_NAME_KEY, firstName);
    }
    public String getLastName() {
        return get(USER_LAST_NAME_KEY);
    }
    public void setLastName(String lastName) {
        set(USER_LAST_NAME_KEY, lastName);
    }

    public String getPicture() {
        return get(USER_PICTURE_KEY);
    }
    public void setPicture(String picture) {
        set(USER_PICTURE_KEY, picture);
    }

    public String getThumbnail() {
        return get(USER_THUMBNAIL_KEY);
    }
    public void setThumbnail(String thumbnail) {
        set(USER_THUMBNAIL_KEY, thumbnail);
    }

    public String getStatus() {
        return get(USER_STATUS_KEY);
    }

    public String getWallpaper() {
        return get(USER_WALLPAPER_KEY);
    }

    public LoginMethod getLoginMethod() {
        Object o = get(USER_LOGIN_METHOD_KEY);
        return o != null && o instanceof String ? LoginMethod.getByName((String) o) : null;
    }
    public void setLoginMethod(LoginMethod loginMethod) {
        set(USER_LOGIN_METHOD_KEY, loginMethod != null ? loginMethod.getName() : null);
    }

    public KeepMedia getKeepMedia() {
        Object o = get(USER_KEEP_MEDIA_KEY);
        return o != null ? KeepMedia.getByValue((Long) o) : null;
    }
    public void setKeepMedia(KeepMedia keepMedia) {
        set(USER_LOGIN_METHOD_KEY, keepMedia != null ? keepMedia.getValue() : null);
    }

    public NetworkMode getNetworkImage() {
        Object o = get(USER_NETWORK_IMAGE_KEY);
        return o != null ? NetworkMode.getByValue((Long) o) : null;
    }
    public void setNetworkImage(NetworkMode networkMode) {
        set(USER_NETWORK_IMAGE_KEY, networkMode != null ? networkMode.getValue() : null);
    }


    public NetworkMode getNetworkVideo() {
        Object o = get(USER_NETWORK_VIDEO_KEY);
        return o != null ? NetworkMode.getByValue((Long) o) : null;
    }
    public void setNetworkVideo(NetworkMode networkMode) {
        set(USER_NETWORK_VIDEO_KEY, networkMode != null ? networkMode.getValue() : null);
    }


    public NetworkMode getNetworkAudio() {
        Object o = get(USER_NETWORK_AUDIO_KEY);
        return o != null ? NetworkMode.getByValue((Long) o) : null;
    }
    public void setNetworkAudio(NetworkMode networkMode) {
        set(USER_NETWORK_AUDIO_KEY, networkMode != null ? networkMode.getValue() : null);
    }


    public Boolean isAutoSaveMedia() {
        return get(USER_AUTO_SAVE_MEDIA_KEY);
    }
    public void setAutoSaveMedia(Boolean autoSaveMedia) {
        set(USER_AUTO_SAVE_MEDIA_KEY, autoSaveMedia);
    }


    public Date getLastActive() {
        return DateUtils.objectToDate(get(USER_LASTACTIVE_KEY));
    }

    public void setLastActive(Date lastActive) {
        set(USER_LASTACTIVE_KEY, lastActive.getTime());
    }


    public Date getLastTerminate() {
        return DateUtils.objectToDate(get(USER_LASTTERMINATE_KEY));
    }

    public void setLastTerminate(Date lastTerminate) {
        set(USER_LASTTERMINATE_KEY, lastTerminate != null ? lastTerminate.getTime() : null);
    }


    public Boolean isOnBoardOk() {
        return getSafeName() != null;
    }

    public String getInitials() {
        String firstName = getFirstName();
        String lastName = getLastName();

        if(firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            return String.format("%s%s", firstName.substring(0, 1), lastName.substring(0, 1));
        }
        return null;
    }

    public String getOneSignalId() {
        return get(USER_ONE_SIGNAL_ID_KEY);
    }
    public void setOneSignalId(String oneSignalId) {
        set(USER_ONE_SIGNAL_ID_KEY, oneSignalId);
    }

}