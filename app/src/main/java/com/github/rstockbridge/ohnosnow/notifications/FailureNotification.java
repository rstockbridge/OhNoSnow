package com.github.rstockbridge.ohnosnow.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.R;

public final class FailureNotification {

    private static final int NOTIFICATION_ID = 9174;

    private static NotificationCompat.Builder getNotificationBuilder(@NonNull final Context context) {
        final PendingIntent emptyPendingIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent() ,
                PendingIntent.FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.uh_oh))
                .setContentText(context.getString(R.string.failed_weather_data))
                .setSmallIcon(R.drawable.ic_app_icon)
                .setAutoCancel(true)
                .setContentIntent(emptyPendingIntent);
    }

    public static void sendNotification(@NonNull final Context context) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannelUtil.createNotificationChannel(notificationManager);

        final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
