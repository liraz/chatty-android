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

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lirazs.chatty.R;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.service.ChatService;
import org.lirazs.chatty.service.UsersService;
import org.lirazs.chatty.util.DateUtils;
import org.lirazs.chatty.view.AvatarViewUtil;
import com.bumptech.glide.Glide;
import com.github.akashandroid90.imageletter.MaterialLetterIcon;
import com.github.clans.fab.FloatingActionButton;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileActivity extends BaseActivity {

    public static String USER_ID_DATA = "userObjectId";

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.dialogAvatarContainer)
    public RelativeLayout dialogAvatarContainer;

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

    @BindView(R.id.country_container)
    public LinearLayout countryContainer;

    @BindView(R.id.email_container)
    public LinearLayout emailContainer;

    @BindView(R.id.phone_container)
    public LinearLayout phoneContainer;

    @BindView(R.id.status_container)
    public LinearLayout statusContainer;

    @BindView(R.id.no_details_container)
    public LinearLayout noDetailsContainer;

    @BindView(R.id.status_text)
    public TextView statusText;

    @BindView(R.id.phone_text)
    public TextView phoneText;

    @BindView(R.id.email_text)
    public TextView emailText;

    @BindView(R.id.country_text)
    public TextView countryText;

    @Inject
    public UsersService usersService;

    @Inject
    public ChatService chatService;

    private String userObjectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_user_profile);
        injectViews();

        userObjectId = getIntent().getStringExtra(USER_ID_DATA);
        if(userObjectId == null) {
            userObjectId = savedInstanceState.getString(USER_ID_DATA);
        }
        // something went wrong - finish the activity..
        if(userObjectId == null) {
            finish();
            return;
        }

        // setting the support toolbar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final DBUser dbUser = usersService.getUserById(userObjectId);

        // update page title
        updateTitleDetails(dbUser);

        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ChatRequest chatRequest = chatService.generatePrivateChatRequest(dbUser);

                Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                intent.putExtra(ChatRequest.class.getCanonicalName(), chatRequest);
                startActivity(intent);*/

                finish();
            }
        });

        boolean noDetails = true;

        if(dbUser.getStatus() != null) {
            statusContainer.setVisibility(View.VISIBLE);
            statusText.setText(dbUser.getStatus());

            noDetails = false;
        } else {
            statusContainer.setVisibility(View.GONE);
        }

        if(dbUser.getPhone() != null) {
            phoneContainer.setVisibility(View.VISIBLE);
            phoneText.setText(dbUser.getPhone());

            noDetails = false;
        } else {
            phoneContainer.setVisibility(View.GONE);
        }

        if(dbUser.getCountry() != null) {
            countryContainer.setVisibility(View.VISIBLE);
            countryText.setText(dbUser.getCountry());

            noDetails = false;
        } else {
            countryContainer.setVisibility(View.GONE);
        }

        if(dbUser.getEmail() != null) {
            emailContainer.setVisibility(View.VISIBLE);
            emailText.setText(dbUser.getEmail());

            noDetails = false;
        } else {
            emailContainer.setVisibility(View.GONE);
        }

        noDetailsContainer.setVisibility(noDetails ? View.VISIBLE : View.GONE);
    }


    private void updateTitleDetails(DBUser dbUser) {

        if(dbUser.getPicture() != null && !dbUser.getPicture().isEmpty()) {
            dialogAvatar.setVisibility(View.VISIBLE);
            dialogAvatarIcon.setVisibility(View.GONE);

            Glide.with(this).load(dbUser.getPicture()).into(dialogAvatar);
        } else {
            dialogAvatar.setVisibility(View.GONE);
            dialogAvatarIcon.setVisibility(View.VISIBLE);

            AvatarViewUtil.renderInitialsUserAvatar(this, dialogAvatarIcon, dbUser.getSafeName());
        }

        dialogName.setText(dbUser.getSafeName());
        dialogDate.setText("Last seen at " + DateUtils.format(this, dbUser.getLastActive()));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(USER_ID_DATA, userObjectId);
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
}
