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


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2/4/17.
 */

public class FirebaseGroupObject extends FirebaseObject implements Serializable {

    public static String GROUP_PATH = "Group";

    public static String GROUP_USERID = "userId";
    public static String GROUP_NAME = "name";
    public static String GROUP_PICTURE = "picture";
    public static String GROUP_MEMBERS = "members";

    public static String GROUP_ISDELETED = "isDeleted";


    public FirebaseGroupObject() {
        super(GROUP_PATH);
    }
    public FirebaseGroupObject(Map<String, Object> properties) {
        super(GROUP_PATH, properties);
    }

    public String getUserId() {
        return get(GROUP_USERID);
    }

    public void setUserId(String userId) {
        set(GROUP_USERID, userId);
    }

    public String getName() {
        return get(GROUP_NAME);
    }

    public void setName(String name) {
        set(GROUP_NAME, name);
    }

    public String getPicture() {
        return get(GROUP_PICTURE);
    }

    public void setPicture(String picture) {
        set(GROUP_PICTURE, picture);
    }

    public List<String> getMembers() {
        return get(GROUP_MEMBERS);
    }

    public void setMembers(List<String> members) {
        set(GROUP_MEMBERS, members);
    }

    public Boolean getDeleted() {
        return get(GROUP_ISDELETED);
    }

    public void setDeleted(Boolean deleted) {
        set(GROUP_ISDELETED, deleted);
    }
}