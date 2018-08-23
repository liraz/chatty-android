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
package org.lirazs.chatty.view;



import android.view.View;
import android.widget.ImageView;

import org.lirazs.chatty.R;
import org.lirazs.chatty.model.chatkit.DBMessageDecorator;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomOutcomingTextMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<DBMessageDecorator> {

    private ImageView messageUserAvatar;
    private MaterialLetterIcon messageUserAvatarIcon;

    public CustomOutcomingTextMessageViewHolder(View itemView) {
        super(itemView);

        messageUserAvatar = (ImageView)itemView.findViewById(R.id.messageUserAvatar);
        messageUserAvatarIcon = (MaterialLetterIcon)itemView.findViewById(R.id.messageUserAvatarIcon);
    }

    @Override
    public void onBind(DBMessageDecorator message) {
        super.onBind(message);

        //time.setText(message.getStatus() + " " + time.getText());

        if(message.getUser().getAvatar() != null && !message.getUser().getAvatar().isEmpty()) {
            messageUserAvatar.setVisibility(View.VISIBLE);
            messageUserAvatarIcon.setVisibility(View.GONE);

            getImageLoader().loadImage(messageUserAvatar, message.getUser().getAvatar());

        } else if(message.getUser().getName() != null) {
            messageUserAvatar.setVisibility(View.GONE);
            messageUserAvatarIcon.setVisibility(View.VISIBLE);

            AvatarViewUtil.renderInitialsUserAvatar(itemView.getContext(), messageUserAvatarIcon, message.getUser().getName());
        }
    }
}
