package com.example.arsalan.last_pic.Model;

public class LastPic {
    private String userId;
    private String firebaseURL;
    private String deviceURL;
    private int likes;
    private String dateUpdated;

    public LastPic(String userId, String firebaseURL, String deviceURL, int likes, String dateUpdated) {
        this.userId = userId;
        this.firebaseURL = firebaseURL;
        this.deviceURL = deviceURL;
        this.likes = likes;
        this.dateUpdated = dateUpdated;
    }

    public LastPic() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setDeviceURL(String deviceURL) {
        this.deviceURL = deviceURL;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

}
