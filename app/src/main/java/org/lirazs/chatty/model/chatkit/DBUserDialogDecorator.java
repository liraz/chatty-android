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

import org.lirazs.chatty.model.realm.DBUser;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class DBUserDialogDecorator implements IDialog<IMessage> {

    public static List<DBUserDialogDecorator> decorateUserList(List<DBUser> users) {
        List<DBUserDialogDecorator> decorators = new ArrayList<>();
        for (DBUser user : users) {
            decorators.add(new DBUserDialogDecorator(user));
        }
        return decorators;
    }

    private DBUser user;

    public DBUserDialogDecorator(DBUser user) {
        this.user = user;
    }

    public DBUser getUser() {
        return user;
    }

    @Override
    public String getId() {
        return user.getObjectId();
    }

    @Override
    public String getDialogPhoto() {
        return user.getPicture();
    }

    @Override
    public String getDialogName() {
        return user.getSafeName();
    }

    @Override
    public List<? extends IUser> getUsers() {
        return new ArrayList<>();
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
                return null;
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
                return null;
            }
        };
    }

    @Override
    public void setLastMessage(IMessage message) {
    }

    @Override
    public int getUnreadCount() {
        return 0;
    }
}
