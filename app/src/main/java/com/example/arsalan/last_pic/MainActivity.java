package com.example.arsalan.last_pic;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends RunTimePermission {

    private static final int REQUEST_PERMISSIONS = 20;
    private String[] permissions ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        MainActivity.super.requestAppPermissions(permissions,
                        REQUEST_PERMISSIONS);

    }

}