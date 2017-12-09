package org.lirazs.chatty.model.realm;

import org.lirazs.chatty.model.firebase.FirebaseUserStatusObject;

import java.util.Date;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mac on 2/18/17.
 */

public class DBUserStatus extends RealmObject implements RealmModel {

    @PrimaryKey
    private String objectId;

    private Date createdAt;
    private Date updatedAt;

    private String name;

    public DBUserStatus() {
    }

    public DBUserStatus(FirebaseUserStatusObject statusObject) {
        setObjectId(statusObject.getObjectId());

        setCreatedAt(statusObject.getCreatedAt());
        setUpdatedAt(statusObject.getUpdatedAt());

        setName(statusObject.getName());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
