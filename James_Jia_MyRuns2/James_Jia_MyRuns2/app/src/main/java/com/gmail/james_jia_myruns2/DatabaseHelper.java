package com.gmail.james_jia_myruns2;

/**
 * Created by James on 2/1/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//SQLite helper layer
public class DatabaseHelper extends SQLiteOpenHelper{

        //Column names
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_INPUT_TYPE = "input";
        public static final String COLUMN_ACTIVITY_TYPE = "activity";
        public static final String COLUMN_DATE_TIME = "dateTime";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_AVERAGE_PACE = "averagePace";
        public static final String COLUMN_AVERAGE_SPEED = "averageSpeed";
        public static final String COLUMN_CALORIES = "calories";
        public static final String COLUMN_CLIMB = "climb";
        public static final String COLUMN_HEARTRATE = "heartrate";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_LOCATION = "location";

        //Database name and version
        public static final String TABLE_NAME_ENTRIES = "Exercises";
        private static final String DATABASE_NAME = "Exercises1.db";
        private static final int DATABASE_VERSION = 1;

        //Create the database table
        public static final String CREATE_TABLE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_ENTRIES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INPUT_TYPE + " INTEGER NOT NULL, "
                + COLUMN_ACTIVITY_TYPE + " INTEGER NOT NULL, "
                + COLUMN_DATE_TIME + " DATETIME NOT NULL, "
                + COLUMN_DURATION + " FLOAT, "
                + COLUMN_DISTANCE + " FLOAT, "
                + COLUMN_AVERAGE_PACE + " FLOAT, "
                + COLUMN_AVERAGE_SPEED + " FLOAT, "
                + COLUMN_CALORIES + " INTEGER, "
                + COLUMN_CLIMB + " FLOAT, "
                + COLUMN_HEARTRATE + " INTEGER, "
                + COLUMN_COMMENT + " TEXT, "
                + COLUMN_LOCATION + " BLOB "+");";


        //constructor
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //Called when database is first accessed
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_ENTRIES);
        }

        //OnUpgrade method
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DatabaseHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ENTRIES);
            onCreate(db);
        }

}

