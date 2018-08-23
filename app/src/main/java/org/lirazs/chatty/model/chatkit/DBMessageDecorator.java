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

import org.lirazs.chatty.model.realm.DBMessage;
import org.lirazs.chatty.util.LocationUtils;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class DBMessageDecorator implements IMessage, MessageContentType.Image {

    public static List<DBMessageDecorator> decorateMessageList(List<DBMessage> messages) {
        List<DBMessageDecorator> decorators = new ArrayList<>();
        for (DBMessage message : messages) {
            decorators.add(new DBMessageDecorator(message));
        }
        return decorators;
    }

    private DBMessage message;

    public DBMessageDecorator(DBMessage message) {
        this.message = message;
    }

    @Override
    public String getId() {
        return this.message.getObjectId();
    }

    @Override
    public String getText() {
        return this.message.getText();
    }

    @Override
    public IUser getUser() {
        return new IUser() {
            @Override
            public String getId() {
                return message.getSenderId();
            }

            @Override
            public String getName() {
                return message.getSenderName();
            }

            @Override
            public String getAvatar() {
                return message.getSenderThumbnail();
            }
        };
    }

    @Override
    public Date getCreatedAt() {
        return message.getCreatedAt();
    }

    @Override
    public String getImageUrl() {
        boolean hasLocation = message.getLatitude() != null && message.getLongitude() != null
                && message.getLatitude() != 0 && message.getLongitude() != 0;

        return hasLocation ?
                LocationUtils.generateGoogleMapsUrl(message.getLatitude().toString(),
                        message.getLongitude().toString())
                : message.getPicture();
    }
}
