package com.momenta_app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joe on 2016-03-10.
 * For Momenta-Capstone
 */
public class HelperPreferences {
    private static final String PREFS_NAME = "momenta_prefs";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public HelperPreferences(Context context) {

        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        this.editor = sharedPreferences.edit();
    }

    public String getPreferences(String key, String defaultValue) {
        String getValue = sharedPreferences.getString(key, defaultValue);
        return getValue;
    }

    public void savePreferences(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void clearAllPreferences() {
        editor.clear();
        editor.commit();
    }
}

