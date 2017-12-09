package org.lirazs.chatty.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.lirazs.chatty.R;
import org.lirazs.chatty.activity.auth.FacebookLoginActivity;
import org.lirazs.chatty.event.AuthenticationEvent;
import org.lirazs.chatty.event.AuthenticationEventListener;
import org.lirazs.chatty.model.chatkit.DBRecentDialogDecorator;
import org.lirazs.chatty.model.chatkit.DBUserDialogDecorator;
import org.lirazs.chatty.model.firebase.FirebaseUserObject;
import org.lirazs.chatty.model.realm.DBRecent;
import org.lirazs.chatty.model.realm.DBUser;
import org.lirazs.chatty.model.request.ChatRequest;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.service.ChatService;
import org.lirazs.chatty.service.ContactsService;
import org.lirazs.chatty.service.RecentService;
import org.lirazs.chatty.service.UsersService;
import org.lirazs.chatty.view.CustomDialogViewHolder;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Liraz on 26/05/2017.
 */

public class RecentChatsActivity extends BaseActivity implements DialogsListAdapter.OnDialogClickListener<DBRecentDialogDecorator>,
        DialogsListAdapter.OnDialogLongClickListener<DBRecentDialogDecorator>, DateFormatter.Formatter, AuthenticationEventListener {

    @Inject
    public RecentService recentService;

    @Inject
    public ChatService chatService;

    @Inject
    public AuthService authService;

    @Inject
    public ContactsService contactsService;

    @Inject
    public UsersService usersService;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.recent_dialogs_list)
    public DialogsList recentDialogsList;

    @BindView(R.id.user_dialogs_list)
    public DialogsList userDialogsList;

    @BindView(R.id.new_message)
    public FloatingActionButton newMessage;

    private Drawer drawer;

    private Realm realm;

    private ImageLoader imageLoader;

    private DialogsListAdapter<DBRecentDialogDecorator> recentDialogsAdapter;
    private RealmResults<DBRecent> recentsRealmResults;
    private RealmChangeListener<RealmResults<DBRecent>> recentsRealmResultsChangeListener;

    private DialogsListAdapter<DBUserDialogDecorator> userDialogsAdapter;
    private RealmResults<DBUser> usersFromContactsResults;
    private RealmChangeListener<RealmResults<DBUser>> usersRealmResultsChangeListener;

    @Subscribe
    public void onAuthenticationEvent(AuthenticationEvent authenticationEvent) {

        if(authenticationEvent.getEventType() == AuthenticationEvent.Type.USER_LOGGED_OUT_SUCCESS) {
            startActivity(new Intent(this, FacebookLoginActivity.class));
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_recent_chats);
        injectViews();

        setupNavigationDrawer(savedInstanceState);

        //TODO: Listen to events
		//[NotificationCenter addObserver:self selector:@selector(refreshTableView) name:NOTIFICATION_REFRESH_RECENTS];
		//[NotificationCenter addObserver:self selector:@selector(refreshTabCounter) name:NOTIFICATION_REFRESH_RECENTS];

        //TODO: Update the application badge counter (not very possible in android)
        /*NSInteger total = 0;
        //---------------------------------------------------------------------------------------------------------------------------------------------
        for (DBRecent *dbrecent in dbrecents)
        total += dbrecent.counter;
        //---------------------------------------------------------------------------------------------------------------------------------------------
        UIUserNotificationSettings *currentUserNotificationSettings = [[UIApplication sharedApplication] currentUserNotificationSettings];
        if (currentUserNotificationSettings.types & UIUserNotificationTypeBadge)
		[UIApplication sharedApplication].applicationIconBadgeNumber = total;*/




        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if (imageView != null && url != null && !url.isEmpty()) {
                    Glide.with(RecentChatsActivity.this).load(url).into(imageView);
                }
            }
        };

        recentDialogsAdapter = new DialogsListAdapter<>(R.layout.item_custom_dialog, CustomDialogViewHolder.class, imageLoader);
        recentDialogsAdapter.setOnDialogClickListener(this);
        recentDialogsAdapter.setOnDialogLongClickListener(this);
        recentDialogsAdapter.setDatesFormatter(this);

        recentDialogsList.setAdapter(recentDialogsAdapter);


        userDialogsAdapter = new DialogsListAdapter<>(R.layout.item_custom_dialog, CustomDialogViewHolder.class, imageLoader);
        userDialogsAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<DBUserDialogDecorator>() {
            @Override
            public void onDialogClick(DBUserDialogDecorator dialog) {
                ChatRequest chatRequest = chatService.generatePrivateChatRequest(dialog.getUser());

                Intent intent = new Intent(RecentChatsActivity.this, ChatActivity.class);
                intent.putExtra(ChatRequest.class.getCanonicalName(), chatRequest);
                startActivity(intent);
            }
        });

        userDialogsList.setAdapter(userDialogsAdapter);


        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the new message screen
                Intent intent = new Intent(RecentChatsActivity.this,
                        NewGroupSelectActivity.class);
                startActivity(intent);
            }
        });

        Drawable icon = new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                .sizeDp(18);
        newMessage.setImageDrawable(icon);
    }

    private void setupNavigationDrawer(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);

        FirebaseUserObject currentUser = authService.getCurrentUser();

        final IProfile profile = new ProfileDrawerItem()
                .withName(currentUser.getSafeName())
                .withEmail(currentUser.getEmail());

        if(currentUser.getPicture() != null) {
            profile.withIcon(currentUser.getPicture());
        }

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .addProfiles(profile)
                .withSavedInstance(savedInstanceState)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withSavedInstance(savedInstanceState)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withDrawerLayout(R.layout.material_drawer_fits_not)
                .withAccountHeader(accountHeader, true)
                .addDrawerItems(

                        new PrimaryDrawerItem().withName("Create new group").withIcon(GoogleMaterial.Icon.gmd_add)
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    // Open the new message screen
                                    Intent intent = new Intent(RecentChatsActivity.this,
                                            NewGroupSelectActivity.class);
                                    startActivity(intent);
                                    return false;
                                }
                            }),
                        new PrimaryDrawerItem().withName("Log out").withIcon(GoogleMaterial.Icon.gmd_settings_power)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        authService.logOut();
                                        return false;
                                    }
                                })
                )
                .build();
    }


    @Override
    public void onDialogLongClick(DBRecentDialogDecorator dialog) {
        Toast.makeText(this, dialog.getDialogName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogClick(DBRecentDialogDecorator dialog) {
        openRecentChat(dialog.getRecent());
    }

    private void openRecentChat(DBRecent recent) {
        ChatRequest chatRequest = chatService.generateRestartRecentChatRequest(recent);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatRequest.class.getCanonicalName(), chatRequest);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        realm = Realm.getDefaultInstance();

        // dealing with recent items
        recentsRealmResults = realm.where(DBRecent.class).equalTo(DBRecent.RECENT_IS_ARCHIVED, false)
                .equalTo(DBRecent.RECENT_IS_DELETED, false)
                .findAllSorted(DBRecent.RECENT_LAST_MESSAGE_DATE, Sort.DESCENDING);

        recentsRealmResultsChangeListener = new RealmChangeListener<RealmResults<DBRecent>>() {
            @Override
            public void onChange(RealmResults<DBRecent> dbRecents) {
                recentDialogsAdapter.setItems(DBRecentDialogDecorator.decorateRecentList(dbRecents));
            }
        };
        recentsRealmResults.addChangeListener(recentsRealmResultsChangeListener);
        recentDialogsAdapter.setItems(DBRecentDialogDecorator.decorateRecentList(recentsRealmResults));

        final FirebaseUserObject currentUser = authService.getCurrentUser();

        // dealing with user items
        usersFromContactsResults = contactsService.findUsersFromContacts();

        usersRealmResultsChangeListener = new RealmChangeListener<RealmResults<DBUser>>() {
            @Override
            public void onChange(RealmResults<DBUser> dbUsers) {
                List<String> userNames = recentService.getRecipientNamesFromPrivateChats(recentsRealmResults);
                userNames.add(currentUser.getSafeName());

                List<DBUser> users = usersService.filterExcludedUserNames(dbUsers, userNames);
                userDialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(users));
            }
        };
        usersFromContactsResults.addChangeListener(usersRealmResultsChangeListener);


        List<String> userNames = recentService.getRecipientNamesFromPrivateChats(recentsRealmResults);
        userNames.add(currentUser.getSafeName());

        List<DBUser> dbUsers = usersService.filterExcludedUserNames(usersFromContactsResults, userNames);
        userDialogsAdapter.setItems(DBUserDialogDecorator.decorateUserList(dbUsers));
    }

    @Override
    public void onStop() {
        super.onStop();

        recentsRealmResults.removeChangeListener(recentsRealmResultsChangeListener);
        usersFromContactsResults.removeChangeListener(usersRealmResultsChangeListener);
        realm.close();

        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return DateFormatter.format(date, DateFormatter.Template.TIME);
        } else if (DateFormatter.isYesterday(date)) {
            return "Yesterday";
        } else if (DateFormatter.isCurrentYear(date)) {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }
}
