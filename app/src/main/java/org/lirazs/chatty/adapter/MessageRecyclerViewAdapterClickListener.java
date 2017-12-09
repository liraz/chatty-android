package org.lirazs.chatty.adapter;

import android.view.View;

import org.lirazs.chatty.model.realm.DBMessage;

/**
 * Created by mac on 6/17/17.
 */

public interface MessageRecyclerViewAdapterClickListener {

    void onClickUserAvatar(View view, int position, DBMessage message);

    void onClickVideoMessage(View view, int position, DBMessage message);

    void onClickAudioMessage(View view, int position, DBMessage message);

    void onClickLocationMessage(View view, int position, DBMessage message);

    void onClickPictureMessage(View view, int position, DBMessage message);

    void onClickMessage(View view, int position, DBMessage message);
}
