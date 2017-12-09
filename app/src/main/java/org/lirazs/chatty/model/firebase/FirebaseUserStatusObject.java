package org.lirazs.chatty.model.firebase;


import java.io.Serializable;
import java.util.Map;

/**
 * Created by mac on 2/4/17.
 */

public class FirebaseUserStatusObject extends FirebaseObject implements Serializable {

    public static String USERSTATUS_PATH = "UserStatus";

    public static String USERSTATUS_NAME = "name";


    public FirebaseUserStatusObject() {
        super(USERSTATUS_PATH);
    }
    public FirebaseUserStatusObject(Map<String, Object> properties) {
        super(USERSTATUS_PATH, properties);
    }

    public String getName() {
        return get(USERSTATUS_NAME);
    }

    public void setName(String name) {
        set(USERSTATUS_NAME, name);
    }
}