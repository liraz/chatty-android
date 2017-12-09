package org.lirazs.chatty.manager;

import io.realm.Realm;

/**
 * Created by mac on 4/20/17.
 */

public class RealmManager {


    public void cleanupDatabase() {
        Realm realm = Realm.getDefaultInstance();

        try {
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
        } finally {
            realm.close();
        }
    }
}
