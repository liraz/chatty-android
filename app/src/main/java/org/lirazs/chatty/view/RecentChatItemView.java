package org.lirazs.chatty.view;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lirazs.chatty.R;
import org.lirazs.chatty.model.realm.DBRecent;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liraz on 26/05/2017.
 */

public class RecentChatItemView extends RelativeLayout {

    @BindView(R.id.icon)
    MaterialLetterIcon icon;

    @BindView(R.id.group_name)
    TextView groupNameText;

    @BindView(R.id.last_message)
    TextView lastMessageText;

    @BindView(R.id.unread_counter)
    TextView unreadCounterText;

    public RecentChatItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.recent_chat_item_view, this);
        ButterKnife.bind(this);
    }

    public void bind(DBRecent recent) {
        AvatarViewUtil.renderInitialsUserAvatar(getContext(), icon, recent.getDescription());

        groupNameText.setText(recent.getInitials());
        lastMessageText.setText(recent.getLastMessage());

        unreadCounterText.setVisibility(recent.getCounter() != null && recent.getCounter() > 0 ? VISIBLE : GONE);
        if (recent.getCounter() != null) {
            unreadCounterText.setText(recent.getCounter().toString());
        }
    }
}
