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

import android.text.TextUtils;

import org.lirazs.chatty.model.enums.ChatType;
import org.lirazs.chatty.model.firebase.FirebaseRecentObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mac on 2/18/17.
 */

public class DBRecent extends RealmObject implements RealmModel {

    public static String RECENT_OBJECTID = "objectId";

    public static String RECENT_CREATED_AT = "createdAt";
    public static String RECENT_UPDATED_AT = "updatedAt";

    public static String RECENT_USERID = "userId";
    public static String RECENT_GROUPID = "groupId";

    public static String RECENT_INITIALS = "initials";
    public static String RECENT_PICTURE = "picture";
    public static String RECENT_DESCRIPTION = "description";
    public static String RECENT_MEMBERS = "members";
    public static String RECENT_PASSWORD = "password";
    public static String RECENT_TYPE = "type";

    public static String RECENT_COUNTER = "counter";
    public static String RECENT_LAST_MESSAGE = "lastMessage";
    public static String RECENT_LAST_MESSAGE_DATE = "lastMessageDate";

    public static String RECENT_IS_ARCHIVED = "isArchived";
    public static String RECENT_IS_DELETED = "isDeleted";


    public static Date getLastUpdatedAt() {
        Date updatedAt = null;

        try {
            Realm realm = Realm.getDefaultInstance();
            DBRecent dbRecent = realm.where(DBRecent.class).findAllSorted("updatedAt", Sort.ASCENDING).last();

            updatedAt = dbRecent.getUpdatedAt();
        } catch (Exception ignore) {
        }

        return updatedAt;
    }

    @PrimaryKey
    private String objectId;

    private Date createdAt;
    private Date updatedAt;

    private String userId;
    private String groupId;

    private String initials;
    private String picture;
    private String description;
    private String members;
    private String password;
    private String type;

    private Long counter;
    private String lastMessage;
    private Date lastMessageDate;

    private Boolean isArchived;
    private Boolean isDeleted;

    public DBRecent() {
    }

    public DBRecent(FirebaseRecentObject recentObject) {
        setObjectId(recentObject.getObjectId());

        setCreatedAt(recentObject.getCreatedAt());
        setUpdatedAt(recentObject.getUpdatedAt());

        setUserId(recentObject.getUserId());
        setGroupId(recentObject.getGroupId());
        setInitials(recentObject.getInitials());
        setPicture(recentObject.getPicture());
        setDescription(recentObject.getDescription());
        setPassword(recentObject.getPassword());
        setType(recentObject.getType());

        setCounter(recentObject.getUnreadCounter());
        setLastMessage(recentObject.getLastMessage());
        setLastMessageDate(recentObject.getLastMessageDate());

        setArchived(recentObject.getArchived());
        setDeleted(recentObject.getDeleted());

        if(recentObject.getMembers() != null) {
            setMembers(TextUtils.join(",", recentObject.getMembers()));
        }
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public ChatType getType() {
        return type != null ? ChatType.getByName(type) : null;
    }
    public void setType(ChatType chatType) {
        type = chatType != null ? chatType.getName() : null;
    }



    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

}
