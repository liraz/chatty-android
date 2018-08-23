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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.lirazs.chatty.R;
import org.lirazs.chatty.model.chatkit.DBUserDialogDecorator;
import org.lirazs.chatty.model.chip.DBUserChip;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.service.ChatService;
import org.lirazs.chatty.service.ContactsService;
import org.lirazs.chatty.view.CustomDialogViewHolder;
import com.bumptech.glide.Glide;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class NewGroupSelectActivity extends BaseActivity implements DialogsListAdapter.OnDialogClickListener<DBUserDialogDecorator>,
        DialogsListAdapter.OnDialogLongClickListener<DBUserDialogDecorator> {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.dialogs_list)
    public DialogsList dialogsList;

    @BindView(R.id.chips_input)
    public ChipsInput chipsInput;

    @Inject
    public ChatService chatService;

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

        setContentView(R.layout.activity_new_group_select);
        injectViews();

        // setting the support toolbar
        toolbar.setTitle("New Group");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if (imageView != null && url != null && !url.isEmpty()) {
                    Glide.with(NewGroupSelectActivity.this).load(url).into(imageView);
                }
            }
        };

        dialogsAdapter = new DialogsListAdapter<>(R.layout.item_custom_dialog, CustomDialogViewHolder.class, imageLoader);

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(dialogsAdapter);

        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chipInterface, int newSize) {
                saveItem.setVisible(newSize > 0);
            }

            @Override
            public void onChipRemoved(ChipInterface chipInterface, int newSize) {
                saveItem.setVisible(newSize > 0);
            }

            @Override
            public void onTextChanged(CharSequence charSequence) {
                dialogsList.setVisibility(charSequence.length() > 0 ? View.GONE : View.VISIBLE);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        usersFromContactsResults = contactsService.findUsersFromContacts();

        usersRealmResultsChangeListener = new RealmChangeListener<RealmResults<DBUser>>() {
            @Override
            public void onChange(RealmResults<DBUser> dbUsers) {
                dialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(dbUsers));
                chipsInput.setFilterableList(DBUserChip.decorateUserList(usersFromContactsResults));
            }
        };
        usersFromContactsResults.addChangeListener(usersRealmResultsChangeListener);

        dialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(usersFromContactsResults));
        chipsInput.setFilterableList(DBUserChip.decorateUserList(usersFromContactsResults));
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
    public void onBackPressed() {
        if(chipsInput.getEditText().getText().length() > 0) {
            chipsInput.getEditText().setText("");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_message, menu);

        saveItem = menu.findItem(R.id.action_save);
        Drawable icon = saveItem.getIcon();
        icon.mutate();
        icon.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null), PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                List<DBUserChip> selectedChipList = (List<DBUserChip>) chipsInput.getSelectedChipList();
                openGroupDetailsWithUsers(DBUserChip.undecorateUserList(selectedChipList));
                return true;

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
        chipsInput.addChip(new DBUserChip(dialog.getUser()));
    }

    private void openGroupDetailsWithUsers(List<DBUser> dbUsers) {
        ArrayList<String> userIds = new ArrayList<>();

        for (DBUser dbUser : dbUsers) {
            userIds.add(dbUser.getObjectId());
        }

        // Start intent for chat activity
        Intent intent = new Intent(this, NewGroupDetailsActivity.class);
        intent.putStringArrayListExtra(NewGroupDetailsActivity.USER_IDS_KEY, userIds);
        startActivity(intent);

        finish();
    }
}
