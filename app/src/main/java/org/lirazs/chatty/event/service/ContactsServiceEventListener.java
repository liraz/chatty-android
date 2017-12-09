package org.lirazs.chatty.event.service;

import org.greenrobot.eventbus.Subscribe;

/**
 *
 */
public interface ContactsServiceEventListener {

    @Subscribe
    void onContactsServiceEvent(ContactsServiceEvent event);
}
