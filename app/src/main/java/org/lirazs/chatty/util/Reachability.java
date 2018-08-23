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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A utility class for determining network availability.
 */
public class Reachability {

    /**
     * A convienence method for retreiving the ConnectivityManager in a given
     * context.
     *
     * @param context
     * @return the ConnectivityManager instance for the given context.
     */
    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * A convienence method for retreiving the {@link NetworkInfo} for the
     * currently active network.
     *
     * @param context
     * @return a {@link NetworkInfo} instance for the currently active network.
     */
    public static NetworkInfo getActiveNetworkInfo(Context context) {
        return getConnectivityManager(context).getActiveNetworkInfo();
    }

    /**
     * A convienence method for determining if networking is available for the
     * currently active network connection.
     *
     * @param context
     * @return true if and only if the currently active network is available and
     *         connected.
     */
    public static boolean isNetworkingAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return (info.isAvailable() && info.isConnected());
    }

}
