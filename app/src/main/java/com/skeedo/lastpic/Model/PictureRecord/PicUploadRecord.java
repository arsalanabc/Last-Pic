package com.skeedo.lastpic.Model.PictureRecord;

public class PicUploadRecord {
    private String key;
    private String userId;
    private String timeStamp;
    private String deviceURL;
    private int likes;
    private String firebaseURL;

    public PicUploadRecord(String userId, int likes, String firebaseURL, String deviceURL, String timeStamp) {
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.likes = likes;
        this.firebaseURL = firebaseURL;
        this.deviceURL = deviceURL;
    }

    public PicUploadRecord(){}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDeviceURL(String deviceURL) {
        this.deviceURL = deviceURL;
    }

    public String getUserId() {
        return userId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getFirebaseURL() {
        return firebaseURL;
    }

    public void setFirebaseURL(String firebaseURL) {
        this.firebaseURL = firebaseURL;
    }

    public String getDeviceURL() {
        return deviceURL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
