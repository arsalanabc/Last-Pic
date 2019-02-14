package com.example.arsalan.last_pic;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class ImageViewer extends AppCompatActivity {

    public ArrayList<String> images = new ArrayList<>();
    private int index = 0;
    private ImageView imageView;
    private Activity that;
    private StorageReference storageReference;
    private DatabaseReference firebaseDatabase;
    public boolean upload_image = true;

    String deviceBrand = android.os.Build.MANUFACTURER;
    String deviceModel = android.os.Build.MODEL;
    String osVersion = android.os.Build.VERSION.RELEASE;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hideBar();
        setContentView(R.layout.image_viewer);
        that = this;

        imageView = findViewById(R.id.imageview);

        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("photos_url");
        storageReference = FirebaseStorage.getInstance().getReference();

        fetchImagesFromFirebase();
        Log.d("info", "images list:"+images.toString());

        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        imageCursor.moveToFirst();

        while (imageCursor.moveToNext()) {
            String imagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            File imageFile = new File(imagePath);
            if (imageFile.canRead() && imageFile.exists()) {
                // we have found the latest picture in the public folder, do whatever you want
                if (upload_image) {
                    upload_image = false;
                    ImageUploader imageUploader = new ImageUploader(imagePath, this);
                    imageUploader.execute(Uri.fromFile(new File(imagePath)));
                }
            }
        }
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
        float y = e.getY();

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
        int imageCount = images.size();

        index += ind;
        index = Math.max(0, index);
        index = Math.min(index, imageCount);

        displayImages();

    }

    public void fetchImagesFromFirebase() {

        firebaseDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String url = userSnapshot.getValue().toString();
                    Log.d("info", "dataSnapshot:" + url);
                    images.add(url);
                }

            Log.d("fb call" , images.toString());
                displayImages();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void displayImages() {
        showToast("showing image");

        GlideApp.with(getApplicationContext())
                .load(images.get(index))
                //.transition(DrawableTransitionOptions.withCrossFade())
                //.apply(options)
                //.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                //.centerCrop()
                .into(imageView);
    }
}
