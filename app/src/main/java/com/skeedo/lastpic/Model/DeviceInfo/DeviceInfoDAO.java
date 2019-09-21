package com.skeedo.lastpic.Model.DeviceInfo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeviceInfoDAO {

    final String DEVICE_INFO = "device_info";
    private final DatabaseReference firebaseDatabase;
    DeviceInfo deviceInfo;

    public DeviceInfoDAO (FirebaseDatabase firebaseDatabase, DeviceInfo deviceInfo){
        this.firebaseDatabase = firebaseDatabase.getReference(DEVICE_INFO);
        this.deviceInfo = deviceInfo;
    }

    public void update(){
        firebaseDatabase.child(DeviceInfo.USER_ANDROID_ID)
                .setValue(deviceInfo);
    }
}
