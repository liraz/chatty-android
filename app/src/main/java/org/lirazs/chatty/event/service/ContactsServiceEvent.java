package org.lirazs.chatty.event.service;

/**
 * Created by mac on 2/17/17.
 */

public class ContactsServiceEvent {

    public enum Type {
        CONTACTS_UPDATED
    }
    private Type eventType;

    public ContactsServiceEvent(Type eventType) {
        this.eventType = eventType;
    }

    public Type getEventType() {
        return eventType;
    }
}
