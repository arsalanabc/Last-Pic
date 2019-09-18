package com.example.lastpic.Model;

public class LastPic {
    private String timeStamp;
    private String upload_records_key;


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LastPic(String picUploadRecordId, String timeStamp) {
        this.upload_records_key = picUploadRecordId;
        this.timeStamp = timeStamp;
    }

    public LastPic() {
    }

    public String getUpload_records_key() {
        return upload_records_key;
    }

    public void setUpload_records_key(String picUploadRecordId) {
        this.upload_records_key = picUploadRecordId;
    }
}
