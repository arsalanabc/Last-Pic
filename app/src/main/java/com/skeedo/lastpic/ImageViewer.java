package com.skeedo.lastpic;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skeedo.lastpic.Model.AndroidId;
import com.skeedo.lastpic.Model.PictureRecord.PicUploadRecord;
import com.skeedo.lastpic.Model.PictureRecord.PictureRecordDAO;

import java.util.ArrayList;
import java.util.List;

public class ImageViewer extends AppCompatActivity {

    private int index = 0;
    private ImageView imageView;
    private DatabaseReference firebaseDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Tracker mTracker;
    final int PRELOAD_IMAGES_NUM = 2;
    final int LIKE_LAYOUT = R.layout.toast_like_layout;
    final int UNLIKE_LAYOUT = R.layout.toast_unlike_layout;

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
        pictureRecordDAO = new PictureRecordDAO(firebaseDatabase.getDatabase(), this);

        likesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PicUploadRecord pic = imageModels.get(index);
                pictureRecordDAO.likeOrUnlike(pic);
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
                    preloadNextImages();
                    changeImage(1);
                }
                else {
                   changeImage(-1);
                }
        }
        return true;
    }

    public float getWidth(){
        return this.getWindowManager().getDefaultDisplay().getWidth();
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

    private void preloadNextImages() {
        for (int i = index; i < Math.min(index+PRELOAD_IMAGES_NUM, imageModels.size()-1); i++){
            Glide.with(this.getApplicationContext()).load(imageModels.get(i+1).getFirebaseURL())
                    .preload();
        }
    }

    public void fetchImagesFromFirebase() {

        firebaseDatabase.child("last_pic").orderByChild("timeStamp").addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {

                    if(!userSnapshot.getKey().equals(AndroidId.USER_ANDROID_ID)){

                        String keyToPic = userSnapshot.child("upload_records_key").getValue().toString();

                        firebaseDatabase.child("upload_records").child(keyToPic)
                                .addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot pictureSnapshot) {
                                        if(pictureSnapshot.getValue() != null){
                                            PicUploadRecord pic = pictureSnapshot.getValue(PicUploadRecord.class);
                                            pic.setKey(pictureSnapshot.getKey());
                                            imageModels.add(pic);
                                            pictureRecordDAO.add(pic);
                                            displayImages();
                                        }
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

    public void showLikeToast(String action) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view;
       if(action.equals("LIKE")){
           view =layoutInflater.inflate(LIKE_LAYOUT,null);
       } else {
           view = layoutInflater.inflate(UNLIKE_LAYOUT,null);
       }

        Toast toast = new Toast(this);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
