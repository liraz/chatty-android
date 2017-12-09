package org.lirazs.chatty.event;

/**
 * Created by mac on 2/17/17.
 */

public class ApplicationEvent {

    public enum ApplicationEventType {
        APPLICATION_STARTED
    }
    private ApplicationEventType eventType;

    public ApplicationEvent(ApplicationEventType eventType) {
        this.eventType = eventType;
    }

    public ApplicationEventType getEventType() {
        return eventType;
    }
}
