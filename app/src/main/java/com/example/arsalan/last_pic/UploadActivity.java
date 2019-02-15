package com.example.arsalan.last_pic;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;

public class UploadActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        imageCursor.moveToFirst();

        while (imageCursor.moveToNext()) {
            String imagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            File imageFile = new File(imagePath);
            ImageUploader imageUploader = new ImageUploader(imagePath, this);

            if (imageFile.canRead() && imageFile.exists()) {
                // we have found the latest picture in the public folder, do whatever you want

                imageUploader.execute(Uri.fromFile(new File(imagePath)));
                startActivity(new Intent(this, ImageViewer.class));
                break;
            }

        }

    }
}
