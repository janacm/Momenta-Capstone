package com.momenta;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Recieves the alarm broadcast from the system.
 */
public class Reciever extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Reciever", "Alarm Set.");
        Intent sIntent = new Intent(context, ScreenTakeOverActivity.class);
        sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(sIntent);
    }
}
