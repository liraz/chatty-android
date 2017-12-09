package org.lirazs.chatty.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;

import org.lirazs.chatty.R;
import org.lirazs.chatty.activity.auth.FacebookLoginActivity;

/**
 * Created by Liraz on 05/05/2017.
 */

public class SplashScreenActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Thread splashTread;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        startAnimations();
    }

    private void startAnimations() {

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 4000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashScreenActivity.this,
                            FacebookLoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    //SplashScreenActivity.this.finish();
                }

            }
        };
        splashTread.start();

    }
}
