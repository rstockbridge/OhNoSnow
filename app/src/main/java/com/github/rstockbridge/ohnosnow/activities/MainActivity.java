package com.github.rstockbridge.ohnosnow.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.rstockbridge.ohnosnow.BuildConfig;
import com.github.rstockbridge.ohnosnow.R;
import com.github.rstockbridge.ohnosnow.api.DarkSkyApi;
import com.github.rstockbridge.ohnosnow.api.MyCallback;
import com.github.rstockbridge.ohnosnow.api.models.ForecastData;
import com.github.rstockbridge.ohnosnow.notifications.LocationPermissionNotification;
import com.github.rstockbridge.ohnosnow.notifications.WeatherNotification;
import com.github.rstockbridge.ohnosnow.utils.LocationUtil;

import pub.devrel.easypermissions.EasyPermissions;

public final class MainActivity
        extends AppCompatActivity
        implements LocationUtil.LocationSuccessListener {

    private LocationPermissionReceiver receiver = new LocationPermissionReceiver();

    private LocationUtil locationUtil;

    private MyCallback<ForecastData> myCallback = new MyCallback<ForecastData>() {
        @Override
        public void onSuccess(@NonNull final ForecastData result) {
            final double snowInInches = result.get12HourSnowInInches();
            WeatherNotification.sendForecastNotification(MainActivity.this, snowInInches);
        }

        @Override
        public void onError() {
            WeatherNotification.sendFailureNotification(MainActivity.this);
        }

        @Override
        public void onFailure() {
            WeatherNotification.sendFailureNotification(MainActivity.this);
        }
    };

    private TextView label;
    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(LocationPermissionActivity.ACTION_LOCATION_PERMISSION_BROADCAST));

        initializeViews();
        syncViewsWithLocationPermission();

        locationUtil = new LocationUtil(this, this);

        if (!locationPermissionsAreGranted()) {
            LocationPermissionNotification.sendNotification(this);
        } else {
            locationUtil.requestLocation(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onLocationSuccess(@NonNull final String latitude, @NonNull final String longitude) {
        DarkSkyApi.getSharedInstance().fetchForecast(BuildConfig.DARK_SKY_KEY, latitude, longitude, myCallback);
    }

    private void initializeViews() {
        label = findViewById(R.id.label);

        spinner = findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.choices_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
    }

    private boolean locationPermissionsAreGranted() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void syncViewsWithLocationPermission() {
        if (locationPermissionsAreGranted()) {
            label.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            label.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
        }
    }

    public class LocationPermissionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String intentAction = intent.getAction();

            if (intentAction != null) {
                switch (intentAction) {
                    case LocationPermissionActivity.ACTION_LOCATION_PERMISSION_BROADCAST:
                        syncViewsWithLocationPermission();
                        if (locationPermissionsAreGranted()) {
                            locationUtil.requestLocation(MainActivity.this, MainActivity.this);
                        }
                        break;
                    default:
                        throw new IllegalStateException("This line should not be reached.");
                }
            }
        }
    }
}
