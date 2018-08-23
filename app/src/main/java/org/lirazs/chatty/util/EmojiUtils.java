/**
 * Copyright Liraz Shilkrot
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
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
