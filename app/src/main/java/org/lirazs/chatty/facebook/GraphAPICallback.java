package org.lirazs.chatty.facebook;

/**
 * Created by Liraz on 11/07/2017.
 */

import com.facebook.FacebookRequestError;
import com.facebook.GraphResponse;

/**
 * Callback for Graph API calls made using GraphAPICall object to easily handle
 * successful responses and errors.
 */
public interface GraphAPICallback {
    /**
     * Called when GraphAPICall returned successfully.
     */
    void handleResponse(GraphResponse response);

    /**
     * Called when GraphAPICall returned with an error.
     */
    void handleError(FacebookRequestError error);
}
