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
package org.lirazs.chatty.model.chatkit;

import org.lirazs.chatty.model.realm.DBRecent;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



public class DBRecentDialogDecorator implements IDialog<IMessage> {

    public static List<DBRecentDialogDecorator> decorateRecentList(List<DBRecent> recents) {
        List<DBRecentDialogDecorator> decorators = new ArrayList<>();
        for (DBRecent recent : recents) {
            decorators.add(new DBRecentDialogDecorator(recent));
        }
        return decorators;
    }

    private DBRecent recent;

    public DBRecentDialogDecorator(DBRecent recent) {
        this.recent = recent;
    }

    public DBRecent getRecent() {
        return recent;
    }

    @Override
    public String getId() {
        return recent.getObjectId();
    }

    @Override
    public String getDialogPhoto() {
        return recent.getPicture();
    }

    @Override
    public String getDialogName() {
        return recent.getDescription();
    }

    @Override
    public List<? extends IUser> getUsers() {
        List<String> members = Arrays.asList(recent.getMembers().split(","));
        List<IUser> users = new ArrayList<>();

        for (final String member : members) {
            users.add(new IUser() {
                @Override
                public String getId() {
                    return null;
                }

                @Override
                public String getName() {
                    return member;
                }

                @Override
                public String getAvatar() {
                    return null;
                }
            });
        }

        return users;
    }

    @Override
    public IMessage getLastMessage() {
        return new IMessage() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getText() {
                return recent.getLastMessage();
            }

            @Override
            public IUser getUser() {
                return new IUser() {
                    @Override
                    public String getId() {
                        return null;
                    }

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public String getAvatar() {
                        return null;
                    }
                };
            }

            @Override
            public Date getCreatedAt() {
                return recent.getLastMessageDate();
            }
        };
    }

    @Override
    public void setLastMessage(IMessage message) {
        recent.setLastMessage(message.getText());
        recent.setLastMessageDate(message.getCreatedAt());
    }

    @Override
    public int getUnreadCount() {
        Long counter = recent.getCounter();
        return counter != null ? counter.intValue() : 0;
    }
}
