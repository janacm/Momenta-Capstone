package com.momenta_app;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Joe on 2016-03-10.
 * For Momenta-Capstone
 */
public class IntervalDialogPreference extends DialogPreference {

    private EditText hours_tv;
    private EditText mins_tv;
    private HelperPreferences sharedPrefs;
    private HelperBroadcast broadcast;

    public IntervalDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedPrefs = new HelperPreferences(context);
        broadcast = new HelperBroadcast(context);
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
        hours_tv.setText(sharedPrefs.getPreferences(Constants.SHPREF_INTERVAL_HOURS, "0"));
        mins_tv.setText(sharedPrefs.getPreferences(Constants.SHPREF_INTERVAL_MINS, "0"));
    }

    @Override
    public void onClick(DialogInterface dialog, int button_id) {
        if (button_id == DialogInterface.BUTTON_POSITIVE) {
            String hours = hours_tv.getText().toString();
            String mins = mins_tv.getText().toString();
            if (hours.equals("") || hours.equals(null)) {
                hours = "0";
            }
            if (mins.equals("") || mins.equals(null)) {
                mins = "0";
            }

            sharedPrefs.savePreferences(Constants.SHPREF_INTERVAL_HOURS, hours);
            sharedPrefs.savePreferences(Constants.SHPREF_INTERVAL_MINS, mins);
            broadcast.sendBroadcast();
        } else if (button_id == DialogInterface.BUTTON_NEGATIVE) {
            // do your stuff to handle negative button
        }
    }


}

