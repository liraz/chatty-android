package org.lirazs.chatty.event.service;

/**
 * Created by mac on 2/17/17.
 */

public class UserServiceEvent {

    public enum Type {
        USERS_UPDATED
    }
    private Type eventType;

    public UserServiceEvent(Type eventType) {
        this.eventType = eventType;
    }

    public Type getEventType() {
        return eventType;
    }
}
