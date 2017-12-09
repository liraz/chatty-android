package org.lirazs.chatty.util;

/**
 * Created by Liraz on 14/06/2017.
 */

public class LocationUtils {

    public static String generateGoogleMapsUrl(String lat, String lng){
        return "https://maps.googleapis.com/maps/api/staticmap?center="
                + lat +
                "," + lng + "&zoom=18&size=280x280&markers=color:red|" + lat + "," + lng;
    }
}
