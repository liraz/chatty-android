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
package org.lirazs.chatty.service;

import android.content.Context;

import org.lirazs.chatty.event.AuthenticationEvent;
import org.lirazs.chatty.event.AuthenticationEventListener;
import org.lirazs.chatty.model.enums.MessageType;
import org.lirazs.chatty.model.firebase.FirebaseMessageObject;
import org.lirazs.chatty.model.firebase.FirebaseRecentObject;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.util.FetchCallback;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by mac on 4/11/17.
 */
public class PushNotificationService implements AuthenticationEventListener {

    private Context context;
    private AuthService authService;
    private RecentService recentService;

    public PushNotificationService(Context context, AuthService authService, RecentService recentService) {
        this.context = context;
        this.authService = authService;
        this.recentService = recentService;

        // initialize OneSignal
        OneSignal.startInit(context).init();
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.NONE, OneSignal.LOG_LEVEL.NONE);

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onAuthenticationEvent(AuthenticationEvent authenticationEvent) {
        if(authenticationEvent.getEventType() == AuthenticationEvent.Type.USER_LOGGED_IN_SUCCESS) {
            // Call syncHashedEmail anywhere in your app if you have the user's email.
            // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
            FirebaseUserObject currentUser = authService.getCurrentUser();

            OneSignal.syncHashedEmail(currentUser.getEmail());
        }
    }


    public void sendPushNotificationByMessage(FirebaseMessageObject message){

        MessageType type = message.getType();
        String text = message.getSenderName();
        String groupId = message.getGroupId();

        switch (type) {
            case TEXT:
                text = text + " sent you a text message.";
                break;
            case EMOJI:
                text = text + " sent you an emoji.";
                break;
            case PICTURE:
                text = text + " sent you a picture.";
                break;
            case VIDEO:
                text = text + " sent you a video.";
                break;
            case AUDIO:
                text = text + " sent you an audio.";
                break;
            case LOCATION:
                text = text + " sent you a location.";
                break;
        }

        final String finalText = text;
        recentService.fetchRecentsByGroupId(groupId, new FetchCallback<List<FirebaseRecentObject>>() {
            @Override
            public void complete(List<FirebaseRecentObject> recents) {
                List<String> userIds = new ArrayList<>();

                for (FirebaseRecentObject recent : recents) {
                    if (!recent.getUserId().equals(AuthService.getCurrentId())) {
                        userIds.add(recent.getUserId());
                    }
                }

                sendPushNotificationToUserIds(userIds, finalText);
            }
        });
    }


    public void sendPushNotificationToUserIds(List<String> userIds, String text) {

        List<String> oneSignalIds = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        List<DBUser> dbUsers = realm.where(DBUser.class).findAll();

        for (DBUser dbUser : dbUsers) {

            if(userIds.contains(dbUser.getObjectId())) {
                if(dbUser.getOneSignalId() != null && !dbUser.getOneSignalId().isEmpty()) {
                    oneSignalIds.add(dbUser.getOneSignalId());
                }
            }
        }

        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject contentsJson = new JSONObject();
            contentsJson.put("en", text);

            jsonObject.put("contents", contentsJson);
            jsonObject.put("include_player_ids", new JSONArray(oneSignalIds));

            OneSignal.postNotification(jsonObject, new OneSignal.PostNotificationResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {

                }

                @Override
                public void onFailure(JSONObject response) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
