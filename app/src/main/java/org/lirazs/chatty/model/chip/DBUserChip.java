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
package org.lirazs.chatty.model.chip;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import org.lirazs.chatty.model.realm.DBUser;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;



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
