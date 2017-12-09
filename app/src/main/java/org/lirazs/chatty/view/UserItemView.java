package org.lirazs.chatty.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lirazs.chatty.R;
import org.lirazs.chatty.adapter.CircleTransform;
import org.lirazs.chatty.model.realm.DBUser;
import com.bumptech.glide.Glide;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liraz on 26/05/2017.
 */

public class UserItemView extends RelativeLayout {

    @BindView(R.id.letter_icon)
    MaterialLetterIcon letterIcon;

    @BindView(R.id.image_icon)
    ImageView imageIcon;

    @BindView(R.id.initials_text)
    TextView initialsText;

    @BindView(R.id.status_text)
    TextView statusText;

    public UserItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.user_item_view, this);
        ButterKnife.bind(this);
    }

    public void bind(DBUser dbUser) {

        if (dbUser.getPicture() != null && !dbUser.getPicture().isEmpty()) {

            Glide.with(getContext()).load(dbUser.getPicture())
                    .override(100, 100)
                    .transform(new CircleTransform(getContext()))
                    .placeholder(R.drawable.ic_contact_picture)
                    .fitCenter()
                    .into(imageIcon);

            letterIcon.setVisibility(GONE);
            imageIcon.setVisibility(VISIBLE);
        } else {
            AvatarViewUtil.renderInitialsUserAvatar(getContext(), letterIcon, dbUser.getSafeName());

            letterIcon.setVisibility(VISIBLE);
            imageIcon.setVisibility(GONE);
        }

        initialsText.setText(dbUser.getSafeName());
        statusText.setText(dbUser.getStatus());
    }
}
