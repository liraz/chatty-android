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