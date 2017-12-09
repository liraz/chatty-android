package org.lirazs.chatty.model.enums;


public enum NetworkMode {

    MANUAL(1),
    WIFI(2),
    ALL(3);

    private long value;

    NetworkMode(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public static NetworkMode getByValue(long value) {
        for (NetworkMode networkMode : values()) {
            if(networkMode.getValue() == value)
                return networkMode;
        }
        return null;
    }
}
