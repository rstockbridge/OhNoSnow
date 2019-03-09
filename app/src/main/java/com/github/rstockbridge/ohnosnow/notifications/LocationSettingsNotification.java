package com.github.rstockbridge.ohnosnow.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.R;
import com.github.rstockbridge.ohnosnow.activities.LocationSettingsActivity;
import com.github.rstockbridge.ohnosnow.utils.NotificationChannelUtil;

import static com.github.rstockbridge.ohnosnow.activities.LocationSettingsActivity.EXTRA_RESOLVABLE_PENDING_INTENT;

public final class LocationSettingsNotification {

    private static final int NOTIFICATION_ID = 3018;

    private static NotificationCompat.Builder getNotificationBuilder(@NonNull final Context context) {
        return new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.location_off))
                .setContentText(context.getString(R.string.tap_to_continue))
                .setSmallIcon(R.drawable.ic_error)
                .setAutoCancel(true);
    }

    public static void sendNotification(@NonNull final Context context, @NonNull final PendingIntent resolvablePendingIntent) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannelUtil.createNotificationChannel(notificationManager);

        final Intent locationSettingsIntent = new Intent(context, LocationSettingsActivity.class);
        locationSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        locationSettingsIntent.putExtra(EXTRA_RESOLVABLE_PENDING_INTENT, resolvablePendingIntent);

        final PendingIntent locationSettingsPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        locationSettingsIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context);
        notificationBuilder.setContentIntent(locationSettingsPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
