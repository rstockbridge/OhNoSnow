package com.github.rstockbridge.ohnosnow.utils;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import pub.devrel.easypermissions.EasyPermissions;

public class EasyPermissionsHelper {

    private EasyPermissionsHelper() {
    }

    public static boolean allTheTimeLocationAccessGranted(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        } else {
            return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}
