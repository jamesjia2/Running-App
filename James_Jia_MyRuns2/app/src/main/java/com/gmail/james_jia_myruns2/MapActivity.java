package com.gmail.james_jia_myruns2;

import android.app.NotificationManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, ServiceConnection {

    public static Database data;
    Calendar calendar = Calendar.getInstance();

    private GoogleMap mMap;
    public static Long id;
    public static ArrayList<Location> locationList = new ArrayList<Location>();
    public static ArrayList<LatLng> latLngList = new ArrayList<LatLng>();
    public ExerciseEntry exercise;
    public Boolean bound = false;
    public Boolean newExercise;
    public int caloriesToMile = 150;
    public static int timeElapsed=0;
    Marker marker;

    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());
    private ServiceConnection connection = this;
    Intent serviceIntent;
    BroadcastReceiver locationReceiver;
    MotionUpdateReceiver classificationReceiver;
    int autoActivityType = 0;
    int[] activityCounter = {0,0,0};

    String tag = "jj";
    String locationListTag = "location";
    String latLngListTag = "latlng";
    String activityCounterTag = "counter";
    String exerciseTag = "exercise";
    String timeTag = "time";
    public String historyTag = "history";
    public String idTag = "id";
    String[]activities = {"Running","Walking","Standing","Cycling","Hiking","Downhill Skiing",
            "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
            "Mountain Biking", "Wheelchair", "Elliptical", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkPermissions();

        //initialize database reference and ExerciseEntry
        data = new Database(this);
        exercise = new ExerciseEntry();
        setDefaultExercise(exercise);

        //newExercise is a boolean that says whether the activity is in new run or history mode
        int history = getIntent().getIntExtra(historyTag,0);
        if (history == 1){
            newExercise = false;
        }
        else if (history == 0){
            newExercise = true;
        }

        //get the mapfragment, get map when it's done loading
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //if currently on a new run, start tracking with gps
        if(newExercise) {
            //check if there's a saved state
            if (savedInstanceState != null) {

                //load saved location list
                if (savedInstanceState.getParcelable(locationListTag) != null) {
                    locationList = savedInstanceState.getParcelable(locationListTag);
                }
                //load saved latlng list
                if (savedInstanceState.getParcelable(locationListTag) != null) {
                    latLngList = savedInstanceState.getParcelable(latLngListTag);
                }
                //load saved entry
                if (savedInstanceState.getSerializable(exerciseTag) != null) {
                    exercise = (ExerciseEntry) savedInstanceState.getSerializable(exerciseTag);
                }
                //load saved location list
                if (savedInstanceState.getParcelable(timeTag) != null) {
                    timeElapsed = savedInstanceState.getParcelable(timeTag);
                }
                //load activity counter list
                if (savedInstanceState.getIntArray(activityCounterTag) != null) {
                    activityCounter = savedInstanceState.getIntArray(activityCounterTag);
                    //load current activity
                    autoActivityType=0;
                    int max = -1;
                    for (int i = 0; i < activityCounter.length; i++) {
                        if (activityCounter[i] > max) {
                            max = activityCounter[i];
                            autoActivityType=i;
                        }
                    }
                }

            }

            //set up location receiver
            locationReceiver = new locationUpdateReceiver();
            IntentFilter locationFilter = new IntentFilter();
            locationFilter.addAction("LOCATION_CHANGED");
            registerReceiver(locationReceiver, locationFilter);


            //set up classification receiver
            if (StartFragment.inputType == 2) {
                classificationReceiver = new MotionUpdateReceiver();
                IntentFilter classificationFilter = new IntentFilter();
                classificationFilter.addAction("ACTION_UPDATED");
                registerReceiver(classificationReceiver, classificationFilter);
            }

            //start service and bind it
            if (newExercise) {
                //start
                serviceIntent = new Intent(this, LocationService.class);
                startService(serviceIntent);
                //bind
                if (!bound) {
                    doBindService(serviceIntent);
                }
            }
        }

    }

    //binding
    private void doBindService(Intent intent) {
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        bound = true;
    }

    //unbinding
    private void doUnbindService() {
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    //when done binding
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(tag, "onServiceConnected()");
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(tag, "onServiceDisconnected()");
    }

    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(tag,"MapActivity IncomingMessageHandler()");
        }
    }

    //auto update broadcast receiver
    public class MotionUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //get most recent classification, and increment counter
            int currentActivity = intent.getIntExtra("Classification", 0);
            activityCounter[currentActivity]++;

            //loop through counters to find which one has the most
            int max = -1;
            for (int i = 0; i < activityCounter.length; i++) {
                if (activityCounter[i] > max) {
                    max = activityCounter[i];
                    autoActivityType=i;
                }
            }

            //set as the one with the most
            exercise.setmActivityType(autoActivityType);
            Log.d(tag, "Received classification is "+ autoActivityType);
        }
    }

    //receive location updates from the location service and update the current path and markers
    public class locationUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(tag, "location list size is " + locationList.size());

            synchronized (locationList) {

                //update latLng ArrayList
                updateLatLngList();

                //camera follows user to last known location
                LatLng lastKnown = latLngList.get(latLngList.size()-1);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnown));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnown, 17));

                //STATS TRACKING
                Log.d(tag, "time elapsed in seconds "+timeElapsed);

                //float currentDistance = getCurrentDistance();
                float totalDistance = toMiles(getTotalDistance());
                float progressDistance = totalDistance - exercise.getmDistance();
                float totalClimb = toMiles(getTotalClimb());
                int calories = (int)totalDistance*caloriesToMile;

                //speed calculations
                float averageSpeed = 0;
                float currentSpeed = 0;
                if(totalDistance>0){
                    //calculate elapsed time in hours and get previous total time
                    float totalTime = ((float)(timeElapsed)/3600);
                    float previousTime = ((float)exercise.getmDuration())/3600;
                    float differenceTime = totalTime - previousTime;

                    //calculate overall average speed and current speed
                    if(totalTime>0) {
                        averageSpeed = (float) (totalDistance / totalTime);
                    }
                    if(differenceTime>0) {
                        currentSpeed = (float) (progressDistance / differenceTime);
                    }

                    //set in exercise entry
                    exercise.setmAvgSpeed(averageSpeed);
                }

                //fill out exercise data in the ExerciseEntry
                exercise.setmDistance(totalDistance);
                exercise.setmClimb(totalClimb);
                exercise.setmDuration(timeElapsed);

                //populate the textviews
                TextView inputText = ((TextView)findViewById(R.id.inputText));
                if(StartFragment.inputType == 1) {
                    inputText.setText("Type: " + activities[StartFragment.activityType]);
                    exercise.setmActivityType(StartFragment.activityType);
                }
                if(StartFragment.inputType == 2) {
                    inputText.setText("Type: "+getAutoActivityString(autoActivityType));
                    exercise.setmActivityType(autoActivityType);
                }

                TextView avgSpeedText = ((TextView)findViewById(R.id.avgSpeedText));
                avgSpeedText.setText("Avg speed: "+formatFloat(averageSpeed)+" m/h");

                TextView currSpeedText = ((TextView)findViewById(R.id.currSpeedText));
                currSpeedText.setText("Cur speed: "+formatFloat(currentSpeed)+ " m/h");

                TextView climbText = ((TextView)findViewById(R.id.climbText));
                climbText.setText("Climb: "+formatFloat(totalClimb)+" Miles");

                TextView calorieText = ((TextView)findViewById(R.id.calorieText));
                calorieText.setText("Calorie: "+calories);

                TextView distanceText = ((TextView)findViewById(R.id.distanceText));
                distanceText.setText("Distance: "+formatFloat(totalDistance)+" Miles");


                //DRAWING
                int latLngLast = latLngList.size()-1;

                //draw blue start marker if first time
                if(latLngList.get(0)!=null) {
                    MarkerOptions markerOptions = new MarkerOptions().
                            position(latLngList.get(0))
                            .title("Start").icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    mMap.addMarker(markerOptions);
                }

                //if not first time, delete previous marker
                if(latLngList.size()>1){
                    //dont remove current marker until there are more than 2 points on the line
                    if (latLngList.size()>2&&marker!=null) {
                        marker.remove();
                    }
                    //draw marker at current location
                    MarkerOptions markerOptions = new MarkerOptions().
                            position(latLngList.get(latLngLast))
                            .title("Current Location");

                    marker = mMap.addMarker(markerOptions);
                }

                //set up polyline options
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(4);

                //draw polyline
                if(latLngList.size()>=2) {
                    polylineOptions.addAll(latLngList);
                    mMap.addPolyline(polylineOptions);
                }
            }
        }
    }

    //get total distance traveled this session
    public float getTotalDistance(){
        float totalDistance = 0;
        float marginalDistance = 0;
        for(int i = 0; i<locationList.size()-1; i++){
            Location firstLocation = locationList.get(i);
            Location secondLocation = locationList.get(i+1);
            marginalDistance = firstLocation.distanceTo(secondLocation);
            totalDistance += marginalDistance;
        }
        return totalDistance;
    }

    //get total climb this session
    public float getTotalClimb(){
        float totalClimb = 0;
        float marginalClimb = 0;
        for(int i = 0; i<locationList.size()-1; i++){
            Location firstLocation = locationList.get(i);
            Location secondLocation = locationList.get(i+1);
            marginalClimb = (float) (firstLocation.getAltitude()-secondLocation.getAltitude());
            totalClimb += marginalClimb;
        }
        return totalClimb;
    }

    public float toMiles(float meter){
        return (float)((meter/1000)*0.621371);
    }

    public String formatFloat(float f){
        return String.format("%.2f", f);
    }

    //update the LatLngList for new entries in the location list
    public void updateLatLngList(){
        ArrayList<LatLng> newList = new ArrayList<LatLng>();
        int size = locationList.size();
        //make a temporary latlng list that has all the necessary values
        for(int i = 0; i<size; i++){
            newList.add(convertLocationToLatLng(locationList.get(i)));
        }
        //update the latLngList instance variable
        latLngList = newList;
    }

    //converts location objects to latlng
    public LatLng convertLocationToLatLng(Location loc){
        LatLng newLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        return newLatLng;
    }

    //unregister receiver to stop getting broadcast
    @Override
    protected void onPause() {
        if (newExercise) {
            unregisterReceiver(locationReceiver);

            //unregister classification receiver
            if (StartFragment.inputType==2){
                try {
                    unregisterReceiver(classificationReceiver);
                }catch(IllegalArgumentException e){
                    Log.d(tag, "couldn't unregister classification receiver");
                }
            }
        }

        super.onPause();
    }

    //register receiver to continue getting broadcast
    @Override
    protected void onResume() {
        if (newExercise) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("LOCATION_CHANGED");
            registerReceiver(locationReceiver, intentFilter);
        }
        if (StartFragment.inputType==2){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("ACTION_UPDATED");
            registerReceiver(classificationReceiver, intentFilter);
        }
        super.onResume();
    }

    //unbind, stop service, kill notification
    @Override
    public void onBackPressed() {
        //don't want to save the locations, so clean out the saved locations manually
        timeElapsed=0;
        locationList.clear();
        latLngList.clear();
        if (newExercise) {
                try {
                    doUnbindService();
                    stopService(serviceIntent);
                } catch (Throwable t) {
                    Log.e(tag, "could not unbind");
                }
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancelAll();
        }
        super.onBackPressed();
    }

    //unbind, stop service, kill notification
    public void onDestroy(){
        if (newExercise) {
            try {
                doUnbindService();
                stopService(serviceIntent);
            } catch (Throwable t) {
                Log.e(tag, "unbind fail");
            }
        }
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
        super.onDestroy();
    }


    //saves the important objects
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(locationListTag, locationList);
        outState.putParcelableArrayList(latLngListTag, latLngList);
        outState.putIntArray(activityCounterTag, activityCounter);

        //the location list is not serializable, should only be used when writing/reading
        if(exercise.getmLocationList()==null) {
            outState.putSerializable(exerciseTag, exercise);
        }
        outState.putInt(timeTag, timeElapsed);
    }

    //called when map is done loading - also handles when MapActivity is called from History
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //start at NewHamp and move camera
        mMap = googleMap;
        LatLng newHamp = new LatLng(43.7019294, -72.28667597);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newHamp));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newHamp, 17));

        //draw history
        if(!newExercise){

            //hide save and cancel buttons
            Button save = (Button)findViewById(R.id.saveButton);
            Button cancel = (Button)findViewById(R.id.cancelButton);
            save.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);

            //load the database entry in the background
            id = getIntent().getLongExtra(idTag, 0);
            exercise = new ReadDatabase(this).loadInBackground();

            //need at least one location entry to bother with drawing anything
            if(exercise.getmLocationList().size()>1) {

                //bring camera to start of exercise
                mMap.moveCamera(CameraUpdateFactory.newLatLng(exercise.getmLocationList().get(0)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(exercise.getmLocationList().get(0), 17));

                //TRACE STUFF
                //draw blue start marker if first time
                if (exercise.getmLocationList().get(0) != null) {
                    MarkerOptions markerOptions = new MarkerOptions().
                            position(exercise.getmLocationList().get(0))
                            .title("Start").icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    mMap.addMarker(markerOptions);
                }

                //draw red marker at end of run
                int last = exercise.getmLocationList().size() - 1;
                if (exercise.getmLocationList().size() > 1) {
                    MarkerOptions markerOptions = new MarkerOptions().
                            position(exercise.getmLocationList().get(last))
                            .title("End").icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(markerOptions);
                }

                //set up polyline options
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(4);

                //draw polyline
                if (exercise.getmLocationList().size() >= 2) {
                    polylineOptions.addAll(exercise.getmLocationList());
                    if (mMap != null) {
                        mMap.addPolyline(polylineOptions);
                    }
                }

                //TEXTVIEW STATS STUFF - populate textview values
                //input type
                TextView inputText = ((TextView) findViewById(R.id.inputText));
                if (exercise.getmInputType() == 1) {
                    inputText.setText("Type: " + activities[exercise.getmActivityType()]);
                }
                if (exercise.getmInputType() == 2) {
                    inputText.setText("Type: "+getAutoActivityString(exercise.getmActivityType()));
                }

                float totalDistance = exercise.getmDistance();
                float totalClimb = exercise.getmClimb();
                int calories = exercise.getmCalorie();
                float averageSpeed = exercise.getmAvgSpeed();

                TextView avgSpeedText = ((TextView) findViewById(R.id.avgSpeedText));
                avgSpeedText.setText("Avg speed: " + formatFloat(averageSpeed) + " m/h");

                TextView currSpeedText = ((TextView) findViewById(R.id.currSpeedText));
                currSpeedText.setText("Cur speed: N/A");

                TextView climbText = ((TextView) findViewById(R.id.climbText));
                climbText.setText("Climb: " + formatFloat(totalClimb) + " Miles");

                TextView calorieText = ((TextView) findViewById(R.id.calorieText));
                calorieText.setText("Calorie: " + calories);

                TextView distanceText = ((TextView) findViewById(R.id.distanceText));
                distanceText.setText("Distance: " + formatFloat(totalDistance) + " Miles");
            }

        }
    }

    //exit
    public void onCancelClicked(View v){
        //don't want to save the locations, so clean out the saved locations manually
        timeElapsed=0;
        locationList.clear();
        latLngList.clear();
        finish();
    }

    //when save is clicked, write to database and exit out of activity
    public void onSaveClicked(View view){
        //write to database
        ArrayList<LatLng> tempList = new ArrayList<LatLng>();
        for (int i=0; i<latLngList.size();i++){
            tempList.add(latLngList.get(i));
        }
        exercise.setmLocationList(tempList);

        //don't bother writing if it didn't get a single location
        if(tempList.size()>1) {
            new WriteDatabase().execute();
        }
        //clear data
        timeElapsed=0;
        locationList.clear();
        latLngList.clear();
        finish();
    }

    //use AsyncTask to handle writing in background thread
    private class WriteDatabase extends AsyncTask<Void, Void, Integer> {
        //write to database in background
        @Override
        protected Integer doInBackground(Void... params) {
            long id = data.insertEntry(exercise);
            return 0;
        }
        //update the historyfragment listview on the main thread when writing is complete
        @Override
        protected void onPostExecute(Integer result) {
            HistoryFragment.adapter.notifyDataSetChanged();
        }
    }

    //read from database with async task
    public static class ReadDatabase extends AsyncTaskLoader<ExerciseEntry> {

        public ReadDatabase(Context context){
            super(context);
        }

        @Override
        public ExerciseEntry loadInBackground() {
            ExerciseEntry entry = data.fetchIndexedEntry(id);
            return entry;
        }
    }

    //delete the current exercise entry when delete is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new DeleteExercise().execute();
        finish();
        return true;
    }

    //asynctask for deleting from database
    private class DeleteExercise extends AsyncTask<ExerciseEntry, Integer, String> {

        @Override
        protected String doInBackground(ExerciseEntry... exerciseEntries) {
            HistoryFragment.data.open();
            HistoryFragment.data.deleteIndexedEntry(id);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            HistoryFragment.adapter.notifyDataSetChanged();
        }
    }

    //make delete button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //only if in history mode
        if(!newExercise) {
            menu.add(Menu.NONE, 0, 0, "DELETE").
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    //initialize all values for exercise entry
    public void setDefaultExercise(ExerciseEntry ex){
        ex.setmInputType(StartFragment.inputType);
        ex.setmActivityType(StartFragment.activityType);
        ex.setmDateTime(calendar.getTimeInMillis());
        ex.setmDuration(0);
        ex.setmDistance(0);
        ex.setmAvgPace(0);
        ex.setmAvgSpeed(0);
        ex.setmCalorie(0);
        ex.setmClimb(0);
        ex.setmHeartRate(0);
        ex.setmComment("");
    }

    //get the string value of the automatically classified activity
    public String getAutoActivityString(int activityID){
        if(activityID == 0){
            return "Standing";
        }
        if(activityID == 1){
            return "Walking";
        }
        if(activityID == 2){
            return "Running";
        }
        return "Standing";
    }

    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || checkSelfPermission
                (android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }
}
