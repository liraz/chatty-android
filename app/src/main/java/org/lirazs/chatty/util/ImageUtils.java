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
package org.lirazs.chatty.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;



public class ImageUtils {

    public static Drawable scaleImage (Context context, Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(context.getResources(), bitmapResized);

        return image;

    }

    public static void uploadImage(Context context, Uri imageUri) {
        uploadImage(context, imageUri, null, null, null);
    }

    public static void uploadImage(Context context, Uri imageUri,
                                   final OnSuccessListener<UploadTask.TaskSnapshot> successListener) {
        uploadImage(context, imageUri, successListener, null, null);
    }

    public static void uploadImage(Context context, Uri imageUri,
                                   final OnSuccessListener<UploadTask.TaskSnapshot> successListener,
                                   OnFailureListener failureListener) {
        uploadImage(context, imageUri, successListener, failureListener, null);
    }

    public static void uploadImage(Context context, Uri imageUri,
                                   final OnSuccessListener<UploadTask.TaskSnapshot> successListener,
                                   OnFailureListener failureListener,
                                   OnProgressListener<UploadTask.TaskSnapshot> progressListener) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(FileUtils.FIREBASE_STORAGE)
                .child(FileUtils.generateFilename("image", "jpg"));

        try {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                context.getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }*/

            Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(data);
            if (failureListener != null) {
                uploadTask.addOnFailureListener(failureListener);
            }
            if (successListener != null) {
                uploadTask.addOnSuccessListener(successListener);
            }
            if (progressListener != null) {
                uploadTask.addOnProgressListener(progressListener);
            }

        } catch (IOException e) {
            failureListener.onFailure(e);
        }
    }
}
