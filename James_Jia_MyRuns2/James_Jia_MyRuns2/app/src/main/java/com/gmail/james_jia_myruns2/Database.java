package com.gmail.james_jia_myruns2;

/**
 * Created by James on 2/1/2017.
 */

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//data source layer
public class Database {

    //database reference and helper
    private SQLiteDatabase database;
    private DatabaseHelper helper;

    //constructor - initializes SQLitehelper
    public Database(Context context) {
        helper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }


    //insert an exercise entry at the bottom of the list
    public long insertEntry(ExerciseEntry entry) {

        //wrap with ContentValue
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_INPUT_TYPE,entry.getmInputType());
        values.put(DatabaseHelper.COLUMN_ACTIVITY_TYPE,entry.getmActivityType());
        values.put(DatabaseHelper.COLUMN_DATE_TIME, entry.getmDateTime());
        values.put(DatabaseHelper.COLUMN_DURATION,entry.getmDuration());
        values.put(DatabaseHelper.COLUMN_DISTANCE,entry.getmDistance());
        values.put(DatabaseHelper.COLUMN_AVERAGE_PACE,entry.getmAvgPace());
        values.put(DatabaseHelper.COLUMN_AVERAGE_SPEED,entry.getmAvgSpeed());
        values.put(DatabaseHelper.COLUMN_CALORIES,entry.getmCalorie());
        values.put(DatabaseHelper.COLUMN_CLIMB,entry.getmClimb());
        values.put(DatabaseHelper.COLUMN_HEARTRATE,entry.getmHeartRate());
        values.put(DatabaseHelper.COLUMN_COMMENT,entry.getmComment());
        values.put(DatabaseHelper.COLUMN_LOCATION,entry.makeByteArray());

        //write to the database
        database = helper.getWritableDatabase();
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_ENTRIES, null, values);
        return insertId;
    }

    //get all the database ExerciseEntries and return them in an ArrayList
    public ArrayList<ExerciseEntry> fetchEntries() {

        //get a readable database
        ArrayList<ExerciseEntry> exercises = new ArrayList<>();
        database = helper.getReadableDatabase();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ENTRIES,
                null, null, null, null, null, null);

        cursor.moveToFirst();

        //until no more entries
        while (!cursor.isAfterLast()) {
            ExerciseEntry entry = cursorToExerciseEntry(cursor);
            exercises.add(entry);
            cursor.moveToNext();
        }

        //close the cursor and the database
        cursor.close();
        return exercises;
    }

    //remove an entry given its index
    public void deleteIndexedEntry(long index) {

        database = helper.getWritableDatabase();
        database.delete(DatabaseHelper.TABLE_NAME_ENTRIES, DatabaseHelper.COLUMN_ID
                + " = " + index, null);
    }


    //get an ExerciseEntry given its index
    public ExerciseEntry fetchIndexedEntry(long index) {

        //get a readable database
        database = helper.getReadableDatabase();

        //put cursor at the index
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ENTRIES,
                null, DatabaseHelper.COLUMN_ID + " = " + index, null, null, null, null);
        cursor.moveToFirst();

        //grab that specific exercise entry
        ExerciseEntry entry = cursorToExerciseEntry(cursor);
        cursor.close();
        return entry;
    }

    //puts database info from cursor into ExerciseEntry object
    public ExerciseEntry cursorToExerciseEntry(Cursor cursor) {

        ExerciseEntry entry = new ExerciseEntry();
        entry.setmId(cursor.getLong(0));
        entry.setmInputType(cursor.getInt(1));
        entry.setmActivityType(cursor.getInt(2));
        entry.setmDateTime(cursor.getLong(3));
        entry.setmDuration(cursor.getInt(4));
        entry.setmDistance(cursor.getFloat(5));
        entry.setmAvgPace(cursor.getFloat(6));
        entry.setmAvgSpeed(cursor.getFloat(7));
        entry.setmCalorie(cursor.getInt(8));
        entry.setmClimb(cursor.getFloat(9));
        entry.setmHeartRate(cursor.getInt(10));
        entry.setmComment(cursor.getString(11));


        byte[] byteArray = cursor.getBlob(12);
        ArrayList latLngArrayList = ExerciseEntry.makeArrayList(byteArray);
        entry.setmLocationList(latLngArrayList);
        return entry;
    }



}