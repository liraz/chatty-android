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

import org.lirazs.chatty.model.realm.DBRecent;
import org.lirazs.chatty.view.RecentChatItemView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;


public class RecentChatListAdapter
        extends RealmBaseAdapter<DBRecent> implements ListAdapter {


    public RecentChatListAdapter() {
        super(null);
    }
    public RecentChatListAdapter(OrderedRealmCollection<DBRecent> realmResults) {
        super(realmResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecentChatItemView recentChatItemView;
        if (convertView == null) {
            convertView = new RecentChatItemView(parent.getContext());
        }
        recentChatItemView = (RecentChatItemView) convertView;

        if (adapterData != null) {
            final DBRecent item = adapterData.get(position);
            recentChatItemView.bind(item);
        }
        return convertView;
    }
}
