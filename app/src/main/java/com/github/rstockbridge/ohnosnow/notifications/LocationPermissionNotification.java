package com.github.rstockbridge.ohnosnow.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.R;
import com.github.rstockbridge.ohnosnow.activities.LocationPermissionActivity;

public class LocationPermissionNotification {

    private static final int NOTIFICATION_ID = 6192;

    private static NotificationCompat.Builder getNotificationBuilder(@NonNull final Context context) {
        String message = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            message = context.getString(R.string.all_the_time_location_permission_not_granted);
        } else {
            message = context.getString(R.string.foreground_location_permission_not_granted);
        }

        return new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle(context.getString(R.string.failed_weather_data))
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setAutoCancel(true);
    }

    public static void sendNotification(@NonNull final Context context) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannelUtil.createNotificationChannel(notificationManager);

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


