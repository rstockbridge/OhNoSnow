package com.github.rstockbridge.ohnosnow.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.R;

public final class FetchingNotification {

    private static NotificationCompat.Builder getNotificationBuilder(@NonNull final Context context) {
        return new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.fetching))
                .setSmallIcon(R.drawable.ic_app_icon)
                .setAutoCancel(true);
    }

    public static Notification getNotification(@NonNull final Context context) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannelUtil.createNotificationChannel(notificationManager);

        final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context);
        return notificationBuilder.build();
    }
}
