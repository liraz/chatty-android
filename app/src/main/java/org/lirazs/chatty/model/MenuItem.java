package org.lirazs.chatty.model;

import org.lirazs.chatty.event.Event;

/**
 * Created on 9/18/2015.
 */
public class MenuItem extends DrawerOption {
    private Event dispatchEvent;

    public MenuItem(Type type) {
        super(type);
    }

    public Event getDispatchEvent() {
        return dispatchEvent;
    }

    public void setDispatchEvent(Event dispatchEvent) {
        this.dispatchEvent = dispatchEvent;
    }
}
