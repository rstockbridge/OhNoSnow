package com.github.rstockbridge.ohnosnow.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rstockbridge.ohnosnow.R;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public final class LocationPermissionActivity
        extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 5713;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);

        requestLocationPermissions();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // occurs when Easy Permissions directs the user to the app settings directly
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            myFinish();
        } else {
            throw new IllegalStateException("This line should not be reached.");
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(final int requestCode, @NonNull final List<String> perms) {
        myFinish();
    }

    @Override
    public void onPermissionsDenied(final int requestCode, @NonNull final List<String> perms) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            } else {
                myFinish();
            }
        } else {
            throw new IllegalStateException("This line should not be reached.");
        }
    }

    private void requestLocationPermissions() {
        final PermissionRequest.Builder builder;
        @StringRes final int rationale;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder = new PermissionRequest.Builder(
                    this,
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            rationale = R.string.all_the_time_location_permission_rationale;

        } else {
            builder = new PermissionRequest.Builder(
                    this,
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            rationale = R.string.foreground_location_permission_rationale;
        }

        EasyPermissions.requestPermissions(builder
                .setRationale(rationale)
                .setPositiveButtonText(R.string.ok)
                .setNegativeButtonText("") // negative button is unnecessary here
                .build());
    }

    private void myFinish() {
        setResult(RESULT_OK);
        finish();
    }
}
