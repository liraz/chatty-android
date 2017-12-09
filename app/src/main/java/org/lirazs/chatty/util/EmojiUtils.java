package org.lirazs.chatty.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.lirazs.chatty.manager.ChattyEmojiManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EmojiUtils {
    private static final Pattern SPACE_REMOVAL = Pattern.compile("[\\s]");

    /** returns true when the string only contains emojis. Note that whitespace will be filtered out. */
    public static boolean isOnlyEmojis(@Nullable final String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        final String inputWithoutSpaces = SPACE_REMOVAL.matcher(text).replaceAll(Matcher.quoteReplacement(""));

        return ChattyEmojiManager.getInstance()
                .getEmojiRepetitivePattern()
                .matcher(inputWithoutSpaces)
                .matches();
    }

    private EmojiUtils() {
        throw new AssertionError("No instances.");
    }
}
