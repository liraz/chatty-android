package org.lirazs.chatty.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.lirazs.chatty.model.realm.DBRecent;
import org.lirazs.chatty.view.RecentChatItemView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by Liraz on 26/05/2017.
 */

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
