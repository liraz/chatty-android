package org.lirazs.chatty.activity;

import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;

import org.lirazs.chatty.R;

public class BaseActivity extends InjectedActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.logo_main));
            mProgressDialog.setIndeterminate(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
