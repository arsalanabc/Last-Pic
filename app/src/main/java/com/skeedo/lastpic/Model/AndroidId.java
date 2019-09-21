package com.skeedo.lastpic.Model;

import android.provider.Settings;

import com.skeedo.lastpic.MainActivity;

public class AndroidId {
    public static String USER_ANDROID_ID;

    public static void setUserAndroidId(MainActivity mainActivity) {
        USER_ANDROID_ID = Settings.Secure.getString(mainActivity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
