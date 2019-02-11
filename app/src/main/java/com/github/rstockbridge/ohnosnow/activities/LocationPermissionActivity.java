package com.github.rstockbridge.ohnosnow.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.github.rstockbridge.ohnosnow.BuildConfig;
import com.github.rstockbridge.ohnosnow.R;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public final class LocationPermissionActivity
        extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 5713;

    public static final String ACTION_LOCATION_PERMISSION_BROADCAST =
            BuildConfig.APPLICATION_ID + ".ACTION_LOCATION_PERMISSION_BROADCAST";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);

        requestLocationPermissions();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // occurs when Easy Permissions directs the user to the app settings directly
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                myFinish();
                break;

            default:
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
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    new AppSettingsDialog.Builder(this).build().show();
                } else {
                    myFinish();
                }
                break;

            default:
                throw new IllegalStateException("This line should not be reached.");
        }
    }

    private void requestLocationPermissions() {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(
                        this,
                        REQUEST_CODE_LOCATION_PERMISSION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        .setRationale(R.string.location_permission_rationale)
                        .setPositiveButtonText(R.string.ok)
                        .setNegativeButtonText("") // negative button is unnecessary here
                        .build());
    }

    private void myFinish() {
        final Intent customBroadcastIntent = new Intent(ACTION_LOCATION_PERMISSION_BROADCAST);
        LocalBroadcastManager.getInstance(this).sendBroadcast(customBroadcastIntent);
        finish();
    }
}
