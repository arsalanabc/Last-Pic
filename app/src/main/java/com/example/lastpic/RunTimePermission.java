package com.example.lastpic;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;


public abstract class RunTimePermission extends AppCompatActivity {

    private boolean alreadyApproved = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Tracker mTracker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Permission requested");
        sendToGoogleAnalytics("REQUESTED");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }
        if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissions Received.", Toast.LENGTH_LONG).show();
            onPermissionsGranted(requestCode);
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_LONG).show();
            sendToGoogleAnalytics("DENIED");
        }
    }

    public void requestAppPermissions(final String permission, final int requestCode) {

            if (ContextCompat.checkSelfPermission(RunTimePermission.this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Toast.makeText(RunTimePermission.this, "We need "+permission+" to make this app work", Toast.LENGTH_LONG).show();
                    sendToGoogleAnalytics("SHOW_RATIONAL");
                }
                ActivityCompat.requestPermissions(RunTimePermission.this, new String[]{permission}, requestCode);
            } else {
                onPermissionsGranted(requestCode);
            }
    }

    public void onPermissionsGranted(final int requestCode) {
        if(!alreadyApproved){
            sendToGoogleAnalytics("APPROVED");
            Intent uploadImage = new Intent(RunTimePermission.this, UploadActivity.class);
            RunTimePermission.this.startActivity(uploadImage);
            RunTimePermission.this.finish();
            alreadyApproved = true;
        }
    }

    public void sendToGoogleAnalytics (String label){
        Log.d("GA","event_sent: "+label);
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("PERMISSION_LAUNCHED")
                .setLabel(label)
                .build());
    }
}
