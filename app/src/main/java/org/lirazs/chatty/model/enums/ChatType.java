package org.lirazs.chatty.model.enums;

/**
 * Created by mac on 3/24/17.
 */

public enum ChatType {
    PRIVATE("private"),
    MULTIPLE("multiple"),
    GROUP("group");


    private String name;

    ChatType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ChatType getByName(String name) {
        for (ChatType chatType : values()) {
            if(chatType.getName().equals(name))
                return chatType;
        }
        return null;
    }
}
