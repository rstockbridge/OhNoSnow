package com.github.rstockbridge.ohnosnow.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.R;
import com.github.rstockbridge.ohnosnow.activities.LocationPermissionActivity;

public class LocationPermissionNotification {

    private static final int NOTIFICATION_ID = 6192;

    private static NotificationCompat.Builder getNotificationBuilder(@NonNull final Context context) {
        return new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.location_permission_not_granted)))
                .setContentTitle(context.getString(R.string.failed_weather_data))
                .setContentText(context.getString(R.string.location_permission_not_granted))
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


