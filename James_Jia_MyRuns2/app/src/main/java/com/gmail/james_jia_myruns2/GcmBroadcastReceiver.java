package com.gmail.james_jia_myruns2;

/**
 * Created by James on 2/22/2017.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("jj", "BroadcastReceiver");

        //specify that gcm intentservice will handle the intent
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());

        //initiate service, keep device awake during startup
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
