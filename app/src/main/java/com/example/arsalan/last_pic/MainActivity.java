package com.example.arsalan.last_pic;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

public class MainActivity extends RunTimePermission {

    private static final int REQUEST_PERMISSIONS = 20;
    private String[] permissions ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBar();

        permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE };
        MainActivity.super.requestAppPermissions(permissions,
                REQUEST_PERMISSIONS);
    }

    private void hideBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

}