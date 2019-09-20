package com.example.lastpic.Model;

import android.provider.Settings;

import com.example.lastpic.MainActivity;

public class AndroidId {
    public static String USER_ANDROID_ID;

    public static void setUserAndroidId(MainActivity mainActivity) {
        USER_ANDROID_ID = Settings.Secure.getString(mainActivity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
