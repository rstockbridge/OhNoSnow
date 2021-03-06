package com.github.rstockbridge.ohnosnow.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.R;

public final class WeatherNotification {

    private static final int NOTIFICATION_ID = 4813;

    private static NotificationCompat.Builder getNotificationBuilder(
            @NonNull final Context context,
            @NonNull final String contentString) {

        final PendingIntent emptyPendingIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent() ,
                PendingIntent.FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.weather_update))
                .setContentText(contentString)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setAutoCancel(true)
                .setContentIntent(emptyPendingIntent);
    }

    public static void sendNotification(@NonNull final Context context, final double snowInInches) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannelUtil.createNotificationChannel(notificationManager);

        final String contentString = snowInInches > 0 ? snowInInches + "\" of snow overnight." : "No overnight snow.";

        final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context, contentString);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
