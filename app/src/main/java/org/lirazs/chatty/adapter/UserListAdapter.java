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
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.view.UserItemView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;


public class UserListAdapter
        extends RealmBaseAdapter<DBUser> implements ListAdapter {

    public UserListAdapter() {
        super(null);
    }
    public UserListAdapter(OrderedRealmCollection<DBUser> realmResults) {
        super(realmResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserItemView userItemView;
        if (convertView == null) {
            convertView = new UserItemView(parent.getContext());
        }
        userItemView = (UserItemView) convertView;

        if (adapterData != null) {
            final DBUser item = adapterData.get(position);
            userItemView.bind(item);
        }
        return convertView;
    }
}
