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
package org.lirazs.chatty.manager;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vanniktech.emoji.EmojiProvider;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.emoji.EmojiCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static dagger.internal.Preconditions.checkNotNull;

public final class ChattyEmojiManager {
    private static final ChattyEmojiManager INSTANCE = new ChattyEmojiManager();
    private static final int GUESSED_UNICODE_AMOUNT = 3000;
    private static final int GUESSED_TOTAL_PATTERN_LENGTH = GUESSED_UNICODE_AMOUNT * 4;

    private static final Comparator<String> STRING_LENGTH_COMPARATOR = new Comparator<String>() {
        @Override public int compare(final String first, final String second) {
            final int firstLength = first.length();
            final int secondLength = second.length();

            return firstLength < secondLength ? 1 : firstLength == secondLength ? 0 : -1;
        }
    };

    private final Map<String, Emoji> emojiMap = new LinkedHashMap<>(GUESSED_UNICODE_AMOUNT);
    private EmojiCategory[] categories;
    private Pattern emojiPattern;
    private Pattern emojiRepetitivePattern;

    private ChattyEmojiManager() {
        // No instances apart from singleton.
    }

    public static ChattyEmojiManager getInstance() {
        return INSTANCE;
    }

    /**
     * Installs the given EmojiProvider.
     *
     * NOTE: That only one can be present at any time.
     *
     * @param provider the provider that should be installed.
     */
    public static void install(@NonNull final EmojiProvider provider) {
        INSTANCE.categories = checkNotNull(provider.getCategories(), "categories == null");
        INSTANCE.emojiMap.clear();

        final List<String> unicodesForPattern = new ArrayList<>(GUESSED_UNICODE_AMOUNT);

        final int categoriesSize = INSTANCE.categories.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < categoriesSize; i++) {
            final Emoji[] emojis = checkNotNull(INSTANCE.categories[i].getEmojis(), "emojies == null");

            final int emojisSize = emojis.length;
            //noinspection ForLoopReplaceableByForEach
            for (int j = 0; j < emojisSize; j++) {
                final Emoji emoji = emojis[j];
                final String unicode = emoji.getUnicode();

                INSTANCE.emojiMap.put(unicode, emoji);
                unicodesForPattern.add(unicode);
            }
        }

        if (unicodesForPattern.isEmpty()) {
            throw new IllegalArgumentException("Your EmojiProvider must at least have one category with at least one emoji.");
        }

        // We need to sort the unicodes by length so the longest one gets matched first.
        Collections.sort(unicodesForPattern, STRING_LENGTH_COMPARATOR);

        final StringBuilder patternBuilder = new StringBuilder(GUESSED_TOTAL_PATTERN_LENGTH);

        final int unicodesForPatternSize = unicodesForPattern.size();
        for (int i = 0; i < unicodesForPatternSize; i++) {
            patternBuilder.append(Pattern.quote(unicodesForPattern.get(i))).append('|');
        }

        final String regex = patternBuilder.deleteCharAt(patternBuilder.length() - 1).toString();
        INSTANCE.emojiPattern = Pattern.compile(regex);
        INSTANCE.emojiRepetitivePattern = Pattern.compile('(' + regex + ")+");
    }

    static void destroy() {
        INSTANCE.emojiMap.clear();
        INSTANCE.categories = null;
        INSTANCE.emojiPattern = null;
        INSTANCE.emojiRepetitivePattern = null;
    }


    EmojiCategory[] getCategories() {
        verifyInstalled();
        return categories; // NOPMD
    }

    public Pattern getEmojiRepetitivePattern() {
        return emojiRepetitivePattern;
    }


    @Nullable Emoji findEmoji(@NonNull final CharSequence candidate) {
        verifyInstalled();

        // We need to call toString on the candidate, since the emojiMap may not find the requested entry otherwise, because
        // the type is different.
        return emojiMap.get(candidate.toString());
    }

    void verifyInstalled() {
        if (categories == null) {
            throw new IllegalStateException("Please install an EmojiProvider through the EmojiManager.install() method first.");
        }
    }
}
