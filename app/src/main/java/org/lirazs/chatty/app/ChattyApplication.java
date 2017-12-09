package org.lirazs.chatty.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;

import org.greenrobot.eventbus.EventBus;
import org.lirazs.chatty.R;
import org.lirazs.chatty.activity.ChatActivity;
import org.lirazs.chatty.activity.GroupProfileActivity;
import org.lirazs.chatty.activity.NewGroupDetailsActivity;
import org.lirazs.chatty.activity.NewGroupSelectActivity;
import org.lirazs.chatty.activity.RecentChatsActivity;
import org.lirazs.chatty.activity.SelectContactActivity;
import org.lirazs.chatty.activity.UserProfileActivity;
import org.lirazs.chatty.activity.auth.EmailAndPhoneLoginActivity;
import org.lirazs.chatty.activity.auth.EmailAndPhoneSignupActivity;
import org.lirazs.chatty.activity.auth.EmailAndPhoneSignupPictureActivity;
import org.lirazs.chatty.activity.auth.EmailAndPhoneVerificationActivity;
import org.lirazs.chatty.activity.auth.FacebookLoginActivity;
import org.lirazs.chatty.event.ApplicationEvent;
import org.lirazs.chatty.manager.ChattyEmojiManager;
import org.lirazs.chatty.service.GroupsService;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mac on 2/17/17.
 */

public class ChattyApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    @Singleton
    @Component(modules = ConfigModule.class)
    public interface ApplicationComponent {
        void inject(GroupsService groupsService);

        void inject(FacebookLoginActivity activity);
        void inject(EmailAndPhoneLoginActivity activity);
        void inject(RecentChatsActivity activity);
        void inject(NewGroupSelectActivity activity);
        void inject(NewGroupDetailsActivity activity);
        void inject(ChatActivity activity);
        void inject(EmailAndPhoneSignupActivity activity);
        void inject(UserProfileActivity activity);
        void inject(GroupProfileActivity activity);
        void inject(SelectContactActivity activity);
        void inject(EmailAndPhoneSignupPictureActivity activity);
        void inject(EmailAndPhoneVerificationActivity activity);

    }

    private ApplicationComponent component;

    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        Realm.init(this);
        Realm.setDefaultConfiguration(getRealmConfig());

        EmojiManager.install(new EmojiOneProvider());
        ChattyEmojiManager.install(new EmojiOneProvider());

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });

        component = DaggerChattyApplication_ApplicationComponent.builder()
                .configModule(new ConfigModule(this))
                .build();

        // dispatching the application started event
        EventBus.getDefault().post(new ApplicationEvent(
                ApplicationEvent.ApplicationEventType.APPLICATION_STARTED));
    }

    private RealmConfiguration getRealmConfig() {
        return new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    public ApplicationComponent component() {
        return component;
    }

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mCurrentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
