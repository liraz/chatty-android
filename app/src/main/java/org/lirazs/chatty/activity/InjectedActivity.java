package org.lirazs.chatty.activity;

import android.support.v7.app.AppCompatActivity;

import org.lirazs.chatty.app.ChattyApplication;

import butterknife.ButterKnife;

/**
 * Created by mac on 2/17/17.
 */

public class InjectedActivity extends AppCompatActivity {

    protected ChattyApplication.ApplicationComponent component() {
        return ((ChattyApplication) getApplication()).component();
    }

    protected void injectViews() {
        ButterKnife.bind(this);
    }
}
