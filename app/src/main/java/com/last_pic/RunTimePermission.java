package com.last_pic;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;


public abstract class RunTimePermission extends AppCompatActivity {

    private boolean alreadyApproved = false;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }
        if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode);
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_LONG).show();
        }
    }

    public void requestAppPermissions(final String[] permissions, final int requestCode) {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : permissions){

            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale) {
                    Toast.makeText(RunTimePermission.this, "We need %s to make this app work", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(this, permissions, requestCode);
                }
            } else {
                onPermissionsGranted(requestCode);
            }
        }
    }

    public void onPermissionsGranted(final int requestCode) {
        if(!alreadyApproved){
            Toast.makeText(this, "Permissions Received.", Toast.LENGTH_LONG).show();

            Intent uploadImage = new Intent(RunTimePermission.this, UploadActivity.class);
            finish();
            RunTimePermission.this.startActivity(uploadImage);
            alreadyApproved = true;
        }
    }
}
