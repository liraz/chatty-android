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
