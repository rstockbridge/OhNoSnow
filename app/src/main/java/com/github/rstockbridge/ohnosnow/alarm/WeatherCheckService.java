package com.github.rstockbridge.ohnosnow.alarm;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.github.rstockbridge.ohnosnow.BuildConfig;
import com.github.rstockbridge.ohnosnow.api.DarkSkyApi;
import com.github.rstockbridge.ohnosnow.api.MyCallback;
import com.github.rstockbridge.ohnosnow.api.models.ForecastData;
import com.github.rstockbridge.ohnosnow.location.LocationSettingsState;
import com.github.rstockbridge.ohnosnow.location.LocationUtil;
import com.github.rstockbridge.ohnosnow.location.MyLocationResult;
import com.github.rstockbridge.ohnosnow.notifications.FailureNotification;
import com.github.rstockbridge.ohnosnow.notifications.FetchingNotification;
import com.github.rstockbridge.ohnosnow.notifications.LocationPermissionNotification;
import com.github.rstockbridge.ohnosnow.notifications.LocationSettingsNotification;
import com.github.rstockbridge.ohnosnow.notifications.WeatherNotification;
import com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.getNotificationPref;

public class WeatherCheckService extends Service {

    // do not respond to alarm if user does not want any notifications
    // still report errors if user requests snow-only notifications


    public static Intent getAlarmIntentService(@NonNull final Context context) {
        return new Intent(context, WeatherCheckService.class);
    }

    final SharedPreferenceHelper.NotificationPref notificationPref = getNotificationPref(WeatherCheckService.this);

    private final CompositeDisposable disposable = new CompositeDisposable();

    private LocationUtil locationUtil;

    private MyCallback<ForecastData> myCallback = new MyCallback<ForecastData>() {
        @Override
        public void onSuccess(@NonNull final ForecastData result) {
            final double snowInInches = result.get12HourSnowInInches();

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
            Crashlytics.logException(t);
            FailureNotification.sendNotification(WeatherCheckService.this);
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        startForeground(1, FetchingNotification.getNotification(this));

        if (notificationPref != SharedPreferenceHelper.NotificationPref.NONE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
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

                    DarkSkyApi.getSharedInstance().fetchForecast(BuildConfig.DARK_SKY_KEY, latitude, longitude, myCallback);
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
        locationUtil.removeLocationUpdates();
        disposable.dispose();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
