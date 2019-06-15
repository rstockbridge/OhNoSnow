package com.github.rstockbridge.ohnosnow.location;

import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;

public final class MyLocationResult {

    /* every instance of MyLocationResult should contain
       - a non-null location, a null locationSettingsState, and a null resolvable, or
       - a null location, a non-null locationSettingsState (not RESOLUTION_REQUIRED), and a null
         resolvable, or
       - a null location, a non-null locationSettingsState (RESOLUTION_REQUIRED), and a non-null
         resolvable
    */

    @Nullable
    private Location location;

    @Nullable
    private LocationSettingsState locationSettingsState;

    @Nullable
    private ResolvableApiException resolvable;

    MyLocationResult(@NonNull final Location location) {
        this.location = location;
    }

    MyLocationResult(@NonNull final LocationSettingsState locationSettingsState) {
        this.locationSettingsState = locationSettingsState;
    }

    MyLocationResult(
            @NonNull final LocationSettingsState locationSettingsState,
            @NonNull ResolvableApiException resolvable) {
        this.locationSettingsState = locationSettingsState;
        this.resolvable = resolvable;
    }

    @NonNull
    public Location getLocation() {
        if (location != null && locationSettingsState == null) {
            return location;
        } else {
            throw new IllegalStateException("This line should not be reached.");
        }
    }

    @NonNull
    public LocationSettingsState getLocationSettingsState() {
        if (location == null && locationSettingsState != null) {
            return locationSettingsState;
        } else {
            throw new IllegalStateException("This line should not be reached.");
        }
    }

    @NonNull
    public ResolvableApiException getResolvableApiException() {
        if (location == null
                && locationSettingsState == LocationSettingsState.RESOLUTION_REQUIRED
                && resolvable != null) {
            return resolvable;
        } else {
            throw new IllegalStateException("This line should not be reached.");
        }
    }

    public boolean hasLocation() {
        return location != null;
    }
}
