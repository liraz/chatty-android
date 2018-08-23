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

import org.lirazs.chatty.model.enums.ChatType;
import org.lirazs.chatty.model.firebase.FirebaseGroupObject;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.model.realm.DBGroup;
import org.lirazs.chatty.model.realm.DBRecent;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.model.request.ChatRequest;
import org.lirazs.chatty.util.FetchCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mac on 3/24/17.
 */

public class ChatService {

    private Context context;
    private AuthService authService;
    private RecentService recentService;

    public ChatService(Context context, AuthService authService, RecentService recentService) {
        this.context = context;
        this.authService = authService;
        this.recentService = recentService;
    }


    public ChatRequest generatePrivateChatRequest(DBUser dbUser2) {

        FirebaseUserObject user1 = authService.getCurrentUser();

        final String userId1 = user1.getObjectId();
        final String userId2 = dbUser2.getObjectId();

        final String initials1 = user1.getInitials();
        final String initials2 = dbUser2.getInitials();

        final String picture1 = user1.getPicture();
        final String picture2 = dbUser2.getPicture();

        final String name1 = user1.getSafeName();
        final String name2 = dbUser2.getSafeName();

        final List<String> members = Arrays.asList(userId1, userId2);

        final String groupId = GroupsService.generateGroupIdByMembers(members);

        recentService.fetchRecentGroupMembersIds(groupId, new FetchCallback<List<String>>() {
            @Override
            public void complete(List<String> userIds) {
                if(!userIds.contains(userId1)) {
                    recentService.createPrivateChatRecentItem(userId1, groupId, initials2, picture2, name2, members);
                }
                if(!userIds.contains(userId2)) {
                    recentService.createPrivateChatRecentItem(userId2, groupId, initials1, picture1, name1, members);
                }
            }
        });

        return new ChatRequest(groupId, members, ChatType.PRIVATE);
    }


    public ChatRequest generateMultipleChatRequest(List<String> userIds) {

        List<String> members = new ArrayList<>(userIds);
        members.add(AuthService.getCurrentId());

        final String groupId = GroupsService.generateGroupIdByMembers(members);

        recentService.createMultipleChatRecentItem(groupId, members);

        return new ChatRequest(groupId, members, ChatType.MULTIPLE);
    }


    public ChatRequest generateGroupChatRequest(FirebaseGroupObject groupObject) {

        List<String> members = groupObject.getMembers();
        String groupId = groupObject.getObjectId();

        //FirebaseUserObject currentUser = authService.getCurrentUser();
        String picture = groupObject.getPicture()/* != null ? groupObject.getPicture() : currentUser.getPicture()*/;

        recentService.createGroupChatRecentItem(groupId, picture, groupObject.getName(), members);

        return new ChatRequest(groupId, members, ChatType.GROUP);
    }

    public ChatRequest generateGroupChatRequest(DBGroup dbGroup) {
        List<String> members = Arrays.asList(dbGroup.getMembers().split(","));
        String groupId = dbGroup.getObjectId();

        FirebaseUserObject currentUser = authService.getCurrentUser();
        String picture = dbGroup.getPicture() != null ? dbGroup.getPicture() : currentUser.getPicture();

        recentService.createGroupChatRecentItem(groupId, picture, dbGroup.getName(), members);

        return new ChatRequest(groupId, members, ChatType.GROUP);
    }

    public ChatRequest generateRestartRecentChatRequest(DBRecent dbRecent) {
        List<String> members = Arrays.asList(dbRecent.getMembers().split(","));

        return new ChatRequest(dbRecent.getGroupId(), members, dbRecent.getType());
    }

}
