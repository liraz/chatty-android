package org.lirazs.chatty.model.chip;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import org.lirazs.chatty.model.realm.DBUser;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liraz on 06/07/2017.
 */

public class DBUserChip implements ChipInterface {
    public static List<DBUserChip> decorateUserList(List<DBUser> users) {
        List<DBUserChip> decorators = new ArrayList<>();
        for (DBUser user : users) {
            decorators.add(new DBUserChip(user));
        }
        return decorators;
    }
    public static List<DBUser> undecorateUserList(List<DBUserChip> userChips) {
        List<DBUser> users = new ArrayList<>();
        for (DBUserChip user : userChips) {
            users.add(user.getDbUser());
        }
        return users;
    }

    private DBUser dbUser;

    private Object id;
    private Uri avatarUri;
    private String label;
    private String info;

    public DBUserChip(DBUser dbUser) {
        this.dbUser = dbUser;

        id = dbUser.getObjectId();
        avatarUri = dbUser.getPicture() != null ? Uri.parse(dbUser.getPicture()) : null;
        label = dbUser.getSafeName();
        info = dbUser.getEmail();
    }

    public DBUser getDbUser() {
        return dbUser;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        return avatarUri;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getInfo() {
        return info;
    }
}
