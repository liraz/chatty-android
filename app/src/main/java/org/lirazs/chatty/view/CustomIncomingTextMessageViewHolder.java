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

public class CustomIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<DBMessageDecorator> {

    private View onlineIndicator;

    private ImageView messageUserAvatar;
    private MaterialLetterIcon messageUserAvatarIcon;

    public CustomIncomingTextMessageViewHolder(View itemView) {
        super(itemView);
        //onlineIndicator = itemView.findViewById(R.id.onlineIndicator);

        messageUserAvatar = (ImageView)itemView.findViewById(R.id.messageUserAvatar);
        messageUserAvatarIcon = (MaterialLetterIcon)itemView.findViewById(R.id.messageUserAvatarIcon);
    }

    @Override
    public void onBind(DBMessageDecorator message) {
        super.onBind(message);

        boolean isOnline = false;//message.getUser().isOnline();
        if (isOnline) {
            //onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
        } else {
            //onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
        }

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
