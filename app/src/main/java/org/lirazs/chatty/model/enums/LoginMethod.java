package org.lirazs.chatty.model.enums;


public enum LoginMethod {

    EMAIL("Email"),
    FACEBOOK("Facebook"),
    GOOGLE("Google"),
    PHONE("Phone");

    private String name;

    LoginMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static LoginMethod getByName(String name) {
        for (LoginMethod loginMethod : values()) {
            if(loginMethod.getName().equals(name))
                return loginMethod;
        }
        return null;
    }
}
