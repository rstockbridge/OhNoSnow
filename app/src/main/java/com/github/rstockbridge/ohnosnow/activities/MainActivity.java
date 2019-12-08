package com.github.rstockbridge.ohnosnow.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rstockbridge.ohnosnow.R;
import com.github.rstockbridge.ohnosnow.alarm.AlarmHelper;
import com.github.rstockbridge.ohnosnow.utils.EasyPermissionsHelper;
import com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper;

import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.NotificationPref;
import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.NotificationPref.NONE;
import static com.github.rstockbridge.ohnosnow.utils.SharedPreferenceHelper.getNotificationPref;

public final class MainActivity
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private static final int REQUEST_CODE_LOCATION_PERMISSION_FLOW_COMPLETE = 1049;

    private TextView label;
    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        if (!EasyPermissionsHelper.allTheTimeLocationAccessGranted(this)) {
            final Intent intent = new Intent(this, LocationPermissionActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOCATION_PERMISSION_FLOW_COMPLETE);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION_FLOW_COMPLETE) {
            if (resultCode == RESULT_OK) {
                syncViewsWithLocationPermission(EasyPermissionsHelper.allTheTimeLocationAccessGranted(this));
            }
        } else {
            throw new IllegalStateException("This line should not be reached.");
        }
    }

    @Override
    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
        final NotificationPref selectedNotificationPref = (NotificationPref) parent.getSelectedItem();
        SharedPreferenceHelper.setNotificationPref(this, selectedNotificationPref);

        if (selectedNotificationPref == NONE) {
            AlarmHelper.cancelAlarm(this);
        } else if (EasyPermissionsHelper.allTheTimeLocationAccessGranted(this)) {
            AlarmHelper.setAlarm(this);
        }
    }

    @Override
    public void onNothingSelected(final AdapterView<?> parent) {
        // this method intentionally left blank
    }

    private void initializeViews() {
        label = findViewById(R.id.label);

        spinner = findViewById(R.id.spinner);

        final ArrayAdapter<NotificationPref> adapter =
                new ArrayAdapter<>(this, R.layout.spinner_item, NotificationPref.values());
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(getNotificationPref(this).ordinal());
        spinner.setOnItemSelectedListener(this);

        final ImageView darkSkyAttribution = findViewById(R.id.darkSkyAttribution);
        darkSkyAttribution.setOnClickListener(v -> {
            final String url = "https://darksky.net/poweredby/";
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }

    private void syncViewsWithLocationPermission(final boolean locationPermissionGranted) {
        if (locationPermissionGranted) {
            label.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            label.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
        }
    }
}
