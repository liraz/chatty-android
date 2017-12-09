package org.lirazs.chatty.event;

/**
 * Created by Liraz on 6/27/2015.
 */
public class ClickEvent extends Event<ClickHandler> {

    protected void dispatch(ClickHandler handler) {
        handler.onClick(this);
    }
}
