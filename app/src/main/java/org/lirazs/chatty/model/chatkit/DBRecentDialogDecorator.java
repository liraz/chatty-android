package org.lirazs.chatty.model.chatkit;

import org.lirazs.chatty.model.realm.DBRecent;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Liraz on 21/06/2017.
 */

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
