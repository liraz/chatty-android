package org.lirazs.chatty.event.service;

/**
 * Created by mac on 2/17/17.
 */

public class GroupServiceEvent {

    public enum Type {
        GROUPS_UPDATED
    }
    private Type eventType;

    public GroupServiceEvent(Type eventType) {
        this.eventType = eventType;
    }

    public Type getEventType() {
        return eventType;
    }
}
