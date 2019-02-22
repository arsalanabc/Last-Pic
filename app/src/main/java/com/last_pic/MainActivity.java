package com.last_pic;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends RunTimePermission {

    private static final int REQUEST_PERMISSIONS = 20;
    private String[] permissions ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_screen);

        Button getStarted_btn = findViewById(R.id.dummy_button);

        hideBar();

        getStarted_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
                MainActivity.super.requestAppPermissions(permissions,
                        REQUEST_PERMISSIONS);
                finish();
            }
        });
    }

    private void hideBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

}