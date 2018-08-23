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
