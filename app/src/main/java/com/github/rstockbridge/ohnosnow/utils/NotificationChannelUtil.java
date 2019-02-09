package com.github.rstockbridge.ohnosnow.utils;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;

public class NotificationChannelUtil {
    public static final String PRIMARY_CHANNEL_ID = "primaryNotificationChannel";

    private static NotificationChannel notificationChannel = null;

    @SuppressLint("NewApi")
    public static void createNotificationChannel(@NonNull final NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && NotificationChannelUtil.notificationChannel == null) {
            notificationChannel = new NotificationChannel(
                    NotificationChannelUtil.PRIMARY_CHANNEL_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
