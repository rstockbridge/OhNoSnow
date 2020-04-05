package com.github.rstockbridge.ohnosnow.alarm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pub.devrel.easypermissions.EasyPermissions;

import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.NotificationPref;
import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.getNotificationPref;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    && getNotificationPref(context) != NotificationPref.NONE) {
                new AlarmHelper(context).setNextAlarm();
            }
        }
    }
}
