package org.lirazs.chatty.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.view.UserItemView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Liraz on 26/05/2017.
 */

public class UserRecyclerViewAdapter
        extends RealmRecyclerViewAdapter<DBUser, UserRecyclerViewAdapter.ViewHolder> {


    public UserRecyclerViewAdapter(OrderedRealmCollection<DBUser> realmResults) {
        super(realmResults, true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new UserItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DBUser obj = getItem(position);
        holder.userItemView.bind(obj);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        UserItemView userItemView;

        ViewHolder(UserItemView view) {
            super(view);
            this.userItemView = view;
        }
    }
}
