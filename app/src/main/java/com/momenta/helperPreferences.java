package com.momenta;

import android.content.Context;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Joe on 2016-03-10.
 * For Momenta-Capstone
 */
public class helperPreferences {
    private static final String PREFS_NAME = "momenta_prefs";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public helperPreferences(Activity activity) {

        this.sharedPreferences = activity.getSharedPreferences(PREFS_NAME, 0);
        this.editor = sharedPreferences.edit();
    }

    public String GetPreferences(String key) {
        String getValue = sharedPreferences.getString(key, "0");
        return getValue;
    }

    public void SavePreferences(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void clearAllPreferences() {
        editor.clear();
        editor.commit();
    }
}

