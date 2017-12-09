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
