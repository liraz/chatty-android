package org.lirazs.chatty.model.enums;


public enum KeepMedia {

    WEEK(1),
    MONTH(2),
    FOREVER(3);

    private long value;

    KeepMedia(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public static KeepMedia getByValue(long value) {
        for (KeepMedia keepMedia : values()) {
            if(keepMedia.getValue() == value)
                return keepMedia;
        }
        return null;
    }
}
