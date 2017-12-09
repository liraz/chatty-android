package org.lirazs.chatty.event;

/**
 * Handler for {@link ClickEvent} events.
 */
public interface ClickHandler extends EventHandler {
    /**
     * Called when a native click event is fired.
     *
     * @param event the {@link ClickEvent} that was fired
     */
    void onClick(ClickEvent event);

}