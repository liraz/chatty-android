package org.lirazs.chatty.model.realm;

import java.util.Map;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 *
 */
public class DBContact extends RealmObject implements RealmModel {

    public static String CONTACT_OBJECTID = "objectId";

    public static String CONTACT_EMAIL = "email";
    public static String CONTACT_HOME_PHONE = "homePhone";
    public static String CONTACT_MOBILE_PHONE = "mobilePhone";
    public static String CONTACT_WORK_PHONE = "workPhone";

    public static String CONTACT_FIRST_NAME = "firstName";
    public static String CONTACT_LAST_NAME = "lastName";
    public static String CONTACT_FULL_NAME = "fullName";

    public static String CONTACT_IS_DELETED = "isDeleted";

    @PrimaryKey
    private String objectId;

    private String email;
    private String homePhone;
    private String mobilePhone;
    private String workPhone;

    private String firstName;
    private String lastName;
    private String fullName;

    private Boolean isDeleted;

    public DBContact() {
    }

    public DBContact(Map<String, Object> properties) {
        setObjectId((String) properties.get(CONTACT_OBJECTID));

        setEmail((String) properties.get(CONTACT_EMAIL));
        setHomePhone((String) properties.get(CONTACT_HOME_PHONE));
        setMobilePhone((String) properties.get(CONTACT_MOBILE_PHONE));
        setWorkPhone((String) properties.get(CONTACT_WORK_PHONE));

        setFirstName((String) properties.get(CONTACT_FIRST_NAME));
        setLastName((String) properties.get(CONTACT_LAST_NAME));
        setFullName((String) properties.get(CONTACT_FULL_NAME));

        setDeleted((Boolean) properties.get(CONTACT_IS_DELETED));
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
