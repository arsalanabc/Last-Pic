package com.example.lastpic.Model;

public class LastPic {
    private String uid;
    private String upload_records_key;


    public LastPic(String uid, String picUploadRecordId) {
        this.uid = uid;
        this.upload_records_key = picUploadRecordId;
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
