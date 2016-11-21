package com.momenta_app;


import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Joe on 2016-02-09.
 * For Tip Calculator
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Constants
    private static final String PREFS_NAME = "momenta_prefs";
    public static final String AM_TIME_FORMAT = "hh:mm a";
    public static final String TWENTY_FOUR_HOUR_FORMAT = "HH:mm";
    private static final int P_REQUEST = 900;

    //Enumeration for referencing start and end time.
    public enum NOTIFICATION_TIME{START_TIME, END_TIME};


    SimpleDateFormat simpleDateFormat;
    helperPreferences helperPreferences;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        this.getSharedPreferences(PREFS_NAME, 0).registerOnSharedPreferenceChangeListener(this);
        helperPreferences = new helperPreferences(this);
        simpleDateFormat = new SimpleDateFormat(AM_TIME_FORMAT);

        PreferenceManager.setDefaultValues(this, R.xml.settings,
                false);
        initSummary(getPreferenceScreen());

        Preference versionPreference = findPreference("version_name");
         if ( versionPreference != null ) {
             versionPreference.setSummary(BuildConfig.VERSION_NAME);
         }

        Preference notificationStartTime = findPreference("notification_start_time");
        if ( notificationStartTime != null ) {
            notificationStartTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimePickerDialog(NOTIFICATION_TIME.START_TIME);
                    return true;
                }
            });
            notificationStartTime.setSummary( helperPreferences.getPreferences(NOTIFICATION_TIME.START_TIME.toString(), "08:30 AM") );
        }

        Preference notificationEndTime = findPreference("notification_end_time");
        if ( notificationEndTime != null ) {
            notificationEndTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimePickerDialog(NOTIFICATION_TIME.END_TIME);
                    return true;
                }
            });
            notificationEndTime.setSummary( helperPreferences.getPreferences(NOTIFICATION_TIME.END_TIME.toString(), "08:30 PM") );
        }

        SwitchPreference calIntegration = (SwitchPreference) findPreference("integration_switch");
        if ( calIntegration != null ) {
            calIntegration.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isEnabled = (boolean)newValue;
                    if (isEnabled) {
                        askPermissions(P_REQUEST, android.Manifest.permission.GET_ACCOUNTS, android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR);
                    }
                    return true;
                }
            });
            calIntegration.setChecked( havePermissions(android.Manifest.permission.GET_ACCOUNTS, android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR) );
        }
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
                helperBroadcast helperBroadcast = new helperBroadcast(this);
                helperBroadcast.cancelAlarm();
                Log.d("SettingsActivity", "Cancelling alarm");
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

    /**
     * Prompts the user for permissions.
     * @param callbackId the unique callbackId
     * @param permissionsId the permissions to request from the user
     */
    private void askPermissions(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions) {
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
        }
    }

    /**
     * Check if all the permissions necessary for Google Calendar have been granted
     * @return True if the all permissions are granted, False Otherwise
     */
    private boolean havePermissions(String... permissionsId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean result = prefs.getBoolean("integration_switch", false);
        for (String p : permissionsId) {
            result = result && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }
        return result;
    }

    /**
     * Helper method to show the time picker dialog
     * @param TIME the start time or end time
     *
     */
    private void showTimePickerDialog(final NOTIFICATION_TIME TIME) {
        //Get the previous time from preferences
        String time = "";
        Calendar cal = Calendar.getInstance();
        if (TIME == NOTIFICATION_TIME.START_TIME) {
            time = helperPreferences.getPreferences(TIME.toString(), "08:30 AM");
        } else {
            time = helperPreferences.getPreferences(TIME.toString(), "08:30 PM");
        }

        try {
            Date date = simpleDateFormat.parse(time);
            cal.setTime(date);
        } catch (ParseException e) {
            Log.e("SettingsActivity", "Error parsing date time from preferences");
        }

        //Build time picker dialog with the time value from preferences.
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Save time in preferences.
                String timeSet = hourOfDay + ":" + minute;
                Date timeSetDate = parseStringToDate(timeSet, TWENTY_FOUR_HOUR_FORMAT);
                helperPreferences.savePreferences(TIME.toString(), simpleDateFormat.format(timeSetDate));
                if (TIME == NOTIFICATION_TIME.START_TIME) {
                    Preference notificationStartTime = findPreference("notification_start_time");
                    notificationStartTime.setSummary( helperPreferences.getPreferences(NOTIFICATION_TIME.START_TIME.toString(), "08:30 AM") );
                } else {
                    Preference notificationEndTime = findPreference("notification_end_time");
                    notificationEndTime.setSummary( helperPreferences.getPreferences(NOTIFICATION_TIME.END_TIME.toString(), "08:30 PM") );
                }
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    /**
     * Convenience method to format a Date object into a String
     * @param date the date object to be formatted
     * @param format the desired format e.g yyyy-MM-dd
     * @return the formatted String
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Convenience method to parse a string into a Date object
     * @param date the string to be parse
     * @param format the format of the string to be parsed e.g yyyy-MM-dd
     * @return Equivalent date object of the string, null if there was a parsing error
     */
    public static Date parseStringToDate(String date, String format) {
        Date result;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            result = sdf.parse(date);
        } catch (ParseException e) {
            result = null;
            Log.e("StatsFragment", Log.getStackTraceString(e));
        }
        return result;
    }

}