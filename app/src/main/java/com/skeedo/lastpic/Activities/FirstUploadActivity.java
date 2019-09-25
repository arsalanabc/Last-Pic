package com.skeedo.lastpic.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.skeedo.lastpic.GlideApp;
import com.skeedo.lastpic.ImageUploader;
import com.skeedo.lastpic.ImageViewer;
import com.skeedo.lastpic.Managers.PreferenceManager;
import com.skeedo.lastpic.R;

import java.io.File;

public class FirstUploadActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button uploadBtn;
    private FirebaseAnalytics firebaseAnalytics;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        preferenceManager = new PreferenceManager(this);
        preferenceManager.setFirstTimeLaunch(false);
        hideBar();

        final String imagePath = this.getIntent().getStringExtra("imagePath");
        setContentView(R.layout.first_upload_layout);
        imageView = findViewById(R.id.first_upload_view);

        uploadBtn = findViewById(R.id.first_upload_btn);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpload(imagePath);
            }
        });

        GlideApp.with(getApplicationContext())
                .load(new File(imagePath))
                .into(imageView);

    }

    private void hideBar() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void startUpload(String imagePath){
        startActivity(new Intent(this, ImageViewer.class));
        ImageUploader imageUploader = new ImageUploader(imagePath, this, firebaseAnalytics);
        imageUploader.execute(Uri.fromFile(new File(imagePath)));

    }
}
