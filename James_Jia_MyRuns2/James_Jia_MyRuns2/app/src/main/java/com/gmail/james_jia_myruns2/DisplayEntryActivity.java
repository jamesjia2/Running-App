package com.gmail.james_jia_myruns2;

/**
 * Created by James on 2/1/2017.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class DisplayEntryActivity extends Activity {

    String[]inputs = {"Manual Entry", "GPS", "Automatic"};
    public String inputTag = "input";
    public String activityTag = "activity";
    public String dateTimeTag = "dateTime";
    public String durationTag = "duration";
    public String distanceTag = "distance";
    public String calorieTag = "calorie";
    public String heartRateTag = "heartRate";
    public String idTag = "id";
    String[]activities = {"Running","Walking","Standing","Cycling","Hiking","Downhill Skiing",
            "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming",
            "Mountain Biking", "Wheelchair", "Elliptical", "Other"};
    ExerciseEntry entry;
    Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_entry);


        //get entry values that were passed along
        int input = getIntent().getIntExtra(inputTag, 0);
        int activity = getIntent().getIntExtra(activityTag, 0);
        Long dateTime = getIntent().getLongExtra(dateTimeTag, 0);
        int duration = getIntent().getIntExtra(durationTag, 0);
        float distance = getIntent().getFloatExtra(distanceTag, 0);
        int calorie = getIntent().getIntExtra(calorieTag, 0);
        int heartRate = getIntent().getIntExtra(heartRateTag, 0);
        id = getIntent().getLongExtra(idTag, 0);

        //populate the edittext boxes
        EditText displayInput = (EditText) findViewById(R.id.input);

        //input type
        String inputType = inputs[input];
        displayInput.setText(inputType);

        //activity type
        EditText displayActivity = (EditText) findViewById(R.id.activity);
        String activityType = activities[activity];
        displayActivity.setText(activityType);

        //date and time
        EditText displayDateTime = (EditText) findViewById(R.id.datetime);
        displayDateTime.setText(HistoryFragment.makeStringDateTime(dateTime));

        //duration
        EditText displayDuration = (EditText) findViewById(R.id.duration);
        displayDuration.setText(HistoryFragment.makeStringDuration(duration));

        //distance
        EditText displayDistance = (EditText) findViewById(R.id.distance);
        String unit = getUnit();
        String distanceText = HistoryFragment.makeStringDistance(distance, unit);
        displayDistance.setText(distanceText);

        //calories
        EditText displayCalories = (EditText) findViewById(R.id.calories);
        displayCalories.setText(calorie+" Cals");

        //heart rate
        EditText displayHeartrate = (EditText) findViewById(R.id.heartrate);
        displayHeartrate.setText(heartRate+" BPM");
    }

    //make the delete button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 0, 0, "DELETE").
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    //delete the current exercise entry when delete is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        new deleteExercise().execute();
        finish();
        return true;
    }

    //asynctask for deleting from database
    private class deleteExercise extends AsyncTask<ExerciseEntry, Integer, String> {

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

    //helper function to get the unit from settings - either Miles or Kilometers
    public String getUnit(){
        String unit = "Kilometers";

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String unitValue = preference.getString("list_preference", "-1");

        if (unitValue.equals("Imperial (Miles)")){
            unit = "Miles";
        }
        return unit;
    }
}
