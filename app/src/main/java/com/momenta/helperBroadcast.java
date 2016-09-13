package com.momenta;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joe on 2016-03-12.
 * For Momenta-Capstone
 */
public class helperBroadcast {

    private Context context;
    helperPreferences sharedPrefs;
    PendingIntent pendingIntent;
    helperPreferences helperPreferences;


    public helperBroadcast(Context context) {
        this.context = context;
        sharedPrefs = new helperPreferences(context);
        Intent intentAlarm = new Intent(context, Receiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        helperPreferences = new helperPreferences(context);
    }

    public void sendBroadcast() {
        Log.d("Dashboard", "Setting Alarm.");
        String minutes = sharedPrefs.getPreferences(Constants.SHPREF_INTERVAL_MINS, "0");
        String hours = sharedPrefs.getPreferences(Constants.SHPREF_INTERVAL_HOURS, "0");

        Log.d("Dashboard", "Setting Alarm..." + hours + minutes);
        Calendar currentCal = Calendar.getInstance();
        int time = Integer.parseInt(hours) * 60 * 60 * 1000 + Integer.parseInt(minutes) * 60 * 1000;
        if (time != 0) {
            //Get start time from preference
            SettingsActivity.NOTIFICATION_TIME START_TIME = SettingsActivity.NOTIFICATION_TIME.START_TIME;
            String startTimeString = helperPreferences.getPreferences(START_TIME.toString(), "08:30 AM");

            //Get end time from preference
            SettingsActivity.NOTIFICATION_TIME END_TIME = SettingsActivity.NOTIFICATION_TIME.END_TIME;
            String endTimeString = helperPreferences.getPreferences(END_TIME.toString(), "08:30 PM");

            //Calculate time to set alarm in milliseconds
            // START_TIME - CURRENT_TIME + INTERVAL_TIME
            Calendar alarmStartCal = Calendar.getInstance(); //Variable used to hold the alarm start time
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(SettingsActivity.parseStringToDate(startTimeString, SettingsActivity.AM_TIME_FORMAT));
            alarmStartCal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
            alarmStartCal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));

            Calendar alarmEndCal = Calendar.getInstance();
            tempCal.setTime(SettingsActivity.parseStringToDate(endTimeString, SettingsActivity.AM_TIME_FORMAT));
            alarmEndCal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
            alarmEndCal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));

            Calendar triggerCal = Calendar.getInstance();
            //If current time is greater than start time, then start the alarm tomorrow
            if ( currentCal.getTimeInMillis() > alarmStartCal.getTimeInMillis() ) {

                if ( currentCal.getTimeInMillis() >= alarmEndCal.getTimeInMillis() ) {
                    //current time is greater than start and end time
                    //schedule alarm for start time tomorrow + interval time
                    triggerCal.setTimeInMillis( alarmStartCal.getTimeInMillis()
                            + TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS)
                            + time );
                    Log.d("helperBroadcast", "Setting alarm tomorrow: " + triggerCal.getTimeInMillis());
                } else if ( currentCal.getTimeInMillis() <= alarmEndCal.getTimeInMillis() ) {
                    //current time is greater than start time, but less than end time
                    //schedule alarm for current time + interval time
                    triggerCal.setTimeInMillis( triggerCal.getTimeInMillis() + time );
                    Log.d("helperBroadcast", "Setting alarm right now: " + triggerCal.getTimeInMillis());
                }
            } else if ( currentCal.getTimeInMillis() <= alarmStartCal.getTimeInMillis() ) {
                //current time is less than or equal to the alarm start time
                //schedule alarm for start time + interval time (same day)
                triggerCal.setTimeInMillis( alarmStartCal.getTimeInMillis() + time );
                Log.d("helperBroadcast", "Setting alarm at start time: " + triggerCal.getTimeInMillis());
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerCal.getTimeInMillis(),
                    time, pendingIntent);
        }
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }
}
