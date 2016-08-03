package com.momenta;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Recieves the alarm broadcast from the system.
 */
public class Receiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Check to see if the alarm is still valid & is within its time limits
        Calendar currentCal = Calendar.getInstance();
        Calendar notifCal = Calendar.getInstance();
        helperPreferences helperPreferences = new helperPreferences(context.getApplicationContext());
        SettingsActivity.NOTIFICATION_TIME START_TIME = SettingsActivity.NOTIFICATION_TIME.START_TIME;
        SettingsActivity.NOTIFICATION_TIME END_TIME = SettingsActivity.NOTIFICATION_TIME.END_TIME;

        //Check to see if current time is earlier than the start time
        String startTimeString = helperPreferences.getPreferences(START_TIME.toString(), "08:30 AM");
        Date tempDate = SettingsActivity.parseTimeString(startTimeString, SettingsActivity.TIME_FORMAT);
        notifCal.setTime(tempDate);

        int cHourOfDay = currentCal.get(Calendar.HOUR_OF_DAY);
        int cMinute = currentCal.get(Calendar.MINUTE);
        int nHourOfDay = notifCal.get(Calendar.HOUR_OF_DAY);
        int nMinute = notifCal.get(Calendar.MINUTE);

        Log.d("Receiver", "Comparing start time, current:" + cHourOfDay + ":" + cMinute
                + " to notif time: " + nHourOfDay + ":" + nMinute);
        if ( (cHourOfDay < nHourOfDay) ||
                ( (cHourOfDay==nHourOfDay) && (cMinute < nMinute) ) ) {
            Log.d("Receiver", "Skipping notification, to early to notify");
            return;
        }

        //Check to see if current time is later than end time
        String endTimeString = helperPreferences.getPreferences(END_TIME.toString(), "08:30 PM");
        tempDate = SettingsActivity.parseTimeString(endTimeString, SettingsActivity.TIME_FORMAT);
        notifCal.setTime(tempDate);

        nHourOfDay = notifCal.get(Calendar.HOUR_OF_DAY);
        nMinute = notifCal.get(Calendar.MINUTE);
        Log.d("Receiver", "Comparing end time, current:" + cHourOfDay + ":" + cMinute
                + " to notif time: " + nHourOfDay + ":" + nMinute);
        if ( (cHourOfDay > nHourOfDay) ||
                ( (cHourOfDay==nHourOfDay) && (cMinute > nMinute) ) ) {
            Log.d("Receiver", "Skipping notification, to late to notify");
            return;
        }

        Log.d("Receiver", "Notifying!!");
        Intent sIntent = new Intent(context, ScreenTakeOverActivity.class);
        sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.getApplicationContext().startActivity(sIntent);
    }


}
