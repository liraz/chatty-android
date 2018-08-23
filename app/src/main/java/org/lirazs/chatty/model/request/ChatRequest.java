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
package org.lirazs.chatty.model.request;

import org.lirazs.chatty.model.enums.ChatType;
import org.lirazs.chatty.service.AuthService;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 3/24/17.
 */

public class ChatRequest implements Serializable {

    private String groupId;
    private List<String> members;
    private ChatType type;

    public ChatRequest(String groupId, List<String> members, ChatType type) {
        this.groupId = groupId;
        this.members = members;
        this.type = type;
    }

    public String getUserIdForPrivateChat() {
        String userId = null;
        if(isPrivateChat() && members != null && members.size() > 1) {
            String userId1 = members.get(0); // first
            String userId2 = members.get(members.size() - 1); // last

            if(userId1.equals(AuthService.getCurrentId())) {
                userId = userId2;
            }
            if(userId2.equals(AuthService.getCurrentId())) {
                userId = userId1;
            }
        }

        return userId;
    }

    public boolean isGroupChat() {
        return type != null && type == ChatType.GROUP;
    }
    public boolean isMultipleChat() {
        return type != null && type == ChatType.MULTIPLE;
    }
    public boolean isPrivateChat() {
        return type != null && type == ChatType.PRIVATE;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }
}
