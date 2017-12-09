package org.lirazs.chatty.view;

/**
 * Created by Liraz on 01/07/2017.
 */

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
