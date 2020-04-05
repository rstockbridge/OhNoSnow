package com.github.rstockbridge.ohnosnow.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmHelper {

    private static final int PENDING_INTENT_REQUEST_CODE = 7293;
    private static final int NOTIFICATION_HOUR = 19;

    private static long computeNextAlarmTimeMillis() {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return calendar.getTimeInMillis();
    }

    public static void setNextAlarm(@NonNull final Context context) {
        final long nextAlarmTimeMillis = computeNextAlarmTimeMillis();

        final PendingIntent pendingIntent = getPendingIntent(context);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        alarmManager.set(
                AlarmManager.RTC,
                nextAlarmTimeMillis,
                pendingIntent
        );
    }

    public static void cancelAlarm(@NonNull final Context context) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        final PendingIntent pendingIntent = getPendingIntent(context);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    private static PendingIntent getPendingIntent(@NonNull final Context context) {
        final Intent alarmIntentService = WeatherCheckService.getAlarmIntentService(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent.getForegroundService(context, PENDING_INTENT_REQUEST_CODE, alarmIntentService, 0);
        } else {
            return PendingIntent.getService(context, PENDING_INTENT_REQUEST_CODE, alarmIntentService, 0);
        }
    }
}
