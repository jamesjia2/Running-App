package com.gmail.james_jia_myruns2;

/**
 * Created by James on 1/15/2017.
 */

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.AsyncTaskLoader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class HistoryFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<List<ExerciseEntry>>{

    public GcmReceiver gcmReceiver;

    public static List<ExerciseEntry> exercises = new ArrayList<ExerciseEntry>();
    public static Database data;
    public static CustomAdapter adapter;

    public String inputTag = "input";
    public String activityTag = "activity";
    public String dateTimeTag = "dateTime";
    public String durationTag = "duration";
    public String distanceTag = "distance";
    public String calorieTag = "calorie";
    public String heartRateTag = "heartRate";
    public String idTag = "id";
    public String historyTag = "history";

    String[]inputs = {"Manual Entry", "GPS", "Automatic"};
    String[]activities = {"Running","Walking","Standing","Cycling","Hiking","Downhill Skiing",
            "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
            "Mountain Biking", "Wheelchair", "Elliptical", "Other"};


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //open database
        data = new Database(getActivity());
        data.open();

        //start loader
        getLoaderManager().initLoader(0, null, this);

        //set listadapter
        adapter = new CustomAdapter(getActivity(), new ArrayList<ExerciseEntry>());
        setListAdapter(adapter);

        //register gcm receiver
        gcmReceiver = new GcmReceiver();
        IntentFilter gcmFilter = new IntentFilter();
        gcmFilter.addAction("GCM_UPDATE");
        getActivity().registerReceiver(gcmReceiver, gcmFilter);
    }




    //start display entry activity
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        super.onListItemClick(parent, v, position, id);

        //get index from third row
        TextView indexLine = (TextView) v.findViewById(R.id.history3);
        long index = Long.parseLong(indexLine.getText().toString());
        ExerciseEntry entry = data.fetchIndexedEntry(index);

        //if manual input, then start manual display entry activity
        if(entry.getmInputType() == 0) {
            //start displayentry activity, pass in the entry values
            Intent mIntent = new Intent(getActivity(), DisplayEntryActivity.class);
            mIntent.putExtra(inputTag, entry.getmInputType());
            mIntent.putExtra(activityTag, entry.getmActivityType());
            mIntent.putExtra(dateTimeTag, entry.getmDateTime());
            mIntent.putExtra(durationTag, entry.getmDuration());
            mIntent.putExtra(distanceTag, entry.getmDistance());
            mIntent.putExtra(calorieTag, entry.getmCalorie());
            mIntent.putExtra(heartRateTag, entry.getmHeartRate());
            mIntent.putExtra(idTag, entry.getmId());
            getActivity().startActivity(mIntent);
        }

        //if gps or auto, then start map activity
        else{
            Intent mIntent = new Intent(getActivity(), MapActivity.class);
            mIntent.putExtra(historyTag, 1);
            mIntent.putExtra(idTag, entry.getmId());
            getActivity().startActivity(mIntent);
        }
    }

    //loader class that handles reading from database in separate thread
    public static class HistoryLoader extends AsyncTaskLoader<List<ExerciseEntry>> {

        public HistoryLoader(Context context){
            super(context);
        }

        //make it start loading
        @Override
        protected void onStartLoading() {
                forceLoad();
        }

        //read from database
        @Override
        public List<ExerciseEntry> loadInBackground() {
            exercises = data.fetchEntries();
            return exercises;
        }
    }

    //override loader methods
    //initialize loader
    @Override
    public Loader<List<ExerciseEntry>> onCreateLoader(int id, Bundle args) {
        return new HistoryLoader(getActivity());
    }

    //when finished loading, update the arraylist
    @Override
    public void onLoadFinished(Loader<List<ExerciseEntry>> loader, List<ExerciseEntry> data) {
        adapter.exercises.addAll(data);
        adapter.notifyDataSetChanged();
    }

    //when loader is reset
    @Override
    public void onLoaderReset(Loader<List<ExerciseEntry>> loader) {
        adapter.exercises.addAll(new ArrayList<ExerciseEntry>());
        adapter.notifyDataSetChanged();
    }

    //custom adapter for listview
    public class CustomAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<ExerciseEntry> exercises;

        public CustomAdapter(Context context, List<ExerciseEntry> exercises) {
            this.exercises = exercises;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            view = inflater.inflate(R.layout.history_fragment, parent, false);

            ExerciseEntry exercise = exercises.get(position);

            //populate first row
            TextView firstLine = (TextView) view.findViewById(R.id.history1);
            String input = inputs[exercise.getmInputType()];

            //get activity
            String activity;
            if(exercise.getmInputType()==0 | exercise.getmInputType()==1) {
                activity = activities[exercise.getmActivityType()];
            }
            else{
                activity = getAutoActivityString(exercise.getmActivityType());
            }

            String dateTime = makeStringDateTime(exercise.getmDateTime());
            String row1= input + ":  " + activity + ",  " + dateTime;
            firstLine.setText(row1);


            //populate second row
            String unit = getUnit();
            //second row
            TextView secondLine = (TextView) view.findViewById(R.id.history2);
            String distance = makeStringDistance(exercise.getmDistance(), unit);
            String duration = makeStringDuration(exercise.getmDuration());
            String row2 = distance + ",  " + duration;
            secondLine.setText(row2);

            //populate third row
            TextView thirdLine = (TextView) view.findViewById(R.id.history3);
            thirdLine.setText(String.valueOf(exercise.getmId()));

            return view;
        }

        @Override
        public void notifyDataSetChanged(){
            List<ExerciseEntry> list = data.fetchEntries();
            adapter = new CustomAdapter(getActivity(), list);
            setListAdapter(adapter);
            super.notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return exercises.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public int getCount() {
            return exercises.size();
        }

    }

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

    //return current unit preference
    public String getUnit(){
        String unit = "Kilometers";

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitValue = preference.getString("list_preference", "-1");

        if (unitValue.equals("Imperial (Miles)")){
            unit = "Miles";
        }
        return unit;
    }

    //format date and time
    public static String makeStringDateTime(long dateTime) {
        Date date = new Date(dateTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
        return timeFormat.format(date) + " " + dateFormat.format(date);
    }

    //format duration
    public static String makeStringDuration(double duration) {
        int minutes = (int)(duration/60);
        int seconds = (int)(duration%60);
        return String.valueOf(minutes) + "min " + String.valueOf(seconds) + " secs";
    }

    //format the distance, account for km/mile
    public static String makeStringDistance(double distance, String unitPref) {

        //1 mile = 1.60934 km
        if (unitPref.equals("Kilometers")) {
            distance *= 1.60934;
        }
        return String.format("%.1f", distance)+" "+unitPref;
    }

    //open/close database when paused and resumed
    @Override
    public void onResume() {
        data.open();

        //register receiver again
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("GCM_UPDATE");
        getActivity().registerReceiver(gcmReceiver, intentFilter);
        super.onResume();
    }
    @Override
    public void onPause() {
        data.close();

        //unregister receiver
        getActivity().unregisterReceiver(gcmReceiver);
        super.onPause();
    }

    //updates the history list when gcm message is received
    public class GcmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("jj", "received gcmBroadcast");
            adapter.notifyDataSetChanged();
        }
    }


}
