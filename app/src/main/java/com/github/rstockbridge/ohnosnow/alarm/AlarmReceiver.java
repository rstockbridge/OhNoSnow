package com.github.rstockbridge.ohnosnow.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.rstockbridge.ohnosnow.BuildConfig;
import com.github.rstockbridge.ohnosnow.api.DarkSkyApi;
import com.github.rstockbridge.ohnosnow.api.MyCallback;
import com.github.rstockbridge.ohnosnow.api.models.ForecastData;
import com.github.rstockbridge.ohnosnow.notifications.WeatherNotification;
import com.github.rstockbridge.ohnosnow.utils.LocationUtil;

import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.NotificationPref;
import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.getNotificationPref;

public class AlarmReceiver
        extends BroadcastReceiver
        implements LocationUtil.LocationSuccessListener {

    public static Intent getAlarmReceiverIntent(@NonNull final Context context) {
        return new Intent(context, AlarmReceiver.class);
    }

    private MyCallback<ForecastData> myCallback;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final LocationUtil locationUtil = new LocationUtil(context, this);

        myCallback = new MyCallback<ForecastData>() {
            @Override
            public void onSuccess(@NonNull final ForecastData result) {
                final double snowInInches = result.get12HourSnowInInches();

                final NotificationPref notificationPref = getNotificationPref(context);
                if ((notificationPref == NotificationPref.SNOW_AND_FAILURE_ONLY && snowInInches > 0)
                        || notificationPref == NotificationPref.ALL) {
                    WeatherNotification.sendForecastNotification(context, snowInInches);
                }
            }

            @Override
            public void onError() {
                WeatherNotification.sendFailureNotification(context);
            }

            @Override
            public void onFailure() {
                WeatherNotification.sendFailureNotification(context);
            }
        };

        locationUtil.requestLocation(context, this);
    }

    @Override
    public void onLocationSuccess(@NonNull final String latitude, @NonNull final String longitude) {
        DarkSkyApi.getSharedInstance().fetchForecast(BuildConfig.DARK_SKY_KEY, latitude, longitude, myCallback);
    }
}
