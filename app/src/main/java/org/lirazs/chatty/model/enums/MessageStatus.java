package org.lirazs.chatty.model.enums;

/**
 * Created by mac on 3/17/17.
 */

public enum MessageStatus {

    TEXT_QUEUED("Queued"),
    TEXT_SENT("Sent"),
    TEXT_READ("Read");

    private String name;

    MessageStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MessageStatus getByName(String name) {
        for (MessageStatus messageStatus : values()) {
            if(messageStatus.getName().equals(name))
                return messageStatus;
        }
        return null;
    }
}
