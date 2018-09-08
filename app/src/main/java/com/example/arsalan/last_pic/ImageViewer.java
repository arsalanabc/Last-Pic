package com.example.arsalan.last_pic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.Touch;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageViewer extends AppCompatActivity {

    private String red = "http://static.hasselblad.com/2016/12/B9403385.jpg";
    private String blue = "http://2.bp.blogspot.com/-GOzVqR_p_ww/VDwWnsJhrNI/AAAAAAAAAH0/U3m5yEhSgj4/s1600/Kajal-Agarwal-HD-Wallpaper-.jpg";
    private String yellow = "http://static.hasselblad.com/2016/10/anders-X1D-sample1.jpg";
    public ArrayList<String> images = new ArrayList<>();
    private int index = 0;
    public int image_length;
    private ImageView imageView;
    private Activity that;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hideBar();
        setContentView(R.layout.image_viewer);
        that = this;

        imageView = findViewById(R.id.imageview);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("photos_url");


//        myRef.push().setValue(red);
//        myRef.push().setValue(blue);
//        myRef.push().setValue(yellow);


        // Attach a listener to read the data at our posts reference
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String image = data.getValue().toString();
                    Log.d("images updated", "Image: " + image);
                    images.add(image);
                    image_length += 1;
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

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
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public void changeImage(int ind){
        index = Math.max(index + ind, 0);
        //Log.d("images updated", " Current Image: " + images.get(index));
        Glide.with(this)
                .load(images.get(index))
                .into(imageView);
    }
}
