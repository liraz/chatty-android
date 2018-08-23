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
package org.lirazs.chatty.model.enums;

/**
 * Created by mac on 3/17/17.
 */

public enum MessageType {

    STATUS("status"),
    TEXT("text"),
    EMOJI("emoji"),
    PICTURE("picture"),
    VIDEO("video"),
    AUDIO("audio"),
    LOCATION("location");

    private String name;

    MessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MessageType getByName(String name) {
        for (MessageType messageType : values()) {
            if(messageType.getName().equals(name))
                return messageType;
        }
        return null;
    }
}
