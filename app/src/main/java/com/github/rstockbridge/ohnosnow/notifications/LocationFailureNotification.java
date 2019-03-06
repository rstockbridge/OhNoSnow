package com.github.rstockbridge.ohnosnow.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.R;
import com.github.rstockbridge.ohnosnow.utils.NotificationChannelUtil;

public final class LocationFailureNotification {

    private static final int NOTIFICATION_ID = 3913;

    private static NotificationCompat.Builder getNotificationBuilder(@NonNull final Context context, boolean locationIsOff) {
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.location_failed))
                        .setSmallIcon(R.drawable.ic_error)
                        .setAutoCancel(true);

        if (locationIsOff) {
            builder.setContentText(context.getString(R.string.location_off));
        }

        return builder;
    }

    public static void sendNotification(@NonNull final Context context, boolean locationIsOff) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannelUtil.createNotificationChannel(notificationManager);

        final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context, locationIsOff);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
