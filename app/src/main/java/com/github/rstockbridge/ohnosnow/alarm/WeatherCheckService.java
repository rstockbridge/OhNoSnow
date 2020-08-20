package com.github.rstockbridge.ohnosnow.alarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.rstockbridge.ohnosnow.BuildConfig;
import com.github.rstockbridge.ohnosnow.api.ClimaCellApi;
import com.github.rstockbridge.ohnosnow.api.MyCallback;
import com.github.rstockbridge.ohnosnow.api.models.Forecast;
import com.github.rstockbridge.ohnosnow.location.LocationSettingsState;
import com.github.rstockbridge.ohnosnow.location.LocationUtil;
import com.github.rstockbridge.ohnosnow.location.MyLocationResult;
import com.github.rstockbridge.ohnosnow.notifications.FailureNotification;
import com.github.rstockbridge.ohnosnow.notifications.FetchingNotification;
import com.github.rstockbridge.ohnosnow.notifications.LocationPermissionNotification;
import com.github.rstockbridge.ohnosnow.notifications.LocationSettingsNotification;
import com.github.rstockbridge.ohnosnow.notifications.WeatherNotification;
import com.github.rstockbridge.ohnosnow.utils.EasyPermissionsHelper;
import com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.getNotificationPref;

public class WeatherCheckService extends Service {

    // do not respond to alarm if user does not want any notifications
    // still report errors if user requests snow-only notifications

    public static Intent getAlarmIntentService(@NonNull final Context context) {
        return new Intent(context, WeatherCheckService.class);
    }

    private SharedPreferenceHelper.NotificationPref notificationPref;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private LocationUtil locationUtil;

    private MyCallback<List<Forecast>> myCallback = new MyCallback<List<Forecast>>() {
        @Override
        public void onSuccess(@NonNull final List<Forecast> result) {
            final double snowInInches = get12HourSnowInInches(result);

            if ((notificationPref == SharedPreferenceHelper.NotificationPref.SNOW_AND_FAILURE_ONLY && snowInInches > 0.75)
                    || notificationPref == SharedPreferenceHelper.NotificationPref.ALL) {
                WeatherNotification.sendNotification(WeatherCheckService.this, snowInInches);
            }

            stopSelf();
        }

        @Override
        public void onError() {
            FailureNotification.sendNotification(WeatherCheckService.this);
            stopSelf();
        }

        @Override
        public void onFailure(@NonNull final Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            FailureNotification.sendNotification(WeatherCheckService.this);
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        startForeground(1, FetchingNotification.getNotification(this));

        notificationPref = getNotificationPref(WeatherCheckService.this);

        if (notificationPref != SharedPreferenceHelper.NotificationPref.NONE) {
            new AlarmHelper(this).setNextAlarm();

            if (EasyPermissionsHelper.allTheTimeLocationAccessGranted(this)) {
                locationUtil = new LocationUtil(this);

                startLocationFlow();
            } else {
                LocationPermissionNotification.sendNotification(this);
                stopSelf();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startLocationFlow() {
        final DisposableSingleObserver<MyLocationResult> observer = new DisposableSingleObserver<MyLocationResult>() {
            @Override
            public void onSuccess(final MyLocationResult locationResult) {
                /*
                   the overall location flow arrives at a success (here) only with a valid location
                   or indication that location settings should be turned on
                */

                if (locationResult.hasLocation()) {
                    final String latitude = locationUtil.getLatitudeAsString(locationResult.getLocation());
                    final String longitude = locationUtil.getLongitudeAsString(locationResult.getLocation());

                    ClimaCellApi.getSharedInstance().fetchForecasts(BuildConfig.CLIMA_CELL_KEY, latitude, longitude, myCallback);
                } else if (locationResult.getLocationSettingsState() == LocationSettingsState.RESOLUTION_REQUIRED) {
                    LocationSettingsNotification.sendNotification(WeatherCheckService.this, locationResult.getResolvableApiException().getResolution());
                    stopSelf();
                } else {
                    throw new IllegalStateException("This line should not be reached.");
                }
            }

            @Override
            public void onError(final Throwable e) {
                FailureNotification.sendNotification(WeatherCheckService.this);
                stopSelf();
            }
        };

        disposable.add(observer);

        locationUtil.getLocationResultSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void onDestroy() {
        if (locationUtil != null) {
            locationUtil.removeLocationUpdates();
        }
        disposable.dispose();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    private double get12HourSnowInInches(@NonNull final List<Forecast> forecasts) {
        double snowInInches = 0;

        for (int i = 0; i < 12; i++) {
            snowInInches += forecasts.get(i).getSnowAccumulation();
        }

        return (double) Math.round(snowInInches * 10) / 10;
    }
}
