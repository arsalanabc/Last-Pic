package com.example.arsalan.last_pic.Model;

import android.app.Activity;
import android.provider.Settings;

public class AndroidId {
    private String value;

    public AndroidId(Activity activity) {
        this.value = Settings.Secure.getString(activity.getContentResolver(),
        Settings.Secure.ANDROID_ID);
    }

    public String getValue() {
        return value;
    }
}
