package com.github.rstockbridge.ohnosnow.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.github.rstockbridge.ohnosnow.R;
import com.github.rstockbridge.ohnosnow.utils.NotificationChannelUtil;

public final class WeatherNotification {

    private static final int FORECAST_NOTIFICATION_ID = 4813;
    private static final int FAILURE_NOTIFICATION_ID = 9174;

    private static NotificationCompat.Builder getForecastNotificationBuilder(
            @NonNull final Context context,
            @NonNull final String contentString) {

        return new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.weather_update))
                .setContentText(contentString)
                .setSmallIcon(R.drawable.ic_forecast)
                .setAutoCancel(true);
    }

    private static NotificationCompat.Builder getFailureNotificationBuilder(@NonNull final Context context) {
        return new NotificationCompat.Builder(context, NotificationChannelUtil.PRIMARY_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.uh_oh))
                .setContentText(context.getString(R.string.failed_weather_data))
                .setSmallIcon(R.drawable.ic_error)
                .setAutoCancel(true);
    }

    public static void sendForecastNotification(@NonNull final Context context, final double snowInInches) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannelUtil.createNotificationChannel(notificationManager);

        final String contentString = snowInInches > 0 ? snowInInches + "\" of snow overnight." : "No overnight snow.";

        final NotificationCompat.Builder notificationBuilder = getForecastNotificationBuilder(context, contentString);
        notificationManager.notify(FORECAST_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void sendFailureNotification(@NonNull final Context context) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannelUtil.createNotificationChannel(notificationManager);

        final NotificationCompat.Builder notificationBuilder = getFailureNotificationBuilder(context);
        notificationManager.notify(FAILURE_NOTIFICATION_ID, notificationBuilder.build());
    }
}
