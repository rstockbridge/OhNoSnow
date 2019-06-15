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

    private static Calendar getAlarmCalendar() {
        final Calendar alarmCalendar = Calendar.getInstance();

        alarmCalendar.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR);
        alarmCalendar.set(Calendar.MINUTE, 0);
        alarmCalendar.set(Calendar.SECOND, 0);
        alarmCalendar.set(Calendar.MILLISECOND, 0);

        if(alarmCalendar.getTimeInMillis() < System.currentTimeMillis()) {
            alarmCalendar.add(Calendar.HOUR, 24);
        }

        return alarmCalendar;
    }

    public static void setAlarm(@NonNull final Context context) {
        final Calendar alarmCalendar = getAlarmCalendar();

        if (!alarmExists(context)) {
            final PendingIntent pendingIntent = getPendingIntent(context);

            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(
                    AlarmManager.RTC,
                    alarmCalendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    public static void cancelAlarm(@NonNull final Context context) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        final PendingIntent pendingIntent = getPendingIntent(context);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    private static boolean alarmExists(@NonNull final Context context) {
        final Intent alarmIntentService = WeatherCheckService.getAlarmIntentService(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent
                    .getForegroundService(
                            context,
                            PENDING_INTENT_REQUEST_CODE,
                            alarmIntentService,
                            PendingIntent.FLAG_NO_CREATE)
                    != null;
        } else {
            return PendingIntent
                    .getService(
                            context,
                            PENDING_INTENT_REQUEST_CODE,
                            alarmIntentService,
                            PendingIntent.FLAG_NO_CREATE)
                    != null;
        }
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
