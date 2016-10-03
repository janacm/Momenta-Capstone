package com.momenta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by joesi on 2016-10-02.
 */

public class BootAlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootBroadcast","BootBroadcast received");
        helperBroadcast helperBroadcast = new helperBroadcast(context);
        helperBroadcast.sendBroadcast();
    }
}
