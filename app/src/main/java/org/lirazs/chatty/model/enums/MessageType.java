package org.lirazs.chatty.model.enums;

/**
 * Created by mac on 3/17/17.
 */

public enum MessageType {

    STATUS("status"),
    TEXT("text"),
    EMOJI("emoji"),
    PICTURE("picture"),
    VIDEO("video"),
    AUDIO("audio"),
    LOCATION("location");

    private String name;

    MessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MessageType getByName(String name) {
        for (MessageType messageType : values()) {
            if(messageType.getName().equals(name))
                return messageType;
        }
        return null;
    }
}
