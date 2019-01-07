package com.gmail.james_jia_myruns2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import java.util.concurrent.ArrayBlockingQueue;
import meapsoft.FFT;

public class LocationService extends Service implements LocationListener, SensorEventListener {

    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    private static ArrayBlockingQueue<Double> queue = new ArrayBlockingQueue<Double>(2048);
    public ArrayList<Location> locationList = MapActivity.locationList;

    Intent classificationIntent;
    Context context = this;
    SensorManager sensorManager;
    Sensor accelerometer;
    private ActivityClassificationTask acTask;

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

        Log.d(tag, "input type is " + StartFragment.inputType);

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

        Log.d(tag, "onStartCommand()");

        //start requesting location updates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
        showNotification();


        //register sensor listener
        if (StartFragment.inputType == 2) {
            Log.d(tag, "input type is " + StartFragment.inputType);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
            acTask = new ActivityClassificationTask(getApplicationContext());

            //execute the classification task
            acTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
            Log.d(tag, "execute");
        }

        return START_STICKY;

    }

    //kill notification, stop timer
    public void onDestroy() {

        Log.d(tag, "service onDestroy()");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        timer.cancel();

        //stop the classification task
        if(StartFragment.inputType==2) {
            sensorManager.unregisterListener(this);
            acTask.cancel(true);
        }



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

    //async task that classifies current activity type
    private class ActivityClassificationTask extends AsyncTask<Void, Void, Void> {

        public ActivityClassificationTask (Context context){
            LocationService.this.context = context;
        }

        //classify using sensor data in the background - stop when cancel is called
        @Override
        protected Void doInBackground(Void... arg0) {

            //feature vector
            ArrayList<Double> featureVector = new ArrayList<Double>(64);
            int blockSize = 0;
            FFT fft = new FFT(64);
            double[] accBlock = new double[64];
            double[] real = accBlock;
            double[] imaginary = new double[64];

            //initialize as very small, will get updated
            double max = Double.MIN_VALUE;

            //only loop until the activity is canceled
            while (!isCancelled()) {

                try {
                    //add new block from queue
                    accBlock[blockSize++] = queue.take().doubleValue();

                    if (blockSize >= 64) {
                        blockSize = 0;

                        //temp holder - will be updated with max value
                        max = .0;

                        //loop through all values to find the biggest
                        for (double value : accBlock) {
                            if (max < value) {
                                max = value;
                            }
                        }

                        //transform
                        fft.fft(real, imaginary);

                        //get magnitude of feature vector
                        for (int i = 0; i < real.length; i++) {
                            double magnitude = Math.sqrt(Math.pow(real[i],2)+Math.pow(imaginary[i],2));
                            featureVector.add(magnitude);
                            imaginary[i] = .0;
                        }

                        //feed into classifier for testing
                        int activityClass = (int) WekaClassifier.classify(featureVector.toArray());
                        Log.d(tag,"Classified value "+activityClass);

                        //send broadcast with an int indicating the activity classification
                        classificationIntent = new Intent();
                        classificationIntent.setAction("ACTION_UPDATED");
                        classificationIntent.putExtra("Classification", activityClass);
                        context.sendBroadcast(classificationIntent);
                        featureVector.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }


    //take data from sensor
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            // distance formula to calculate magnitude
            double m = Math.sqrt(Math.pow(event.values[0],2) + Math.pow(event.values[1],2)
                        + Math.pow(event.values[2],2));

            //add to queue
            try {
                queue.add(new Double(m));
            } catch (IllegalStateException e) {

                //capacity reached
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(queue.size()*2);
                queue.drainTo(newBuf);
                queue = newBuf;
                queue.add(new Double(m));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //handle incoming client messages
    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(tag, "handleMessage()");
        }
    }
}
