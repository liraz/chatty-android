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

import org.lirazs.chatty.util.DateUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 1/28/17.
 */

public class FirebaseObject implements Serializable {
    public static final long serialVersionUID = 1L;

    public static String OBJECT_ID_KEY = "objectId";
    public static String UPDATED_AT_KEY = "updatedAt";
    public static String CREATED_AT_KEY = "createdAt";

    private String path;
    private String subPath;

    private Map<String, Object> properties;


    public FirebaseObject(String path) {
        this(path, null, null);
    }
    public FirebaseObject(String path, Map<String, Object> properties) {
        this(path, null, properties);
    }
    public FirebaseObject(String path, String subPath) {
        this(path, subPath, null);
    }
    public FirebaseObject(String path, String subPath, Map<String, Object> properties) {
        this.path = path;
        this.subPath = subPath;

        if (properties != null) {
            this.properties = properties;
        } else {
            this.properties = new HashMap<>();
        }
    }

    public <T> T get(String key) {
        return properties.containsKey(key) ? (T) properties.get(key) : null;
    }

    public <T> void set(String key, T object) {
        properties.put(key, object);
    }

    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }

    public String getObjectId() {
        return (String) properties.get(OBJECT_ID_KEY);
    }

    public void setObjectId(String objectId) {
        properties.put(OBJECT_ID_KEY, objectId);
    }

    public Date getCreatedAt() {
        return DateUtils.objectToDate(get(CREATED_AT_KEY));
    }

    public void setCreatedAt(Date createdAt) {
        set(CREATED_AT_KEY, createdAt.getTime());
    }

    public Date getUpdatedAt() {
        return DateUtils.objectToDate(get(UPDATED_AT_KEY));
    }

    public void setUpdatedAt(Date updatedAt) {
        set(UPDATED_AT_KEY, updatedAt.getTime());
    }

    public void deleteInBackground() {
        deleteInBackground(null);
    }
    public void deleteInBackground(DatabaseReference.CompletionListener completionListener) {
        DatabaseReference reference = getDatabaseReference();

        if(properties.containsKey(OBJECT_ID_KEY)) {
            if(completionListener != null) {
                reference.removeValue(completionListener);
            } else {
                reference.removeValue();
            }
        } else if(completionListener != null) {
            completionListener.onComplete(
                    DatabaseError.fromException(new Exception("Object cannot be deleted.")), reference);
        }
    }



    public void saveInBackground() {
        saveInBackground(null);
    }
    public void saveInBackground(DatabaseReference.CompletionListener completionListener) {

        DatabaseReference reference = getDatabaseReference();

        if(!properties.containsKey(OBJECT_ID_KEY)) {
            properties.put(OBJECT_ID_KEY, reference.getKey());
        }

        long interval = new Date().getTime();

        if(!properties.containsKey(CREATED_AT_KEY)) {
            properties.put(CREATED_AT_KEY, interval);
        }
        properties.put(UPDATED_AT_KEY, interval);

        if(completionListener != null) {
            reference.updateChildren(properties, completionListener);
        } else {
            reference.updateChildren(properties);
        }
    }



    public void updateInBackground() {
        updateInBackground(null);
    }
    public void updateInBackground(DatabaseReference.CompletionListener completionListener) {
        final DatabaseReference reference = getDatabaseReference();

        if(properties.containsKey(OBJECT_ID_KEY)) {
            properties.put(UPDATED_AT_KEY, new Date().getTime());

            if(completionListener != null) {
                reference.updateChildren(properties, completionListener);
            } else {
                reference.updateChildren(properties);
            }
        } else if(completionListener != null) {
            completionListener.onComplete(
                    DatabaseError.fromException(new Exception("Object cannot be updated.")), reference);
        }
    }


    public void fetchInBackground() {
        fetchInBackground(null);
    }
    public void fetchInBackground(final DatabaseReference.CompletionListener completionListener) {
        final DatabaseReference reference = getDatabaseReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    properties = (Map<String, Object>) dataSnapshot.getValue();

                    if(completionListener != null) {
                        completionListener.onComplete(null, reference);
                    }
                } else {
                    if(completionListener != null) {
                        completionListener.onComplete(
                                DatabaseError.fromException(new Exception("Object not found.")), reference);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(completionListener != null) {
                    completionListener.onComplete(databaseError, reference);
                }
            }
        });
    }


    private DatabaseReference getDatabaseReference() {
        DatabaseReference reference;

        if(subPath == null) {
            reference = FirebaseDatabase.getInstance().getReference(path);
        } else {
            reference = FirebaseDatabase.getInstance().getReference(path).child(subPath);
        }

        if(!properties.containsKey(OBJECT_ID_KEY)) {
            return reference.push(); // create a new object with auto generated id
        }
        return reference.child((String) properties.get(OBJECT_ID_KEY));
    }
}
