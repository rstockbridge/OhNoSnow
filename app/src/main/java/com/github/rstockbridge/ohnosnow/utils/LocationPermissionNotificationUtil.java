package com.github.rstockbridge.ohnosnow.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.LocationPermissionActivity;
import com.github.rstockbridge.ohnosnow.R;

public class LocationPermissionNotificationUtil {

    private static final int NOTIFICATION_ID = 6192;

    private static NotificationCompat.Builder getNotificationBuilder(@NonNull final Context context) {
        return new NotificationCompat.Builder(context, NotificationUtil.PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.location_permission_not_granted))
                .setContentText(context.getString(R.string.tap_to_continue))
                .setSmallIcon(R.drawable.ic_error)
                .setAutoCancel(true);
    }

    public static void sendNotification(@NonNull final Context context) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationUtil.createNotificationChannel(notificationManager);

        final Intent locationPermissionIntent = new Intent(context, LocationPermissionActivity.class);
        locationPermissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent locationPermissionPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        locationPermissionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context);
        notificationBuilder.setContentIntent(locationPermissionPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
