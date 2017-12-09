package org.lirazs.chatty.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import org.lirazs.chatty.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * Created by Liraz on 23/09/2017.
 */

public class ImageViewUtil {

    public static void openImageChooser(final Activity activity, final int requestCode) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_picture_title)), requestCode);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_picture_title)), requestCode);
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };

        // Try to get "READ_EXTERNAL_STORAGE" permission from user
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }
}
