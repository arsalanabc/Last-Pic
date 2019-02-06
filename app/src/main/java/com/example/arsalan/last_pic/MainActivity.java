package com.example.arsalan.last_pic;

import android.Manifest;
import android.os.Bundle;

public class MainActivity extends RunTimePermission {

    private static final int REQUEST_PERMISSIONS = 20;
    private String[] permissions ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };

        MainActivity.super.requestAppPermissions(permissions,
                        REQUEST_PERMISSIONS);

    }

}