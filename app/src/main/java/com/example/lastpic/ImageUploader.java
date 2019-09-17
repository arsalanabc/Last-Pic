package com.example.lastpic;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;


import com.example.lastpic.Model.AndroidId;
import com.example.lastpic.Model.LastPic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static android.net.Uri.parse;

public class ImageUploader extends AsyncTask <Uri, Integer , String> {
    String filePath;
    Activity activity;
    private FirebaseAnalytics firebaseAnalytics;

    ImageUploader(String filePath, Activity activity, FirebaseAnalytics firebaseAnalytics){
        this.filePath = filePath;
        this.activity = activity;
        this.firebaseAnalytics = firebaseAnalytics;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Uri... uris) {
        Log.d("Doing the onBackground", "do");
        String filePath = String.valueOf(uris[0]);
        return compressImageNew(filePath, activity);
    }

    public String compressImageNew (String imageUri, Activity activity) {
        try {
            String filePath = getRealPathFromURI(imageUri, activity);

            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = actualHeight * 0.6f; //4032f;
            float maxWidth =  actualWidth * 0.6f; //4032f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if(actualHeight <= 0 || actualWidth <= 0){
                Log.e("Upload_issue", "image size is less than zero");
                this.cancel(true);
                activity.finish();
            }

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas;
            if (scaledBitmap != null ) {
                canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            }

            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                if (scaledBitmap != null) {
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (scaledBitmap != null) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                uploadImage(bytes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "done";
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private static String getRealPathFromURI(String contentURI, Activity activity) {
        Uri contentUri = parse(contentURI);
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public String uploadImage (ByteArrayOutputStream bytes){
        byte[] data = bytes.toByteArray();

        if(data.length != 0){
//            final ProgressDialog progressDialog = new ProgressDialog(activity);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference ref = storageReference.child("pictures/"+ UUID.randomUUID().toString());


            Log.d("putBytes", "putting bytes");
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressDialog.dismiss();

                    writeToFirebase(taskSnapshot);

                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    progressDialog.dismiss();
                    Toast.makeText(activity, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        UploadTask.TaskSnapshot downUri = task.getResult();
                        Log.d("Final URL", "onComplete: Url: " + downUri.toString());
                    }
                }
            });
        }
        return "done";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeToFirebase(UploadTask.TaskSnapshot taskSnapshot) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("last_pic");

        String firebaseURL = taskSnapshot.getDownloadUrl().toString();

        String androidId = AndroidId.getAndroidId(activity);
        LastPic lastPic = new LastPic(androidId, firebaseURL, filePath, 0, Instant.now().toString());

        myRef.child(androidId).setValue(lastPic);

        Toast.makeText(activity, "Your last picture is uploaded!", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, androidId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "pic_updates");
        setFirebaseAnalytics(bundle);

        activity.finish();
    }

    public void setFirebaseAnalytics(Bundle bundle){
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
