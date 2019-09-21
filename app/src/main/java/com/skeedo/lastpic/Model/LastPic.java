package com.skeedo.lastpic.Model;

public class LastPic {
    private long timeStamp;
    private String upload_records_key;


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LastPic(String picUploadRecordId, long timeStamp) {
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
