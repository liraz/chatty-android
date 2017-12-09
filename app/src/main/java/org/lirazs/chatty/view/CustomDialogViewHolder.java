package org.lirazs.chatty.view;

import android.view.View;

import org.lirazs.chatty.R;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

/*
 * Created by Anton Bevza on 1/18/17.
 */
public class CustomDialogViewHolder
        extends DialogsListAdapter.DialogViewHolder<IDialog> {

    //private View onlineIndicator;
    private View dialogAvatar;
    private MaterialLetterIcon dialogAvatarIcon;

    public CustomDialogViewHolder(View itemView) {
        super(itemView);
        //onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
        dialogAvatar = itemView.findViewById(R.id.dialogAvatar);
        dialogAvatarIcon = (MaterialLetterIcon)itemView.findViewById(R.id.dialogAvatarIcon);
    }

    @Override
    public void onBind(IDialog dialog) {
        super.onBind(dialog);


        /*if (dialog.getUsers().size() > 1) {
            onlineIndicator.setVisibility(View.GONE);
        } else {
            boolean isOnline = dialog.getUsers().get(0).isOnline();
            onlineIndicator.setVisibility(View.VISIBLE);
            if (isOnline) {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
            } else {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
            }
        }*/

        if(dialog.getDialogPhoto() != null && !dialog.getDialogPhoto().isEmpty()) {
            dialogAvatar.setVisibility(View.VISIBLE);
            dialogAvatarIcon.setVisibility(View.GONE);

        } else if(dialog.getDialogName() != null) {
            dialogAvatar.setVisibility(View.GONE);
            dialogAvatarIcon.setVisibility(View.VISIBLE);

            AvatarViewUtil.renderInitialsUserAvatar(itemView.getContext(), dialogAvatarIcon, dialog.getDialogName());
        }

        //TODO: dialogLastMessageUserAvatar - support for user associated with last message

    }
}