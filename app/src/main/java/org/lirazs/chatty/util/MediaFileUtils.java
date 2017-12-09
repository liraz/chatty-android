package org.lirazs.chatty.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

/**
 * Created by Liraz on 16/06/2017.
 */

public class MediaFileUtils {

    public static long getDuration(Context context, Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        // use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, videoUri);

        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);

        retriever.release();
        return timeInMillisec;
    }
}
