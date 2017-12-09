package org.lirazs.chatty.model;


import org.lirazs.chatty.event.ClickHandler;

public class DrawerItem {

    public enum Type {DIVIDER, SUB_OPTIONS_TITLE, OPTION, SUB_OPTION}

    private final Type type;
    private ClickHandler clickHandler;

    public DrawerItem(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }


}