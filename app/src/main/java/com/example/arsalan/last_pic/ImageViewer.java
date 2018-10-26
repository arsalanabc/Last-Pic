package com.example.arsalan.last_pic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import id.zelory.compressor.Compressor;

//import io.reactivex.android.schedulers..AndroidSchedulers;


public class ImageViewer extends AppCompatActivity {

//    private String red = "http://static.hasselblad.com/2016/12/B9403385.jpg";
//    private String blue = "http://2.bp.blogspot.com/-GOzVqR_p_ww/VDwWnsJhrNI/AAAAAAAAAH0/U3m5yEhSgj4/s1600/Kajal-Agarwal-HD-Wallpaper-.jpg";
//    private String yellow = "http://static.hasselblad.com/2016/10/anders-X1D-sample1.jpg";
    public ArrayList<String> images = new ArrayList<>();
    private int index = 0;
    public int image_length;
    private ImageView imageView;
    private Activity that;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference;
    //private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hideBar();
        setContentView(R.layout.image_viewer);
        that = this;

        imageView = findViewById(R.id.imageview);

        //Firebase
        //firebaseDatabase = FirebaseDatabase.getInstance().getReference("photos_url").getDatabase();
        storageReference = FirebaseStorage.getInstance().getReference();

        //requestRead();

        // Write a message to the database
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("photos_url");

//        myRef.push().setValue(red);
//        myRef.push().setValue(blue);
//        myRef.push().setValue(yellow);
        /*
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
        */

        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        imageCursor.moveToFirst();
        do {
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            if (fullPath.contains("DCIM")) {
                //--last image from camera --

//                try {
//                    uploadImage(fullPath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                ImageUploader imageUploader = new ImageUploader(fullPath, this);
                imageUploader.execute(Uri.fromFile(new File(fullPath)));

                Glide.with(this)
                        .load(fullPath)
                        .into(imageView);

                showToast(fullPath);
                return;
            }
        }
        while (imageCursor.moveToNext());
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
                   //changeImage(1);
                }
                else {
                    showToast("Touch on right");
                   //changeImage(-1);
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


    private void uploadImage(String imagePath) throws IOException {

        Uri filePath = Uri.fromFile(new File(imagePath));

        //
        //// Create a storage reference from our app
        //StorageReference storageRef = storage.getReference();
        //StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        //uploadTask = riversRef.putFile(file);
        //
        //// Register observers to listen for when the download is done or if it fails
        //uploadTask.addOnFailureListener(new OnFailureListener() {
        //@Override
        //public void onFailure(@NonNull Exception exception) {
        //// Handle unsuccessful uploads
        //}
        //}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        //@Override
        //public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        //// taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
        //// ...
        //}
        //});
        //
        //
        File actualImageFile = new File(filePath.getPath());
        File compressedImageFile = new Compressor(this).compressToFile(actualImageFile);



        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ImageViewer.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ImageViewer.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}
