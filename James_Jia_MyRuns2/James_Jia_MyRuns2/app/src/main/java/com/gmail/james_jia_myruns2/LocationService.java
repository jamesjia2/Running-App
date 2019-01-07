package com.gmail.james_jia_myruns2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements LocationListener {

    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    public ArrayList<Location> locationList = MapActivity.locationList;
    private LocationManager locationManager;
    private NotificationManager notificationManager;
    Timer timer;
    TimerTask timerTask;
    String tag = "jj";

    @Override
    public void onCreate() {
        checkPermission();
        super.onCreate();

        //keeps track of time since service was started
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                MapActivity.timeElapsed++;
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    //after onBind is called
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    //after startService is called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkPermission();

        //start requesting location updates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
        showNotification();
        return START_STICKY;

    }

    //kill notification, stop timer
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        timer.cancel();
        super.onDestroy();
    }

    //notification to go back to activity
    private void showNotification() {

        //Use pending intent
        Intent localIntent = new Intent(this, MapActivity.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent localPendingIntent = PendingIntent.getActivity(this, 0, localIntent, 0);
        Notification localNotification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            localNotification = new Notification.Builder(this).setContentTitle("MyRuns GPS").
                    setContentText("Tap to go back").setSmallIcon(R.drawable.ic_launcher).
                    setContentIntent(localPendingIntent).build();
        }
        //set up notification
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        localNotification.flags = localNotification.flags
                | Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify(0, localNotification);

    }

    //just in case
    public boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    //location updates
    @Override
    public void onLocationChanged(Location location) {
        //append to the locationList in MapActivity, then send broadcast to inform of update
        synchronized (this.locationList) {
            locationList.add(location);

            Intent locationUpdateBroadcast = new Intent();
            locationUpdateBroadcast.setAction("LOCATION_CHANGED");
            this.sendBroadcast(locationUpdateBroadcast);
            return;
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }

    //handle incoming client messages
    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(tag, "handleMessage()");
        }
    }
}
