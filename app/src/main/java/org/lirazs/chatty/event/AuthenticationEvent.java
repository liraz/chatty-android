package org.lirazs.chatty.event;

/**
 * Created by mac on 2/17/17.
 */

public class AuthenticationEvent {

    public enum Type {
        USER_LOGGED_IN_SUCCESS,
        USER_LOGGED_IN_ERROR,
        USER_LOGGED_OUT_SUCCESS,
        USER_LOGGED_OUT_ERROR,

        REGISTER_SUCCESS,
        REGISTER_ERROR
    }
    private Type eventType;

    public AuthenticationEvent(Type eventType) {
        this.eventType = eventType;
    }

    public Type getEventType() {
        return eventType;
    }
}
