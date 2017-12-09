package org.lirazs.chatty.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.lirazs.chatty.model.realm.DBRecent;
import org.lirazs.chatty.view.RecentChatItemView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Liraz on 26/05/2017.
 */

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
