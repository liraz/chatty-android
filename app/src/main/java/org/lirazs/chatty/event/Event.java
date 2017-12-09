package org.lirazs.chatty.event;

/**
 * Created by Liraz on 6/27/2015.
 */
public abstract class Event<H> {

    /**
     * Constructor.
     */
    protected Event() {
    }

    /**
     * Implemented by subclasses to to invoke their handlers in a type safe
     * manner. Intended to be called by {@link EventBus#fireEvent(Event)} or
     * {@link EventBus#fireEventFromSource(Event, Object)}.
     *
     * @param handler handler
     * @see EventBus#dispatchEvent(Event, Object)
     */
    protected abstract void dispatch(H handler);
}
