package com.skeedo.lastpic.Model.DeviceInfo;

import android.app.Activity;
import android.provider.Settings;

public class DeviceInfo {

    public static String USER_ANDROID_ID;
    public static String DEVICE_BRAND;
    public static String DEVICE_MODEL;
    public static String OS_VERSION;

    public DeviceInfo(Activity activity){
        this.USER_ANDROID_ID = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        this.DEVICE_BRAND = android.os.Build.MANUFACTURER;
        this.DEVICE_MODEL = android.os.Build.MODEL;
        this.OS_VERSION = android.os.Build.VERSION.RELEASE;
    }

    public DeviceInfo(){}

    public static String getUserAndroidId() {
        return USER_ANDROID_ID;
    }

    public void setUserAndroidId(String userAndroidId) {
        USER_ANDROID_ID = userAndroidId;
    }

    public String getDeviceBrand() {
        return DEVICE_BRAND;
    }

    public void setDeviceBrand(String deviceBrand) {
        DEVICE_BRAND = deviceBrand;
    }

    public String getDeviceModel() {
        return DEVICE_MODEL;
    }

    public void setDeviceModel(String deviceModel) {
        DEVICE_MODEL = deviceModel;
    }

    public String getOsVersion() {
        return OS_VERSION;
    }

    public void setOsVersion(String osVersion) {
        OS_VERSION = osVersion;
    }


}
