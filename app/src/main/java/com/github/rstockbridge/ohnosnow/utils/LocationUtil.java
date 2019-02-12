package com.github.rstockbridge.ohnosnow.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.github.rstockbridge.ohnosnow.notifications.LocationFailureNotification;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class LocationUtil {

    public interface LocationSuccessListener {
        void onLocationSuccess(@NonNull final String latitude, @NonNull final String longitude);
    }

    private LocationSuccessListener listener;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationRequest locationRequest = new LocationRequest()
            .setInterval(SECONDS.toMillis(15))
            .setFastestInterval(SECONDS.toMillis(5))
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setExpirationDuration(SECONDS.toMillis(60));
    // no easy way to know if the location request expires without returning a location

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(final LocationResult locationResult) {
            if (locationResult == null) {
                LocationFailureNotification.sendNotification(context, false);
                return;
            }

            final Location location = locationResult.getLastLocation();
            if (location == null) {
                LocationFailureNotification.sendNotification(context, false);
                return;
            }

            fusedLocationClient.removeLocationUpdates(locationCallback);
            listener.onLocationSuccess(getLatitudeAsString(location), getLongitudeAsString(location));
        }
    };

    @NonNull
    private final Context context;

    public LocationUtil(@NonNull final Context context, @NonNull final LocationSuccessListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    public void requestLocation(@NonNull final Context context, @NonNull final LocationSuccessListener listener) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        if (location != null) {
                            listener.onLocationSuccess(getLatitudeAsString(location), getLongitudeAsString(location));
                        } else {
                            getFreshLocation();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        getFreshLocation();
                    }
                });
    }

    private void getFreshLocation() {
        final LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();

        final SettingsClient client = LocationServices.getSettingsClient(context);
        client.checkLocationSettings(settingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(final LocationSettingsResponse locationSettingsResponse) {
                        if (locationSettingsResponse == null) {
                            LocationFailureNotification.sendNotification(context, false);
                        } else {
                            startLocationUpdates();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        LocationFailureNotification.sendNotification(context, false);
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private String getLatitudeAsString(@NonNull final Location location) {
        return Double.toString(location.getLatitude());
    }

    private String getLongitudeAsString(@NonNull final Location location) {
        return Double.toString(location.getLongitude());
    }
}
