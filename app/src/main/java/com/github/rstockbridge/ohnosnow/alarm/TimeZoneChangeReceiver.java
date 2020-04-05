package com.github.rstockbridge.ohnosnow.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeZoneChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // Edge case:
        //   Assume it is 6:30pm in the current time zone (alarm pending for 7pm)
        //   And we cross a time zone boundary so that it is now 7:30pm
        //   When the setNextAlarm method is called
        //   The existing pending alarm will be canceled
        //   And the next alarm will be scheduled for 23.5 hours in the future
        //   And no alarm will fire for the current calendar day.
        AlarmHelper.setNextAlarm(context);
    }

}
