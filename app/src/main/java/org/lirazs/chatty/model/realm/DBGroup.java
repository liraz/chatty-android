package org.lirazs.chatty.model.realm;

import android.text.TextUtils;

import org.lirazs.chatty.model.firebase.FirebaseGroupObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mac on 2/18/17.
 */
public class DBGroup extends RealmObject implements RealmModel {

    public static String GROUP_OBJECTID = "objectId";

    public static String GROUP_CREATED_AT = "createdAt";
    public static String GROUP_UPDATED_AT = "updatedAt";

    public static String GROUP_USERID = "userId";
    public static String GROUP_NAME = "name";
    public static String GROUP_PICTURE = "picture";
    public static String GROUP_MEMBERS = "members";

    public static String GROUP_ISDELETED = "isDeleted";

    @PrimaryKey
    private String objectId;

    private Date createdAt;
    private Date updatedAt;

    private String userId;
    private String name;
    private String picture;
    private String members;

    private Boolean isDeleted;

    public DBGroup() {
    }

    public DBGroup(FirebaseGroupObject groupObject) {
        setObjectId(groupObject.getObjectId());

        setCreatedAt(groupObject.getCreatedAt());
        setUpdatedAt(groupObject.getUpdatedAt());

        setUserId(groupObject.getUserId());
        setName(groupObject.getName());
        setPicture(groupObject.getPicture());
        setDeleted(groupObject.getDeleted());

        if(groupObject.getMembers() != null && groupObject.getMembers().size() > 0) {
            setMembers(TextUtils.join(",", groupObject.getMembers()));
        }
    }

    public static Date getLastUpdatedAt() {
        Date updatedAt = null;

        try {
            Realm realm = Realm.getDefaultInstance();
            DBGroup groupDB = realm.where(DBGroup.class).findAllSorted(GROUP_UPDATED_AT, Sort.ASCENDING).last();

            updatedAt = groupDB.getUpdatedAt();

        } catch (Exception ignore) {
        }

        return updatedAt;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMembers() {
        return members;
    }
    public List<String> getMembersAsList() {
        return members != null ? Arrays.asList(members.split(",")) : Collections.<String>emptyList();
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
