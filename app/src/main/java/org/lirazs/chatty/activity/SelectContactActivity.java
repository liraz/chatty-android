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

package org.lirazs.chatty.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.lirazs.chatty.R;
import org.lirazs.chatty.model.chatkit.DBUserDialogDecorator;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.service.ContactsService;
import org.lirazs.chatty.view.CustomDialogViewHolder;
import com.bumptech.glide.Glide;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class SelectContactActivity extends BaseActivity implements DialogsListAdapter.OnDialogClickListener<DBUserDialogDecorator>,
        DialogsListAdapter.OnDialogLongClickListener<DBUserDialogDecorator> {

    public static int USER_OBJECT_ID_REQUEST = 1;
    public static String USER_OBJECT_ID_EXTRA = "userObjectId";

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.dialogs_list)
    public DialogsList dialogsList;

    @Inject
    public ContactsService contactsService;

    private ImageLoader imageLoader;
    private DialogsListAdapter<DBUserDialogDecorator> dialogsAdapter;

    private RealmResults<DBUser> usersFromContactsResults;
    private RealmChangeListener<RealmResults<DBUser>> usersRealmResultsChangeListener;

    private MenuItem saveItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_select_contact);
        injectViews();

        // setting the support toolbar
        toolbar.setTitle("Select Contact");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if (imageView != null && url != null && !url.isEmpty()) {
                    Glide.with(SelectContactActivity.this).load(url).into(imageView);
                }
            }
        };

        dialogsAdapter = new DialogsListAdapter<>(R.layout.item_custom_dialog, CustomDialogViewHolder.class, imageLoader);

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(dialogsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        usersFromContactsResults = contactsService.findUsersFromContacts();

        usersRealmResultsChangeListener = new RealmChangeListener<RealmResults<DBUser>>() {
            @Override
            public void onChange(RealmResults<DBUser> dbUsers) {
                dialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(dbUsers));
            }
        };
        usersFromContactsResults.addChangeListener(usersRealmResultsChangeListener);

        dialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(usersFromContactsResults));
    }

    @Override
    public void onStop() {
        super.onStop();

        usersFromContactsResults.removeChangeListener(usersRealmResultsChangeListener);
    }


    /*
     * It is good practice to null the reference from the view to the adapter when it is no longer needed.
     * Because the <code>RealmRecyclerViewAdapter</code> registers itself as a <code>RealmResult.ChangeListener</code>
     * the view may still be reachable if anybody is still holding a reference to the <code>RealmResult>.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogLongClick(DBUserDialogDecorator dialog) {
        Toast.makeText(this, dialog.getDialogName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogClick(DBUserDialogDecorator dialog) {
        DBUser user = dialog.getUser();

        Intent data = new Intent();
        data.putExtra(USER_OBJECT_ID_EXTRA, user.getObjectId());
        setResult(RESULT_OK, data);
        finish();
    }
}
