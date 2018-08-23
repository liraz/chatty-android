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
import android.view.ViewGroup;

import org.lirazs.chatty.model.realm.DBRecent;
import org.lirazs.chatty.view.RecentChatItemView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class RecentChatRecyclerViewAdapter
        extends RealmRecyclerViewAdapter<DBRecent, RecentChatRecyclerViewAdapter.ViewHolder> {


    public RecentChatRecyclerViewAdapter(OrderedRealmCollection<DBRecent> realmResults) {
        super(realmResults, true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new RecentChatItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DBRecent obj = getItem(position);
        holder.recentChatItemView.bind(obj);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RecentChatItemView recentChatItemView;

        ViewHolder(RecentChatItemView view) {
            super(view);
            this.recentChatItemView = view;
        }
    }
}
