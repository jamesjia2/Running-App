package com.gmail.james_jia_myruns2;

/**
 * Created by James on 1/15/2017.
 */

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartFragment extends Fragment {

    //manual by default
    static int inputType=0;
    //running by default
    static int activityType = 0;

    String tag = "jj";
    public static Database data;

    public String historyTag = "history";
    public static String SERVER_ADDR = "https://eng-charge-159419.appspot.com/";

    String[]inputs = {"Manual Entry", "GPS", "Automatic"};
    String[]activities = {"Running","Walking","Standing","Cycling","Hiking","Downhill Skiing",
            "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
            "Mountain Biking", "Wheelchair", "Elliptical", "Other"};

    void FragmentA(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View view = inflater.inflate(R.layout.start_fragment, container, false);
        Button startButton = (Button) view.findViewById(R.id.button);
        Button syncButton = (Button) view.findViewById(R.id.button2);

        //start button
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                //get input spinner and check what its current value is
                Spinner mySpinner =(Spinner) view.findViewById(R.id.input_spinner);
                final String input = mySpinner.getSelectedItem().toString();
                if(input.equals("Manual Entry")) {
                    inputType = 0;
                }
                if(input.equals("GPS")) {
                    inputType = 1;
                }
                if(input.equals("Automatic")) {
                    inputType = 2;
                }

                //get activity spinner and check what its current value is
                mySpinner =(Spinner) view.findViewById(R.id.activity_spinner);
                final String activity = mySpinner.getSelectedItem().toString();
                if(activity.equals("Running")) {
                    activityType = 0;
                }
                if(activity.equals("Walking")) {
                    activityType = 1;
                }
                if(activity.equals("Standing")) {
                    activityType = 2;
                }
                if(activity.equals("Cycling")) {
                    activityType = 3;
                }
                if(activity.equals("Hiking")) {
                    activityType = 4;
                }
                if(activity.equals("Downhill Skiing")) {
                    activityType = 5;
                }
                if(activity.equals("Cross-Country Skiing")) {
                    activityType = 6;
                }
                if(activity.equals("Snowboarding")) {
                    activityType = 7;
                }
                if(activity.equals("Skating")) {
                    activityType = 8;
                }
                if(activity.equals("Swimming")) {
                    activityType = 9;
                }
                if(activity.equals("Mountain Biking")) {
                    activityType = 10;
                }
                if(activity.equals("Wheelchair")) {
                    activityType = 11;
                }
                if(activity.equals("Elliptical")) {
                    activityType = 12;
                }
                if(activity.equals("Other")) {
                    activityType = 13;
                }

                //next activity depends on what spinner's value is
                if(input.equals("Manual Entry")) {
                    Intent s = new Intent(getActivity(), StartActivity.class);
                    startActivity(s);
                }
                if(input.equals("GPS")) {
                    Intent s = new Intent(getActivity(), MapActivity.class);
                    s.putExtra(historyTag, 0);
                    startActivity(s);
                }
                if(input.equals("Automatic")) {
                    Intent s = new Intent(getActivity(), MapActivity.class);
                    s.putExtra(historyTag, 0);
                    startActivity(s);
                }
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //open database
                data = HistoryFragment.data;
                data.open();
                ArrayList<ExerciseEntry> exerciseList = new ArrayList<ExerciseEntry>();
                exerciseList = data.fetchEntries();

                //clear out current datastore
                DeleteAllTask delTask = new DeleteAllTask();
                delTask.execute();

                //individually add all the current exercise entries to the datastore
                for(int i = 0; i<exerciseList.size(); i++){

                    //use an async task to add the entries to the datastore and update the html page
                    AddEntryTask addTask = new AddEntryTask();
                    addTask.execute(exerciseList.get(i));
                }
            }
        });
        return view;
    }

    //async task for adding an exercise entry to the datastore
    private class AddEntryTask extends AsyncTask<ExerciseEntry, Void, String>{

        @Override
        protected String doInBackground(ExerciseEntry... params) {

            String id = Long.toString(params[0].getmId());

            //input type
            String inputType = inputs[params[0].getmInputType()];

            //activity types for auto mode else gps/manual mode
            String activityType = "";
            if(params[0].getmInputType() == 2) {
                activityType = getAutoActivityString(params[0].getmActivityType());
            }
            else{
                activityType = activities[params[0].getmActivityType()];
            }

            //formatted date
            String dateTime = makeStringDateTime(params[0].getmDateTime());

            //distance
            String distanceString = String.format("%.2f", params[0].getmDistance());
            String distance = distanceString+" miles";

            //duration
            String duration = makeStringDuration(params[0].getmDuration());

            //speed
            String speedString = String.format("%.2f", params[0].getmAvgSpeed());
            String speed = speedString+" m/h";

            //calories
            String calories = Integer.toString(params[0].getmCalorie())+" calories";

            //climb
            String climbString = String.format("%.2f", params[0].getmClimb());
            String climb = climbString+" miles";

            //heartrate
            String heartrate = Integer.toString(params[0].getmHeartRate())+" bpm";

            //comment
            String comment = params[0].getmComment();

            //create hashmap param
            Map<String, String> map = new HashMap<String, String>();

            //put strings into hashmap
            map.put("id", id);
            map.put("input", inputType);
            map.put("activity", activityType);
            map.put("date", dateTime);
            map.put("distance", distance);
            map.put("duration", duration);
            map.put("avgSpeed", speed);
            map.put("calories", calories);
            map.put("climb", climb);
            map.put("heartRate", heartrate);
            map.put("comment", comment);

            try {
                //add to datastore
                ServerUtilities.post(SERVER_ADDR+"/add.do", map);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    //async task for adding an exercise entry to the datastore
    private class DeleteAllTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            Map<String, String> map = new HashMap<String, String>();
            try {
                ServerUtilities.post(SERVER_ADDR+"/deleteall.do", map);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }
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

    //format duration
    public static String makeStringDuration(double duration) {
        int minutes = (int)(duration/60);
        int seconds = (int)(duration%60);
        return String.valueOf(minutes) + "min " + String.valueOf(seconds) + " secs";
    }

    public static String makeStringDateTime(long dateTime) {
        Date date = new Date(dateTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
        return timeFormat.format(date) + " " + dateFormat.format(date);
    }

}
