package org.lirazs.chatty.model.firebase;


import org.lirazs.chatty.model.enums.ChatType;
import org.lirazs.chatty.util.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2/4/17.
 */

public class FirebaseRecentObject extends FirebaseObject implements Serializable {

    public static String RECENT_PATH = "Recent";

    public static String RECENT_USER_ID = "userId";
    public static String RECENT_GROUP_ID = "groupId";

    public static String RECENT_INITIALS = "initials";
    public static String RECENT_PICTURE = "picture";
    public static String RECENT_DESCRIPTION = "description";
    public static String RECENT_MEMBERS = "members";
    public static String RECENT_PASSWORD = "password";
    public static String RECENT_TYPE = "type";

    public static String RECENT_UNREAD_COUNTER = "counter";
    public static String RECENT_LAST_MESSAGE = "lastMessage";
    public static String RECENT_LAST_MESSAGE_DATE = "lastMessageDate";

    public static String RECENT_IS_ARCHIVED = "isArchived";
    public static String RECENT_IS_DELETED = "isDeleted";


    public FirebaseRecentObject() {
        super(RECENT_PATH);
    }
    public FirebaseRecentObject(Map<String, Object> properties) {
        super(RECENT_PATH, properties);
    }

    public String getUserId() {
        return get(RECENT_USER_ID);
    }

    public void setUserId(String userId) {
        set(RECENT_USER_ID, userId);
    }

    public String getGroupId() {
        return get(RECENT_GROUP_ID);
    }

    public void setGroupId(String groupId) {
        set(RECENT_GROUP_ID, groupId);
    }

    public String getInitials() {
        return get(RECENT_INITIALS);
    }

    public void setInitials(String initials) {
        set(RECENT_INITIALS, initials);
    }

    public String getPicture() {
        return get(RECENT_PICTURE);
    }

    public void setPicture(String picture) {
        set(RECENT_PICTURE, picture);
    }

    public String getDescription() {
        return get(RECENT_DESCRIPTION);
    }

    public void setDescription(String description) {
        set(RECENT_DESCRIPTION, description);
    }

    public List<String> getMembers() {
        return get(RECENT_MEMBERS);
    }

    public void setMembers(List<String> members) {
        set(RECENT_MEMBERS, members);
    }

    public String getPassword() {
        return get(RECENT_PASSWORD);
    }

    public void setPassword(String password) {
        set(RECENT_PASSWORD, password);
    }


    public ChatType getType() {
        Object o = get(RECENT_TYPE);
        return o != null ? ChatType.getByName((String) o) : null;
    }
    public void setType(ChatType chatType) {
        set(RECENT_TYPE, chatType != null ? chatType.getName() : null);
    }

    public Long getUnreadCounter() {
        return get(RECENT_UNREAD_COUNTER);
    }

    public void setUnreadCounter(Long counter) {
        set(RECENT_UNREAD_COUNTER, counter);
    }

    public String getLastMessage() {
        return get(RECENT_LAST_MESSAGE);
    }

    public void setLastMessage(String lastMessage) {
        set(RECENT_LAST_MESSAGE, lastMessage);
    }

    public Date getLastMessageDate() {
        return DateUtils.objectToDate(get(RECENT_LAST_MESSAGE_DATE));
    }

    public void setLastMessageDate(Date lastMessageDate) {
        set(RECENT_LAST_MESSAGE_DATE, lastMessageDate.getTime());
    }

    public Boolean getArchived() {
        return get(RECENT_IS_ARCHIVED);
    }

    public void setArchived(Boolean archived) {
        set(RECENT_IS_ARCHIVED, archived);
    }

    public Boolean getDeleted() {
        return get(RECENT_IS_DELETED);
    }

    public void setDeleted(Boolean deleted) {
        set(RECENT_IS_DELETED, deleted);
    }
}