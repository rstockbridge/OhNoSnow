package com.github.rstockbridge.ohnosnow.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class LocationUtil {

    @NonNull
    private final Context context;

    private FusedLocationProviderClient fusedLocationClient;

    private final LocationRequest locationRequest = new LocationRequest()
            .setInterval(SECONDS.toMillis(15))
            .setFastestInterval(SECONDS.toMillis(5))
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setExpirationDuration(SECONDS.toMillis(60));
    // ideally would update UI to "unknown" if location request expires without returning location
    // but no easy way to know when that happens, so UI will continue to show progress bar

    private final LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build();

    private LocationCallback locationCallback;
    private int failedOnLocationResultCounter;

    public LocationUtil(@NonNull final Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
    }

    public Single<MyLocationResult> getLocationResultSingle() {
        return processLastLocationResult(getLastLocationSingle());
    }

    @SuppressLint("MissingPermission")
    @NonNull
    private Single<MyLocationResult> getLastLocationSingle() {
        return Single.create(emitter ->
                fusedLocationClient
                        .getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                emitter.onSuccess(new MyLocationResult(location));
                            } else {
                                /*
                                   null location *may* be due to location settings being turned off
                                   so next step is to check location settings
                                */

                                emitter.onSuccess(new MyLocationResult(LocationSettingsState.CHECK));
                            }
                        })
                        .addOnFailureListener(emitter::onError));
    }

    @NonNull
    private Single<MyLocationResult> getLocationSettingsSingle() {
        return Single.create(emitter ->
                LocationServices.getSettingsClient(context)
                        .checkLocationSettings(settingsRequest)
                        .addOnSuccessListener(locationSettingsResponse -> {
                            if (locationSettingsResponse != null) {
                                emitter.onSuccess(new MyLocationResult(LocationSettingsState.ON));
                            } else {
                                emitter.onError(new Throwable("Check location settings successful but response is null."));
                            }
                        })
                        .addOnFailureListener(e -> {
                            final int statusCode = ((ApiException) e).getStatusCode();
                            if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                                emitter.onSuccess(new MyLocationResult(LocationSettingsState.RESOLUTION_REQUIRED, (ResolvableApiException) e));
                            } else {
                                emitter.onError(e);
                            }
                        }));
    }

    @SuppressLint("MissingPermission")
    private Single<MyLocationResult> getLocationUpdatesSingle() {
        return Single.create(emitter -> {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {
                    if (failedOnLocationResultCounter > 3) {
                        emitter.onError(new Throwable("Location callback successful but response is null after 3 attempts. Will not try again."));
                    }

                    if (locationResult != null) {
                        removeLocationUpdates();

                        final Location location = locationResult.getLastLocation();

                        if (location != null) {
                            emitter.onSuccess(new MyLocationResult(location));
                        } else {
                            emitter.onError(new Throwable("Location callback successful and response non-null but location is null."));
                        }
                    } else {
                        emitter.onError(new Throwable("Location callback successful but response is null."));
                        failedOnLocationResultCounter++;
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        });
    }

    private Single<MyLocationResult> processLastLocationResult(@NonNull Single<MyLocationResult> lastLocationSingle) {
        return lastLocationSingle.flatMap((Function<MyLocationResult, SingleSource<MyLocationResult>>) lastLocationResult -> {
            /*
               lastLocationSingle emits a success only if there is a valid location or to indicate
               location settings should be checked
            */

            if (lastLocationResult.hasLocation()) {
                return Single.just(lastLocationResult);
            } else if (lastLocationResult.getLocationSettingsState() == LocationSettingsState.CHECK) {
                return mapLocationSettingsResultToLocationResult(getLocationSettingsSingle());
            } else {
                throw new IllegalStateException("This line should not be reached.");
            }
        });
    }

    private Single<MyLocationResult> mapLocationSettingsResultToLocationResult(@NonNull Single<MyLocationResult> locationSettingsSingle) {
        return locationSettingsSingle.flatMap((Function<MyLocationResult, SingleSource<MyLocationResult>>) locationSettingsResult -> {
            // locationSettingsSingle emits a success only if location settings are on or require a resolution

            switch (locationSettingsResult.getLocationSettingsState()) {
                case ON:
                    return getLocationUpdatesSingle();
                case RESOLUTION_REQUIRED:
                    return Single.just(new MyLocationResult(LocationSettingsState.RESOLUTION_REQUIRED,
                            locationSettingsResult.getResolvableApiException()));
                default:
                    throw new IllegalStateException("This line should not be reached.");
            }
        });
    }

    public void removeLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public String getLatitudeAsString(@NonNull final Location location) {
        return Double.toString(location.getLatitude());
    }

    public String getLongitudeAsString(@NonNull final Location location) {
        return Double.toString(location.getLongitude());
    }
}
