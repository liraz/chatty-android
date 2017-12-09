package org.lirazs.chatty.event;

import org.greenrobot.eventbus.Subscribe;

/**
 *
 */
public interface ApplicationEventListener {

    @Subscribe
    void onApplicationEvent(ApplicationEvent applicationEvent);
}
