package org.lirazs.chatty.event.service;

/**
 * Created by mac on 2/17/17.
 */

public class RecentServiceEvent {

    public enum Type {
        RECENTS_UPDATED
    }
    private Type eventType;

    public RecentServiceEvent(Type eventType) {
        this.eventType = eventType;
    }

    public Type getEventType() {
        return eventType;
    }
}
