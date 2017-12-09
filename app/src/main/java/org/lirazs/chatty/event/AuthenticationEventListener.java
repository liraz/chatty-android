package org.lirazs.chatty.event;

import org.greenrobot.eventbus.Subscribe;

/**
 *
 */
public interface AuthenticationEventListener {

    @Subscribe
    void onAuthenticationEvent(AuthenticationEvent authenticationEvent);
}
