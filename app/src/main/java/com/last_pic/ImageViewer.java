package com.last_pic;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.last_pic.Model.LastPic;

import java.util.ArrayList;
import java.util.List;

public class ImageViewer extends AppCompatActivity {

    private int index = 0;
    private ImageView imageView;
    private DatabaseReference firebaseDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;

    String deviceBrand = android.os.Build.MANUFACTURER;
    String deviceModel = android.os.Build.MODEL;
    String osVersion = android.os.Build.VERSION.RELEASE;
    List<LastPic> imageModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        hideBar();
        setContentView(R.layout.image_viewer);

        imageView = findViewById(R.id.imageview);

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
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("last_pic");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageModels = new ArrayList<>();

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    LastPic pic =userSnapshot.getValue(LastPic.class);
                    imageModels.add(pic);
                    //Log.d("images-id", userSnapshot.getKey()+this.toString());
                }

                displayImages();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayImages() {
        GlideApp.with(getApplicationContext())
                .load(imageModels.get(index).getUrl())
                //.transition(DrawableTransitionOptions.withCrossFade())
                //.apply(options)
                //.dontAnimate()
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                //.centerCrop()
                .into(imageView);
    }
}
