package org.lirazs.chatty.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import org.lirazs.chatty.R;
import org.lirazs.chatty.model.chatkit.DBUserDialogDecorator;
import org.lirazs.chatty.model.realm.DBGroup;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.service.ChatService;
import org.lirazs.chatty.service.GroupsService;
import org.lirazs.chatty.service.UsersService;
import org.lirazs.chatty.util.DateUtils;
import org.lirazs.chatty.util.VoidCallback;
import org.lirazs.chatty.view.AvatarViewUtil;
import org.lirazs.chatty.view.CustomDialogViewHolder;
import com.bumptech.glide.Glide;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;
import com.github.clans.fab.FloatingActionButton;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Liraz on 26/05/2017.
 */

public class GroupProfileActivity extends BaseActivity {

    public static String GROUP_OBJECT_ID_DATA = "groupObjectId";

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.dialogs_list)
    public DialogsList dialogsList;

    @BindView(R.id.dialogAvatar)
    public CircleImageView dialogAvatar;

    @BindView(R.id.dialogAvatarIcon)
    public MaterialLetterIcon dialogAvatarIcon;

    @BindView(R.id.dialogName)
    public TextView dialogName;

    @BindView(R.id.dialogDate)
    public TextView dialogDate;

    @BindView(R.id.new_message)
    public FloatingActionButton newMessage;

    @BindView(R.id.members_count)
    public TextView membersCount;

    @BindView(R.id.add_member)
    public RippleView addMember;

    @Inject
    public UsersService usersService;

    @Inject
    public GroupsService groupsService;

    @Inject
    public ChatService chatService;

    private ImageLoader imageLoader;
    private DialogsListAdapter<DBUserDialogDecorator> dialogsAdapter;

    private String groupObjectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_group_profile);
        injectViews();

        groupObjectId = getIntent().getStringExtra(GROUP_OBJECT_ID_DATA);
        if(groupObjectId == null) {
            groupObjectId = savedInstanceState.getString(GROUP_OBJECT_ID_DATA);
        }
        // something went wrong - finish the activity..
        if(groupObjectId == null) {
            finish();
            return;
        }

        // setting the support toolbar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final DBGroup dbGroup = groupsService.getGroupById(groupObjectId);

        // update page title
        updateTitleDetails(dbGroup);

        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if (imageView != null && url != null && !url.isEmpty()) {
                    Glide.with(GroupProfileActivity.this).load(url).into(imageView);
                }
            }
        };

        dialogsAdapter = new DialogsListAdapter<>(R.layout.item_custom_dialog, CustomDialogViewHolder.class, imageLoader);
        dialogsList.setAdapter(dialogsAdapter);

        List<DBUser> users = usersService.getUsersByIds(dbGroup.getMembersAsList());
        dialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(users));

        membersCount.setText(users.size() + " Members");

        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(GroupProfileActivity.this, SelectContactActivity.class),
                        SelectContactActivity.USER_OBJECT_ID_REQUEST);
            }
        });

        updateMembersList();
    }


    private void updateTitleDetails(DBGroup dbGroup) {

        if(dbGroup.getPicture() != null && !dbGroup.getPicture().isEmpty()) {
            dialogAvatar.setVisibility(View.VISIBLE);
            dialogAvatarIcon.setVisibility(View.GONE);

            Glide.with(this).load(dbGroup.getPicture()).into(dialogAvatar);
        } else {
            dialogAvatar.setVisibility(View.GONE);
            dialogAvatarIcon.setVisibility(View.VISIBLE);

            AvatarViewUtil.renderInitialsUserAvatar(this, dialogAvatarIcon, dbGroup.getName());
        }

        dialogName.setText(dbGroup.getName());
        dialogDate.setText("Last updated at " + DateUtils.format(this, dbGroup.getUpdatedAt()));
    }


    private void updateMembersList() {
        final DBGroup dbGroup = groupsService.getGroupById(groupObjectId);
        List<DBUser> users = usersService.getUsersByIds(dbGroup.getMembersAsList());

        dialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(users));

        membersCount.setText(users.size() + " Members");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(GROUP_OBJECT_ID_DATA, groupObjectId);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SelectContactActivity.USER_OBJECT_ID_REQUEST) {
            if (resultCode == RESULT_OK) {
                String userObjectId = data.getStringExtra(SelectContactActivity.USER_OBJECT_ID_EXTRA);

                // add the user to the group
                groupsService.addGroupMembers(groupObjectId, Arrays.asList(userObjectId), new VoidCallback() {
                    @Override
                    public void complete() {

                        updateMembersList();
                    }
                });
            }
        }
    }
}
