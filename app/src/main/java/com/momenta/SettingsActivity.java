package com.momenta;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Joe on 2016-02-09.
 * For Tip Calculator
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREFS_NAME = "momenta_prefs";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        this.getSharedPreferences(PREFS_NAME, 0).registerOnSharedPreferenceChangeListener(this);

        PreferenceManager.setDefaultValues(this, R.xml.settings,
                false);
        initSummary(getPreferenceScreen());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                    Preference singlePref = preferenceGroup.getPreference(j);
                    updatePreference(singlePref, singlePref.getKey());
                }
            } else {
                updatePreference(preference, preference.getKey());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initSummary(Preference p) {
        updatePrefSummary(p);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key), key);
    }

    private void updatePreference(Preference preference, String key) {
//        Log.d("preference.toSt",preference.toString());
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
            return;
        }
        SharedPreferences sharedPrefs = this.getSharedPreferences(PREFS_NAME, 0);
        if (key.equals("interval_time") || key.equals(Constants.SHPREF_INTERVAL_HOURS) || key.equals(Constants.SHPREF_INTERVAL_MINS)) {
            String summary;
            String hours = sharedPrefs.getString(Constants.SHPREF_INTERVAL_HOURS, "0");
            String minutes = sharedPrefs.getString(Constants.SHPREF_INTERVAL_MINS, "0");


            if ((hours.equals("0") || hours.equals("00")) && (minutes.equals("0") || minutes.equals("00"))) {
                summary = getString(R.string.interval_time_summary_never_remind);
            } else if (hours.equals("0") || hours.equals("00")) {
                summary = getString(R.string.interval_time_summary) + " " + minutes + " " + getString(R.string.interval_time_summary_minutes);
            } else if (minutes.equals("0") || minutes.equals("00")) {
                summary = getString(R.string.interval_time_summary) + " " + hours + " " + getString(R.string.interval_time_summary_hours);
            } else {
                summary = getString(R.string.interval_time_summary) + " " + hours + " " + getString(R.string.interval_time_summary_hours) + " " + minutes + " " + getString(R.string.interval_time_summary_minutes);
            }
            findPreference("interval_time").setSummary(summary);

        }

    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password")) {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }

    }
}
