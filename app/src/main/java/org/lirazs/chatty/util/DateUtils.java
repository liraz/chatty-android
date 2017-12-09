package org.lirazs.chatty.util;

import android.content.Context;

import org.lirazs.chatty.R;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;

/**
 * Created by mac on 6/24/17.
 */

public class DateUtils {

    public static Date objectToDate(Object object) {

        if (object != null) {
            if(object instanceof Long) {
                return new Date((long)object);
            } else if(object instanceof Double) {
                return new Date(((Double)object).longValue());
            } else if(object instanceof Date) {
                return (Date) object;
            }
        }
        return null;
    }

    public static String format(Context context, Date date) {
        if (DateFormatter.isToday(date)) {
            return context.getString(R.string.date_header_today);
        } else if (DateFormatter.isYesterday(date)) {
            return context.getString(R.string.date_header_yesterday);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }
}
