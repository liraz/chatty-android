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

package org.lirazs.chatty.event.service;

import org.lirazs.chatty.model.realm.DBMessage;

/**
 * Created by mac on 2/17/17.
 */

public class MessagesServiceEvent {

    public enum Type {
        MESSAGE_UPDATED,
        MESSAGE_ADDED,
        MESSAGE_DELETED
    }
    private Type eventType;
    private DBMessage dbMessage;

    public MessagesServiceEvent(Type eventType, DBMessage dbMessage) {
        this.eventType = eventType;
        this.dbMessage = dbMessage;
    }

    public Type getEventType() {
        return eventType;
    }

    public DBMessage getDbMessage() {
        return dbMessage;
    }
}
