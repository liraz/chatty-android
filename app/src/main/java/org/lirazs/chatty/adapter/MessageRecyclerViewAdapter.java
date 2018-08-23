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

package org.lirazs.chatty.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.lirazs.chatty.R;
import org.lirazs.chatty.model.enums.MessageType;
import org.lirazs.chatty.model.realm.DBMessage;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.service.UsersService;
import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiEditText;

import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class MessageRecyclerViewAdapter
        extends RealmRecyclerViewAdapter<DBMessage, MessageRecyclerViewAdapter.MessageViewHolder> {

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;

    private MessageRecyclerViewAdapterClickListener listener;
    private UsersService usersService;

    public MessageRecyclerViewAdapter(MessageRecyclerViewAdapterClickListener listener,
                                      UsersService usersService,
                                      OrderedRealmCollection<DBMessage> realmResults) {
        super(realmResults, true);

        this.listener = listener;
        this.usersService = usersService;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right,parent,false);
            return new MessageViewHolder(view);
        }else if (viewType == LEFT_MSG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left,parent,false);
            return new MessageViewHolder(view);
        }else if (viewType == RIGHT_MSG_IMG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img,parent,false);
            return new MessageViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img,parent,false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final DBMessage dbMessage = getItem(position);

        if(dbMessage != null && dbMessage.getPicture() != null) {
            if(dbMessage.getObjectId() != null && dbMessage.getObjectId().equals(AuthService.getCurrentId())) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if(dbMessage != null && dbMessage.getObjectId() != null
                && dbMessage.getObjectId().equals(AuthService.getCurrentId())) {
            return RIGHT_MSG;
        } else {
            return LEFT_MSG;
        }

        //TODO: Support for other files other than images

        //TODO: Support for sending location in message
        /*if (model.getMapModel() != null){
            if (model.getUserModel().getName().equals(nameUser)){
                return RIGHT_MSG_IMG;
            }else{
                return LEFT_MSG_IMG;
            }
        }*/
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        final DBMessage dbMessage = getItem(position);

        if (dbMessage != null) {
            // Get the sender's image by its objectId
            DBUser dbUser = usersService.getUserById(dbMessage.getSenderId());
            holder.setUserImage(dbUser.getPicture());

            holder.setMessageText(dbMessage.getText());
            holder.setTimestampText(dbMessage.getCreatedAt());

            if(dbMessage.getPicture() != null) {
                holder.setMessagePicture(dbMessage.getPicture());
            }
        }


        //TODO: Support for sending location in message
        /*if(model.getMapModel() != null){
            viewHolder.setMessagePicture(Util.local(model.getMapModel().getLatitude(),model.getMapModel().getLongitude()));
            viewHolder.tvIsLocation(View.VISIBLE);
        }*/
    }

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView timestampText;
        TextView locationText;
        EmojiEditText messageText;
        ImageView userImage;
        ImageView messagePicture;

        MessageViewHolder(View itemView) {
            super(itemView);
            timestampText = (TextView)itemView.findViewById(R.id.timestamp_text);
            messageText = (EmojiEditText)itemView.findViewById(R.id.message_text);
            locationText = (TextView)itemView.findViewById(R.id.location_text);
            messagePicture = (ImageView)itemView.findViewById(R.id.message_picture);
            userImage = (ImageView)itemView.findViewById(R.id.user_image);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            DBMessage message = getItem(position);

            if(listener != null && message != null) {
                if(view == userImage) {
                    listener.onClickUserAvatar(view, position, message);
                }
                else if(message.getType() == MessageType.LOCATION) {
                    listener.onClickLocationMessage(view, position, message);
                }
                else if(message.getType() == MessageType.PICTURE) {
                    listener.onClickPictureMessage(view, position, message);
                }
                else if(message.getType() == MessageType.VIDEO) {
                    listener.onClickVideoMessage(view, position, message);
                }
                else if(message.getType() == MessageType.AUDIO) {
                    listener.onClickAudioMessage(view, position, message);
                }
                else {
                    listener.onClickMessage(view, position, message);
                }
            }
        }

        void setMessageText(String message){
            if (messageText != null) {
                messageText.setText(message);
            }
        }

        void setUserImage(String urlPhotoUser){
            if (userImage != null) {
                Glide.with(userImage.getContext()).load(urlPhotoUser)
                        .centerCrop().transform(new CircleTransform(userImage.getContext()))
                        .override(40, 40).into(userImage);
            }
        }

        void setTimestampText(Date timestamp){
            if (timestampText != null) {
                timestampText.setText(DateUtils.getRelativeTimeSpanString(
                        timestamp.getTime(),
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            }
        }

        public void setMessagePicture(String url){
            if (messagePicture != null) {
                Glide.with(messagePicture.getContext()).load(url)
                        .override(100, 100)
                        .fitCenter()
                        .into(messagePicture);
                messagePicture.setOnClickListener(this);
            }
        }
    }
}
