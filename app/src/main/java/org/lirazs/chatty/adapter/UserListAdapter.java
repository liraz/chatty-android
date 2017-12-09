package org.lirazs.chatty.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.view.UserItemView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by Liraz on 26/05/2017.
 */

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
