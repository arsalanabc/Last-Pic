package com.example.lastpic.Model;

import android.app.Activity;
import android.provider.Settings;

public class AndroidId {
    static String id;

    public static String getAndroidId(Activity activity) {

        id = Settings.Secure.getString(activity.getContentResolver(),
        Settings.Secure.ANDROID_ID);
        return id;
    }

    public static String AndroidId (){
        return id;
    }

}
