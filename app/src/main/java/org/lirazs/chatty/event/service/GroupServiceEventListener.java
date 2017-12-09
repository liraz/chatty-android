package org.lirazs.chatty.event.service;

import org.greenrobot.eventbus.Subscribe;

/**
 *
 */
public interface GroupServiceEventListener {

    @Subscribe
    void onGroupServiceEvent(GroupServiceEvent authenticationEvent);
}
