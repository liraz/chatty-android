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

public enum MessageStatus {

    TEXT_QUEUED("Queued"),
    TEXT_SENT("Sent"),
    TEXT_READ("Read");

    private String name;

    MessageStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MessageStatus getByName(String name) {
        for (MessageStatus messageStatus : values()) {
            if(messageStatus.getName().equals(name))
                return messageStatus;
        }
        return null;
    }
}
