package com.last_pic.Model;

public class LastPic {
    private String userId;
    private String url;
    private int likes;
    private String dateUpdated;

    public LastPic(String userId, String url, int likes, String dateUpdated) {
        this.userId = userId;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
