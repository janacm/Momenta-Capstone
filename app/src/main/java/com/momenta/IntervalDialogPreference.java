package com.momenta;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Joe on 2016-03-10.
 * For Momenta-Capstone
 */
public class IntervalDialogPreference extends DialogPreference {

    private EditText hours_tv;
    private EditText mins_tv;
    private helperPreferences sharedPrefs;
    private helperBroadcast broadcast;

    public IntervalDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Activity activity = (Activity) context;
        sharedPrefs = new helperPreferences(activity);
        broadcast = new helperBroadcast(activity);
        setDialogLayoutResource(R.layout.interval_dialog_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setPersistent(true);
    }



    @Override
    protected View onCreateDialogView() {
        return super.onCreateDialogView();
    }



    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        hours_tv = (EditText) view.findViewById(R.id.pref_hour_textview);
        mins_tv = (EditText) view.findViewById(R.id.pref_minute_textview);
        hours_tv.setText(sharedPrefs.GetPreferences(Constants.SHPREF_INTERVAL_HOURS));
        mins_tv.setText(sharedPrefs.GetPreferences(Constants.SHPREF_INTERVAL_MINS));
    }

    @Override
    public void onClick(DialogInterface dialog, int button_id) {
        if (button_id == DialogInterface.BUTTON_POSITIVE) {
            sharedPrefs.SavePreferences(Constants.SHPREF_INTERVAL_HOURS, hours_tv.getText().toString());
            sharedPrefs.SavePreferences(Constants.SHPREF_INTERVAL_MINS, mins_tv.getText().toString());
            broadcast.sendBroadcast();
        } else if (button_id == DialogInterface.BUTTON_NEGATIVE) {
            // do your stuff to handle negative button
        }
    }


}

