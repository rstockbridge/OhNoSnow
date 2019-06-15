package com.github.rstockbridge.ohnosnow.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rstockbridge.ohnosnow.BuildConfig;
import com.github.rstockbridge.ohnosnow.R;

public final class LocationSettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_SETTINGS = 6124;

    public static final String ACTION_LOCATION_SETTINGS_BROADCAST =
            BuildConfig.APPLICATION_ID + ".ACTION_LOCATION_SETTINGS_BROADCAST";

    public static final String EXTRA_RESOLVABLE_PENDING_INTENT = "resolvablePendingIntent";
    public static final String EXTRA_LOCATION_ON = "locationOn";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_settings);

        final PendingIntent resolvablePendingIntent = getIntent().getParcelableExtra(EXTRA_RESOLVABLE_PENDING_INTENT);

        try {
            startIntentSenderForResult(
                    resolvablePendingIntent.getIntentSender(),
                    REQUEST_CODE_LOCATION_SETTINGS,
                    null,
                    0,
                    0,
                    0);
        } catch (IntentSender.SendIntentException e) {
            myFinish(false);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                myFinish(true);
            } else {
                myFinish(false);
            }
        } else {
            throw new IllegalStateException("This line should not be reached.");
        }
    }

    private void myFinish(final boolean locationOn) {
        final Intent customBroadcastIntent = new Intent(ACTION_LOCATION_SETTINGS_BROADCAST);
        customBroadcastIntent.putExtra(EXTRA_LOCATION_ON, locationOn);
        LocalBroadcastManager.getInstance(this).sendBroadcast(customBroadcastIntent);
        finish();
    }
}
