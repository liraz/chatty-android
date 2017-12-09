package org.lirazs.chatty.model.chatkit;

import org.lirazs.chatty.model.realm.DBUser;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Liraz on 21/06/2017.
 */

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
