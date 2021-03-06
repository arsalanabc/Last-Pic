package com.skeedo.lastpic;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.skeedo.lastpic.Model.AndroidId;
import com.skeedo.lastpic.Model.LastPic;
import com.skeedo.lastpic.Model.PictureRecord.PicUploadRecord;
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
            float maxHeight = Math.min(actualHeight, 1080f); //4032f;
            float maxWidth =  Math.min(actualWidth, 1080f); //4032f;
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
                uploadImage(scaledBitmap);
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

    public String uploadImage (final Bitmap scaledImage){
        ByteArrayOutputStream webpBytes = new ByteArrayOutputStream();
        scaledImage.compress(Bitmap.CompressFormat.WEBP, 100, webpBytes);

        byte[] data = webpBytes.toByteArray();

        if(data.length != 0){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference ref = storageReference.child("pictures/"+ UUID.randomUUID().toString());

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    writeToFirebase(taskSnapshot);
                    showUploadToast(scaledImage);
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
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
        DatabaseReference myRef = database.getReference("upload_records");

        String firebaseURL = taskSnapshot.getDownloadUrl().toString();

        String newKeyToUploadImage = myRef.push().getKey();
        Instant updateTimeStamp = Instant.now();
        PicUploadRecord picUploadRecord = new PicUploadRecord(
                AndroidId.USER_ANDROID_ID,
                0,
                firebaseURL,
                filePath,
                updateTimeStamp.toString());
        myRef.child(newKeyToUploadImage).setValue(picUploadRecord);

        database.getReference("last_pic").child(AndroidId.USER_ANDROID_ID).setValue(
                new LastPic(newKeyToUploadImage, updateTimeStamp.getEpochSecond()*-1));

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, AndroidId.USER_ANDROID_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "pic_updates");
        setFirebaseAnalytics(bundle);

        activity.finish();
    }

    public void setFirebaseAnalytics(Bundle bundle){
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void showUploadToast(Bitmap bmp) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.toast_upload_image,null);
        ImageView imageView = view.findViewById(R.id.upload_image);
        imageView.setImageBitmap(bmp);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(activity);
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
