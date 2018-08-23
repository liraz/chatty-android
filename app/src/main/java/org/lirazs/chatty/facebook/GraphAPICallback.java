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
package org.lirazs.chatty.facebook;



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
