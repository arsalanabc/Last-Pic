package com.example.lastpic;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lastpic.Managers.ConnectionManager;
import com.example.lastpic.Model.AndroidId;
import com.example.lastpic.Model.PictureRecord.PicUploadRecord;
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

        this.setContentView(R.layout.upload_activity);
        TextView textView = findViewById(R.id.warning);
        Button closeButton = findViewById(R.id.app_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeApp();
            }
        });

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
                ConnectionManager connectionManager = new ConnectionManager(this);
                if(connectionManager.isConnected()){
                checkIfImageNeedsToBeUpdated(imagePath);
                startActivity(new Intent(this, ImageViewer.class));}
                else{
                    textView.setText("No Internet Connection!");
                }
                break;
            }
        }
    }

    private void closeApp() {
        this.finish();
    }

    private void checkIfImageNeedsToBeUpdated(final String currentImagePath){
        String userId = AndroidId.getAndroidId(this);
        Query query = dbRef.child("last_pic/"+userId).child("upload_records_key");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {

                if(userSnapshot.getValue() == null){
                    updateImage(currentImagePath);
                } else {
                    String key = userSnapshot.getValue().toString();
                    Query queryForRecord = dbRef.child("upload_records").child(key);
                    queryForRecord.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot data) {
                            System.out.println(data);
                            if(!data.getValue(PicUploadRecord.class)
                                    .getDeviceURL().equals(currentImagePath)
                            ){
                                updateImage(currentImagePath);
                            } else {
                                noUpdateNeeded();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
