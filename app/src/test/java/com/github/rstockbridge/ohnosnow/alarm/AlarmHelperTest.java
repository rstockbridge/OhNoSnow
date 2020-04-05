package com.github.rstockbridge.ohnosnow.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.TimeZone;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AlarmHelperTest {

    @Mock
    public AlarmManager alarmManager;

    @Mock
    public Context context;

    @Test
    public void testCorrectAlarmTimeSetBefore7PM() {
        // Arrange
        long currentTimeMillis = 1586007812000L; // Saturday, April 4, 2020 9:43:32 AM GMT-04:00
        TimeZone timeZone = TimeZone.getTimeZone("America/Detroit");

        AlarmHelper alarmHelper = new AlarmHelper(
                alarmManager,
                new MockClock(currentTimeMillis),
                context
        );

        // Act
        alarmHelper.setNextAlarm(timeZone);

        // Assert
        long expectedTime = 1586041200000L; // Saturday, April 4, 2020 7:00:00 PM GMT-04:00
        verify(alarmManager).set(anyInt(), eq(expectedTime), nullable(PendingIntent.class));
    }

    @Test
    public void testCorrectAlarmTimeSetAfter7PM() {
        // Arrange
        long currentTimeMillis = 1586007812000L + HOURS.toMillis(10); // Saturday, April 4, 2020 7:43:32 PM GMT-04:00
        TimeZone timeZone = TimeZone.getTimeZone("America/Detroit");

        AlarmHelper alarmHelper = new AlarmHelper(
                alarmManager,
                new MockClock(currentTimeMillis),
                context
        );

        // Act
        alarmHelper.setNextAlarm(timeZone);

        // Assert
        long expectedTime = 1586127600000L; // Sunday, April 5, 2020 7:00:00 PM GMT-04:00
        verify(alarmManager).set(anyInt(), eq(expectedTime), nullable(PendingIntent.class));
    }

    @Test
    public void testCorrectAlarmTimeWhenDSTChanges() {
        // Arrange
        long currentTimeMillis = 1583625601000L; // Saturday, March 7, 2020 7:00:01 PM GMT-05:00
        TimeZone timeZone = TimeZone.getTimeZone("America/Detroit");

        AlarmHelper alarmHelper = new AlarmHelper(
                alarmManager,
                new MockClock(currentTimeMillis),
                context
        );

        // Act
        alarmHelper.setNextAlarm(timeZone);

        // Assert
        long expectedTime = 1583708400000L; // Sunday, March 8, 2020 7:00:00 PM GMT-04:00
        verify(alarmManager).set(anyInt(), eq(expectedTime), nullable(PendingIntent.class));
    }

}
