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

package org.lirazs.chatty.app;

import android.app.Application;

import org.lirazs.chatty.manager.RealmManager;
import org.lirazs.chatty.service.AuthService;
import org.lirazs.chatty.service.ChatService;
import org.lirazs.chatty.service.ContactsService;
import org.lirazs.chatty.service.GroupsService;
import org.lirazs.chatty.service.MessagesService;
import org.lirazs.chatty.service.PushNotificationService;
import org.lirazs.chatty.service.RecentService;
import org.lirazs.chatty.service.UserStatusesService;
import org.lirazs.chatty.service.UsersService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mac on 2/17/17.
 */

@Module
public class ConfigModule {

    private RealmManager realmManager;

    private AuthService authService;
    private GroupsService groupsService;
    private MessagesService messagesService;
    private RecentService recentService;
    private UsersService usersService;
    private UserStatusesService userStatusesService;
    private ChatService chatService;
    private PushNotificationService pushNotificationService;
    private ContactsService contactsService;

    public ConfigModule(Application context) {

        realmManager = new RealmManager();

        authService = new AuthService(context, realmManager);
        usersService = new UsersService(context, authService);
        recentService = new RecentService(context, authService, usersService);
        pushNotificationService = new PushNotificationService(context, authService, recentService);
        messagesService = new MessagesService(context, authService, recentService, pushNotificationService);
        userStatusesService = new UserStatusesService(context, authService);
        chatService = new ChatService(context, authService, recentService);
        groupsService = new GroupsService(context, authService, chatService, recentService);
        contactsService = new ContactsService(context, authService);
    }

    @Provides
    @Singleton
    AuthService provideAuthService() {
        return authService;
    }

    @Provides
    @Singleton
    GroupsService provideGroupsService() {
        return groupsService;
    }

    @Provides
    @Singleton
    MessagesService provideMessagesService() {
        return messagesService;
    }

    @Provides
    @Singleton
    RecentService provideRecentsService() {
        return recentService;
    }

    @Provides
    @Singleton
    UsersService provideUsersService() {
        return usersService;
    }

    @Provides
    @Singleton
    UserStatusesService provideUserStatusesService() {
        return userStatusesService;
    }

    @Provides
    @Singleton
    ChatService provideChatService() {
        return chatService;
    }

    @Provides
    @Singleton
    PushNotificationService providePushNotificationService() {
        return pushNotificationService;
    }

    @Provides
    @Singleton
    ContactsService provideContactsService() {
        return contactsService;
    }
}
