package com.github.rstockbridge.ohnosnow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public class SharedPreferenceHelper {

    public enum NotificationPref {
        NONE("Off"),
        SNOW_AND_FAILURE_ONLY("Snow only"),
        ALL("Always");

        @NonNull
        private final String displayString;

        NotificationPref(@NonNull final String displayString) {
            this.displayString = displayString;
        }

        @NonNull
        @Override
        public String toString() {
            return displayString;
        }
    }

    private static final String PREF_NOTIFICATION_PREF = "notificationPref";
    private static final NotificationPref DEFAULT_NOTIFICATION_PREF = NotificationPref.NONE;

    public static void setNotificationPref(
            @NonNull final Context context,
            @NonNull final NotificationPref selectedNotificationPref) {

        getSharedPreferences(context)
                .edit()
                .putInt(PREF_NOTIFICATION_PREF, selectedNotificationPref.ordinal())
                .apply();
    }

    public static NotificationPref getNotificationPref(@NonNull final Context context) {
        return NotificationPref.values()
                [getSharedPreferences(context).getInt(PREF_NOTIFICATION_PREF, DEFAULT_NOTIFICATION_PREF.ordinal())];
    }

    private static SharedPreferences getSharedPreferences(@NonNull final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
