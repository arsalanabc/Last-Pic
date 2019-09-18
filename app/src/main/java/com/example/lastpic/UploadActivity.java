package com.example.lastpic;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.example.lastpic.Model.AndroidId;
import com.example.lastpic.Model.LastPic;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class UploadActivity extends Activity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private Tracker mTracker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("UploadActivity requested");
        sendToGoogleAnalytics("STARTED");

        final String[] imageColumns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
                null, null, imageOrderBy);

        while (imageCursor.moveToNext()) {
            String data = MediaStore.Images.ImageColumns.DATA;
            int columnIndex = imageCursor.getColumnIndex(data);
            String imagePath = imageCursor.getString(columnIndex);
            File imageFile = new File(imagePath);


            if (imageFile.canRead() && imageFile.exists()) {
                sendToGoogleAnalytics("IMAGE_FOUND");

                // we have found the latest picture in the public folder, do whatever you want
                checkIfImageNeedsToBeUpdated(imagePath);
                startActivity(new Intent(this, ImageViewer.class));
                break;
            }
        }
    }

    private void checkIfImageNeedsToBeUpdated(final String currentImagePath){
        String userId = AndroidId.getAndroidId(this);
        Query query = dbRef.child("last_pic").orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0){
                    updateImage(currentImagePath);
                } else {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if(!data.getValue(LastPic.class)
                                .getDeviceURL().equals(currentImagePath)
                        ){
                            updateImage(currentImagePath);
                        } else {
                            noUpdateNeeded();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void noUpdateNeeded() {
        sendToGoogleAnalytics("NO_UPLOAD_NEEDED");
        this.finish();
    }

    private void updateImage(String imagePath) {
        sendToGoogleAnalytics("UPLOAD_NEEDED");
        ImageUploader imageUploader = new ImageUploader(imagePath, this, mFirebaseAnalytics);
        imageUploader.execute(Uri.fromFile(new File(imagePath)));
    }

    public void sendToGoogleAnalytics (String label){
        Log.d("GA","event_sent: "+label);
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("UPLOAD")
                .setLabel(label)
                .build());
    }

}