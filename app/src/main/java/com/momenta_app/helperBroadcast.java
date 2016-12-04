package com.momenta_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joe on 2016-03-12.
 * For Momenta-Capstone
 */
public class HelperBroadcast {
    private static final String TAG = "HelperBroadcast";
    enum SCHEDULE_TIME{CANCEL, NOW, START_TIME, TOMORROW}

    private Context context;
    private HelperPreferences sharedPrefs;
    private PendingIntent pendingIntent;
    HelperPreferences helperPreferences;


    public HelperBroadcast(Context context) {
        this.context = context;
        sharedPrefs = new HelperPreferences(context);
        Intent intentAlarm = new Intent(context, Receiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        helperPreferences = new HelperPreferences(context);
    }

    public void sendBroadcast() {
        Log.d(TAG, "Setting Alarm.");
        String minutes = sharedPrefs.getPreferences(Constants.SHPREF_INTERVAL_MINS, "0");
        String hours = sharedPrefs.getPreferences(Constants.SHPREF_INTERVAL_HOURS, "0");

        Log.d(TAG, "Setting Alarm..." + hours + minutes);
        Calendar currentCal = Calendar.getInstance();
        int time = Integer.parseInt(hours) * 60 * 60 * 1000 + Integer.parseInt(minutes) * 60 * 1000;
        if (time != 0) {
            SCHEDULE_TIME scheduleTime = scheduleTime();
            Calendar calendars[] = getAlarmStartEndTime();
            Calendar alarmStartCal = calendars[0];

            switch (scheduleTime) {
                case NOW:
                    Log.d(TAG, "Setting alarm right now: " + currentCal.getTimeInMillis());
                    currentCal.setTimeInMillis( currentCal.getTimeInMillis() + time );
                    break;
                case START_TIME:
                    Log.d(TAG, "Setting alarm at start time: " + currentCal.getTimeInMillis());
                    currentCal.setTimeInMillis( alarmStartCal.getTimeInMillis() + time );
                    break;
                case TOMORROW:
                    Log.d(TAG, "Setting alarm tomorrow: " + currentCal.getTimeInMillis());
                    currentCal.setTimeInMillis( alarmStartCal.getTimeInMillis()
                            + TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS)
                            + time );
                    break;
                default:
                    Log.d(TAG, "Invalid case");
                    return;
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, currentCal.getTimeInMillis(),
                    time, pendingIntent);
        }
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Finds when the next alarm should be scheduled for
     * @return SCHEDULE_TIME for the next alarm.
     *          NOW: the alarm should be scheduled right away
     *          START_TIME: the alarm should be scheduled at the start time today
     *          TOMORROW: the alarm should be scheduled at the start time tomorrow
     */
    SCHEDULE_TIME scheduleTime() {
        SCHEDULE_TIME result;
        SharedPreferences sharedPrefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        String hours = sharedPrefs.getString(Constants.SHPREF_INTERVAL_HOURS, "0");
        String minutes = sharedPrefs.getString(Constants.SHPREF_INTERVAL_MINS, "0");
        if ((hours.equals("0") || hours.equals("00")) && (minutes.equals("0") || minutes.equals("00"))) {
            result = SCHEDULE_TIME.CANCEL;
            return result;
        }

        Calendar calendars[] = getAlarmStartEndTime();
        Calendar alarmStartCal = calendars[0];
        Calendar alarmEndCal = calendars[1];
        Calendar currentCal = Calendar.getInstance();

        if ( currentCal.getTimeInMillis() >= alarmStartCal.getTimeInMillis() ) {

            if ( currentCal.getTimeInMillis() >= alarmEndCal.getTimeInMillis() ) {
                // If (currentTime > startTime) & (currentTime > endTime)
                // the alarm should be scheduled for tomorrow
                result = SCHEDULE_TIME.TOMORROW;
            } else {
                // If (currentTime > startTime) & !(currentTime > endTime)
                // the alarm should be scheduled for right now + interval_time
                result = SCHEDULE_TIME.NOW;
            }
        } else {
            // Current time is less the alarm start time
            // schedule alarm for start time + interval time (same day)
            result = SCHEDULE_TIME.START_TIME;
        }
        return result;
    }

    /**
     * Gets the start and end time of the notification alarms.
     * @return An array with two calendar.
     *          result[0]: Start time Calendar
     *          result[1]: End time Calendar
     */
    private Calendar[] getAlarmStartEndTime() {
        SettingsActivity.NOTIFICATION_TIME START_TIME = SettingsActivity.NOTIFICATION_TIME.START_TIME;
        String startTimeString = helperPreferences.getPreferences(START_TIME.toString(), "08:30 AM");
        SettingsActivity.NOTIFICATION_TIME END_TIME = SettingsActivity.NOTIFICATION_TIME.END_TIME;
        String endTimeString = helperPreferences.getPreferences(END_TIME.toString(), "08:30 PM");

        // The calendars to represent the start and end times.
        Calendar alarmStartCal = Calendar.getInstance();
        Calendar alarmEndCal = Calendar.getInstance();

        // Temp calendar reused to store hour of day.
        Calendar tempCal = Calendar.getInstance();

        // Set the time of day for the start cal.
        tempCal.setTime(SettingsActivity.parseStringToDate(startTimeString, SettingsActivity.AM_TIME_FORMAT));
        alarmStartCal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
        alarmStartCal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));

        // Set the time of day for the end cal.
        tempCal.setTime(SettingsActivity.parseStringToDate(endTimeString, SettingsActivity.AM_TIME_FORMAT));
        alarmEndCal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
        alarmEndCal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));

        return new Calendar[]{alarmStartCal, alarmEndCal};
    }
}
