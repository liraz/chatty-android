package org.lirazs.chatty.view;

import android.content.Context;

import org.lirazs.chatty.R;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;

/**
 * Created by Liraz on 01/07/2017.
 */

public class AvatarViewUtil {


    public static void renderInitialsUserAvatar(Context context, MaterialLetterIcon materialLetterIcon, String username) {
        int[] materialColors = context.getResources().getIntArray(R.array.colors);

        materialLetterIcon.setOval(true);

        String text = username.toUpperCase();
        if(!text.contains(" ") && text.length() > 1) {
            text = text.charAt(0) + " " + text.charAt(1);
        }

        materialLetterIcon.setText(text);
        materialLetterIcon.setLetterCount(2);

        final int color = Math.abs(materialLetterIcon.getText().hashCode()) % materialColors.length;
        materialLetterIcon.setShapeColor(materialColors[color]);

        materialLetterIcon.setTextSize(18);
    }
}
