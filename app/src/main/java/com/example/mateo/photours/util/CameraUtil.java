package com.example.mateo.photours.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.example.mateo.photours.Global;

import java.io.File;

public class CameraUtil {

    public static void startCamera(Activity activity) {
        if (PermissionUtils.requestPermission(
                activity,
                Global.CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", getCameraFile(activity));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivityForResult(intent, Global.CAMERA_IMAGE_REQUEST);
        }
    }

    public static File getCameraFile(Activity activity) {
        File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, Global.FILE_NAME);
    }
}
