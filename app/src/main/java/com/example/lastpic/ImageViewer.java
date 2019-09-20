package com.example.lastpic;

import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.lastpic.Model.AndroidId;
import com.example.lastpic.Model.PictureRecord.PicUploadRecord;
import com.example.lastpic.Model.PictureRecord.PictureRecordDAO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImageViewer extends AppCompatActivity {

    private int index = 0;
    private ImageView imageView;
    private DatabaseReference firebaseDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Tracker mTracker;

    String deviceBrand = android.os.Build.MANUFACTURER;
    String deviceModel = android.os.Build.MODEL;
    String osVersion = android.os.Build.VERSION.RELEASE;
    List<PicUploadRecord> imageModels = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView likesTextView;
    private PictureRecordDAO pictureRecordDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        hideBar();
        setContentView(R.layout.image_viewer);
        imageView = findViewById(R.id.imageview);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        likesTextView = findViewById(R.id.likes);
        pictureRecordDAO = new PictureRecordDAO();

        likesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like();
            }
        });

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("ImageViewer Launched");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("IMAGEVIEW_LAUNCHED")
                .setLabel("ENTERED")
                .build());

        fetchImagesFromFirebase();
    }

    private void like() {
        class LikeDAO {
            String userId;
            String picRecordId;

            public LikeDAO() {
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getPicRecordId() {
                return picRecordId;
            }

            public void setPicRecordId(String picRecordId) {
                this.picRecordId = picRecordId;
            }
        }
        PicUploadRecord pic = imageModels.get(index);
        pic.setLikes(pic.getLikes()+1);
        firebaseDatabase.child("upload_records").child(pic.getKey()).setValue(pic);

    }

    private void hideBar() {
            // Hide UI first
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // reverse direction of rotation above the mid-line
                if (x >= getWidth() / 2) {
                   showToast("Touch on left");
                   changeImage(1);
                }
                else {
                    showToast("Touch on right");
                   changeImage(-1);
                }
        }
        return true;
    }

    public float getWidth(){
        return this.getWindowManager().getDefaultDisplay().getWidth();
    }

    public void showToast(String msg){
        //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public void changeImage(int ind){
        int imageCount = imageModels.size() - 1 ;

        if (index + ind < 0) {
            index = 0;
        } else if (index + ind > imageCount) {
            index = Math.max(imageCount, 0);
        } else {
            index += ind;
        }

        displayImages();
    }

    public void fetchImagesFromFirebase() {

        //Firebase
        firebaseDatabase.child("last_pic").orderByChild("timeStamp").addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {

                    if(!userSnapshot.getKey().equals(AndroidId.getAndroidId(ImageViewer.this))){

                        String keyToPic = userSnapshot.child("upload_records_key").getValue().toString();

                        firebaseDatabase.child("upload_records").child(keyToPic)
                                .addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot pictureSnapshot) {
                                        PicUploadRecord pic = pictureSnapshot.getValue(PicUploadRecord.class);
                                        pic.setKey(pictureSnapshot.getKey());
                                        imageModels.add(pic);
                                        pictureRecordDAO.add(pic);
                                        displayImages();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayImages() {
        PicUploadRecord pictureRecord = imageModels.get(index);
        pictureRecordDAO.likeAPicture(pictureRecord);
        likesTextView.setText(String.valueOf(pictureRecord.getLikes()));

        progressBar.setVisibility(View.VISIBLE);
        GlideApp.with(getApplicationContext())
                .load(pictureRecord.getFirebaseURL())
                //.transition(DrawableTransitionOptions.withCrossFade())
                //.apply(options)
                //.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                //.centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }
}
