package com.example.lastpic;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends RunTimePermission {

    private static final int REQUEST_PERMISSIONS = 20;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBar();

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MainActivity Launched");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("APP_LAUNCHED")
                .setLabel("ENTERED")
                .build());

        MainActivity.super.requestAppPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                REQUEST_PERMISSIONS);
    }

    private void hideBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

}