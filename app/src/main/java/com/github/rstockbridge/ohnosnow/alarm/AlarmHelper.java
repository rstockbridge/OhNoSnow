package com.github.rstockbridge.ohnosnow.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.github.rstockbridge.ohnosnow.utils.Clock;
import com.github.rstockbridge.ohnosnow.utils.SystemClock;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AlarmHelper {

    private static final int PENDING_INTENT_REQUEST_CODE = 7293;
    private static final int NOTIFICATION_HOUR = 19;

    @NonNull
    private final AlarmManager alarmManager;

    @NonNull
    private final Clock clock;

    @NonNull
    private final Context context;

    public AlarmHelper(@NonNull final Context context) {
        this(
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE),
                new SystemClock(),
                context
        );
    }

    @VisibleForTesting
    AlarmHelper(
            @NonNull final AlarmManager alarmManager,
            @NonNull final Clock clock,
            @NonNull final Context context
    ) {
        this.alarmManager = alarmManager;
        this.clock = clock;
        this.context = context;
    }

    public void setNextAlarm() {
        setNextAlarm(TimeZone.getDefault());
    }

    @VisibleForTesting
    void setNextAlarm(@NonNull final TimeZone timeZone) {
        final long nextAlarmTimeMillis = computeNextAlarmTimeMillis(timeZone);

        final PendingIntent pendingIntent = getPendingIntent();

        alarmManager.set(
                AlarmManager.RTC,
                nextAlarmTimeMillis,
                pendingIntent
        );
    }

    public void cancelAlarm() {
        final PendingIntent pendingIntent = getPendingIntent();
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    private long computeNextAlarmTimeMillis(@NonNull final TimeZone timeZone) {
        final Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(new Date(clock.getCurrentMillis()));

        calendar.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < clock.getCurrentMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return calendar.getTimeInMillis();
    }

    private PendingIntent getPendingIntent() {
        final Intent alarmIntentService = WeatherCheckService.getAlarmIntentService(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent.getForegroundService(context, PENDING_INTENT_REQUEST_CODE, alarmIntentService, 0);
        } else {
            return PendingIntent.getService(context, PENDING_INTENT_REQUEST_CODE, alarmIntentService, 0);
        }
    }
}
