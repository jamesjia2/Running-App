package com.gmail.james_jia_myruns2;

/**
 * Created by James on 1/15/2017.
 */

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.app.ListActivity;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class StartActivity extends ListActivity
        implements ListView.OnItemClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    static final String[] DATA = new String[] { "Date", "Time", "Duration", "Distance",
            "Calories", "Heart Rate", "Comment"};

    Calendar calendar = Calendar.getInstance();
    public int maxDigits = 8;

    //entry object and the reference to the database
    public static ExerciseEntry exercise;
    public static Database data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up adapter
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.activity_start, DATA);

        //set up listview
        setListAdapter(mAdapter);

        //get listview
        ListView listView = getListView();

        View footerView = getLayoutInflater().inflate(R.layout.footer, null);

        //set up footer for buttons beneath the listview contents
        listView.addFooterView(footerView);
        listView.setFooterDividersEnabled(false);

        //listener that responds whenever a list item is clicked
        listView.setOnItemClickListener(this);

        //set up exercise entry with default values
        exercise = new ExerciseEntry();
        setDefaultExercise(exercise);
        Log.d("jj","HI");

        data = new Database(this);
    }

    //initialize all values for exercise entry
    public void setDefaultExercise(ExerciseEntry ex){
        ex.setmDateTime(calendar.getTimeInMillis());
        ex.setmDistance(0);
        ex.setmDuration(0);
        ex.setmCalorie(0);
        ex.setmHeartRate(0);
        ex.setmComment("");
        ex.setmInputType(StartFragment.inputType);
        ex.setmActivityType(StartFragment.activityType);

        ArrayList<LatLng> list = new ArrayList<LatLng>();
        ex.setmLocationList(list);
    }


    //when listview item is clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

        //set up individual list items and also their onClick callbacks
        //DATE
        if (id==0){
            final DatePickerDialog datePickerDialog = new DatePickerDialog(this,this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }

        //TIME
        if (id==1){
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, this,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }

        //DURATION
        if(id==2){
            final EditText entry = new EditText(this);
            entry.setInputType(InputType.TYPE_CLASS_NUMBER);
            entry.setHint("Enter run duration here.");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Duration");
            alertDialog.setView(entry);
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //WRITE
                            //get value from edittext
                            if(!entry.getText().toString().isEmpty()) {
                                if(entry.getText().toString().length()<=maxDigits) {
                                    int finalValue = Integer.parseInt(entry.getText().toString());
                                    //set entry with the value
                                    exercise.setmDuration(finalValue);
                                }
                            }
                        }
                    });
            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.d("jj", "canceled");
                        }
                    });
            alertDialog.show();
        }

        //DISTANCE
        if(id==3){
            final EditText entry = new EditText(this);
            entry.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
            entry.setHint("Enter run distance here.");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Distance");
            alertDialog.setView(entry);
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //WRITE
                            //get value from edittext
                            if(!entry.getText().toString().isEmpty()) {
                                if(entry.getText().toString().length()<=maxDigits) {
                                    float finalValue = Float.parseFloat(entry.getText().toString());
                                    //set entry with the value
                                    exercise.setmDistance(finalValue);
                                }
                            }
                        }
                    });
            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.d("jj", "canceled");
                        }
                    });
            alertDialog.show();
        }

        //CALORIES
        if(id==4){
            final EditText entry = new EditText(this);
            entry.setInputType(InputType.TYPE_CLASS_NUMBER);
            entry.setHint("Enter calories burned here.");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Calories");
            alertDialog.setView(entry);
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //WRITE
                            //get value from edittext
                            if(!entry.getText().toString().isEmpty()) {
                                if(entry.getText().toString().length()<=maxDigits) {
                                    int finalValue = Integer.parseInt(entry.getText().toString());
                                    //set entry with the value
                                    exercise.setmCalorie(finalValue);
                                }
                            }
                        }
                    });
            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.d("jj", "canceled");
                        }
                    });
            alertDialog.show();
        }

        //HEART RATE
        if(id==5){
            final EditText entry = new EditText(this);
            entry.setInputType(InputType.TYPE_CLASS_NUMBER);
            entry.setHint("Enter heart rate here.");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Heart Rate");
            alertDialog.setView(entry);
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //WRITE
                            //get value from edittext
                            if(!entry.getText().toString().isEmpty()) {
                                if(entry.getText().toString().length()<=maxDigits) {
                                    int finalValue = Integer.parseInt(entry.getText().toString());
                                    //set entry with the value
                                    exercise.setmHeartRate(finalValue);
                                }
                            }
                        }
                    });
            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.d("jj", "canceled");
                        }
                    });
            alertDialog.show();
        }

        //COMMENT
        if(id==6){
            final EditText entry = new EditText(this);
            entry.setHint("How did it go? Notes here.");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Comment");
            alertDialog.setView(entry);
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //WRITE
                            //get value from edittext
                            if(!entry.getText().toString().isEmpty()) {
                                String finalValue = entry.getText().toString();

                                //set entry with the value
                                exercise.setmComment(finalValue);
                            }
                        }
                    });
            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.d("jj", "canceled");
                        }
                    });
            alertDialog.show();
        }
    }

    //grab selected date
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        exercise.setmDateTime(calendar.getTimeInMillis());
    }

    //grab selected time
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        exercise.setmDateTime(calendar.getTimeInMillis());
    }

    //when save is clicked, write to database and exit out of activity
    public void onSaveClicked(View view){
        //write to database
        new writeEntryToDatabase().execute();
        finish();
    }

    //use AsyncTask to handle writing in background thread
    private class writeEntryToDatabase extends AsyncTask<Void, Void, Integer> {

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

    //show a toast and exit
    public void onCancelClicked(View view){
        Toast.makeText(this, "Entry discarded.", Toast.LENGTH_LONG).show();
        finish();
    }

    //open/close database when paused and resumed
    @Override
    protected void onResume() {
        data.open();
        super.onResume();
    }
    @Override
    protected void onPause() {
        data.close();
        super.onPause();
    }
}
