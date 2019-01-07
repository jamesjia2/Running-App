package com.gmail.james_jia_myruns2;

/**
 * Created by James on 2/22/2017.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {

            Log.d("jj", "IntentService onHandleIntent() message received");

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                //get id from the received message
                long id = Long.parseLong(extras.getString("message"));

                //delete entry at that id from the database
                Database data = new Database(this);
                data.deleteIndexedEntry(id);

                //send a broadcast that historyfragment will receive - it will then update
                Intent gcmBroadcast = new Intent();
                gcmBroadcast.setAction("GCM_UPDATE");
                this.sendBroadcast(gcmBroadcast);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
