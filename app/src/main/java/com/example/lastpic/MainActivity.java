package com.example.lastpic;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

public class MainActivity extends RunTimePermission {

    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBar();

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