package com.example.arsalan.last_pic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class DownloadImage extends AsyncTask <String, String , Bitmap> {
    Activity activity;
    ImageView imageView;

    public DownloadImage(Activity activity, ImageView imageView){
        this.activity = activity;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {


        return null;
    }

    protected void onPostExecute(Bitmap result) {
        Glide.with(this.activity)
                .load("https://c-ash.smule.com/sf/s34/arr/94/d4/adcf908f-8b19-434f-b519-3cc3ee0729ae_256.jpg")
                .into(this.imageView);
        //this.imageView.setImageBitmap(result);
    }
}
