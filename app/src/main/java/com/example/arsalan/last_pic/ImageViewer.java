package com.example.arsalan.last_pic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.Touch;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ImageViewer extends AppCompatActivity {
    private ImageViewer imageViewer;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hideBar();
        setContentView(R.layout.image_viewer);
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
                }
                else {
                    showToast("Touch on right");
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
}
