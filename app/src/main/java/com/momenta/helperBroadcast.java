package com.momenta;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Joe on 2016-03-12.
 * For Momenta-Capstone
 */
public class helperBroadcast {

    private Activity activity;
    helperPreferences sharedPrefs;

    public helperBroadcast(Activity activity) {
        this.activity = activity;
        sharedPrefs = new helperPreferences(activity);
    }

    public void sendBroadcast() {
        Log.d("Dashboard", "Setting Alarm.");
        String minutes = sharedPrefs.getPreferences(Constants.SHPREF_INTERVAL_MINS, "0");
        String hours = sharedPrefs.getPreferences(Constants.SHPREF_INTERVAL_HOURS, "0");
        Log.d("Dashboard", "Setting Alarm..." + hours + minutes);
        Calendar cal = Calendar.getInstance();
        int time = Integer.parseInt(hours) * 60 * 60 * 1000 + Integer.parseInt(minutes) * 60 * 1000;
        if (time != 0) {
            cal.setTimeInMillis(cal.getTimeInMillis() + time);

            Intent intentAlarm = new Intent(activity, Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            Log.d("Dashboard", "Alarm Set to ring in " + String.valueOf(time) + "milliseconds");
        }
    }
}
