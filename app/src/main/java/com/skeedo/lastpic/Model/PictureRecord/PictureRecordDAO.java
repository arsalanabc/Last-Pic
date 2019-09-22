package com.skeedo.lastpic.Model.PictureRecord;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skeedo.lastpic.ImageViewer;
import com.skeedo.lastpic.Model.AndroidId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureRecordDAO {

    private final DatabaseReference firebaseDatabaseRef;
    Map<String, PicUploadRecord> records;
    final String UPLOAD_RECORDS = "upload_records";
    final String LIKES = "likes";
//    TODO Using an unspecified index. Consider adding '".indexOn": "user_liked_pic"' at likes to your security and Firebase Database rules for better performance
    final String USER_LIKED_PIC = "user_liked_pic";
    private ImageViewer imageViewer;


    public PictureRecordDAO(FirebaseDatabase firebaseDatabase, ImageViewer imageViewer){
        this.imageViewer = imageViewer;
        records = new HashMap<>();
        this.firebaseDatabaseRef = firebaseDatabase.getInstance().getReference();
    }

    public void add(PicUploadRecord picUploadRecord){
        records.put(picUploadRecord.getKey(), picUploadRecord);
    }

    public List<PicUploadRecord> getAll(){
        return new ArrayList<>(records.values());
    }

    public void likeOrUnlike(final PicUploadRecord picRecord){
        firebaseDatabaseRef.child(LIKES).child(AndroidId.USER_ANDROID_ID)
                .child(picRecord.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){
                            userActionUpdate("LIKE", picRecord);
                        } else {
                            userActionUpdate("UNLIKE", picRecord);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void update(PicUploadRecord picUploadRecord){
        firebaseDatabaseRef.child(UPLOAD_RECORDS).child(picUploadRecord.getKey()).setValue(picUploadRecord);
    }

    public void save(PicUploadRecord picUploadRecord){
        firebaseDatabaseRef.child(UPLOAD_RECORDS).push().setValue(picUploadRecord);
    }

    private void userActionUpdate(String action, PicUploadRecord picture) {
        switch (action){
            case "UNLIKE":
            {
                updateLikes(picture, -1);
                firebaseDatabaseRef
                        .child(LIKES)
                        .child(AndroidId.USER_ANDROID_ID).child(picture.getKey()).removeValue();
                break;
            }
            case "LIKE":
            {
                updateLikes(picture, 1);
                Map<String, Boolean> pictureLiked = new HashMap<>();
                pictureLiked.put(USER_LIKED_PIC, true);

                firebaseDatabaseRef
                        .child(LIKES)
                        .child(AndroidId.USER_ANDROID_ID)
                        .child(picture.getKey())
                        .setValue(pictureLiked);
                break;
            }
            default:{
            }
        }

        imageViewer.showLikeToast(action);
    }

    private void updateLikes(PicUploadRecord picRecord, int like) {
        picRecord.setLikes(picRecord.getLikes() + like);
        update(picRecord);
    }

}
