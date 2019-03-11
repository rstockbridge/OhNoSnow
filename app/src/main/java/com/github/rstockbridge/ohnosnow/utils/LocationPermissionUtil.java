package com.github.rstockbridge.ohnosnow.utils;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;

import pub.devrel.easypermissions.EasyPermissions;

public final class LocationPermissionUtil {

    public static boolean locationPermissionGranted(@NonNull final Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
