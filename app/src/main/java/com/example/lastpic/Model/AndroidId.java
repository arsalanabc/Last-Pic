package com.example.lastpic.Model;

import android.app.Activity;
import android.provider.Settings;

public class AndroidId {

    public static String getAndroidId(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(),
        Settings.Secure.ANDROID_ID);
    }

}
