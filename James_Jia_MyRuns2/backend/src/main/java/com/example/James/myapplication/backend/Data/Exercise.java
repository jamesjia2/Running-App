package com.example.James.myapplication.backend.Data;

/**
 * Created by James on 2/21/2017.
 */

//model for datastore
public class Exercise {

    //intialize datastore keys
    public static final String EXERCISE_PARENT_ENTITY_NAME = "ExerciseParent";
    public static final String EXERCISE_PARENT_KEY_NAME = "ExerciseParent";
    public static final String EXERCISE_ENTITY_NAME = "Exercise";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_INPUT = "input";
    public static final String FIELD_NAME_ACTIVITY = "activity";
    public static final String FIELD_NAME_DATETIME = "datetime";
    public static final String FIELD_NAME_DURATION = "duration";
    public static final String FIELD_NAME_DISTANCE = "distance";
    public static final String FIELD_NAME_AVGSPEED = "avgspeed";
    public static final String FIELD_NAME_CALORIES = "calories";
    public static final String FIELD_NAME_CLIMB = "climb";
    public static final String FIELD_NAME_HEARTRATE = "heartrate";
    public static final String FIELD_NAME_COMMENT = "comment";

    public String mID, mInput, mActivity, mDateTime, mDuration, mDistance, mAverageSpeed,
            mCalories, mClimb, mHeartRate, mComment;

    //create a new exercise with specified values
    public Exercise(String id, String input, String activity, String dateTime, String duration,
                    String distance, String avgSpeed, String calories, String climb,
                    String heartRate, String comment) {

        // Initialize parameters
        mID = id;
        mInput = input;
        mActivity = activity;
        mDateTime = dateTime;
        mDuration = duration;
        mDistance = distance;
        mAverageSpeed = avgSpeed;
        mCalories = calories;
        mClimb = climb;
        mHeartRate = heartRate;
        mComment = comment;
    }

    //create a blank exercise
    public Exercise(){
        mID = "0";
        mInput = "0";
        mActivity = "0";
        mDateTime = "0";
        mDuration = "0";
        mDistance = "0";
        mAverageSpeed = "0";
        mCalories = "0";
        mClimb = "0";
        mHeartRate = "0";
        mComment = "0";
    }

    //for testing
    public void setDefault(){
        mID = "0";
        mInput = "0";
        mActivity = "0";
        mDateTime = "0";
        mDuration = "0";
        mDistance = "0";
        mAverageSpeed = "0";
        mCalories = "0";
        mClimb = "0";
        mHeartRate = "0";
        mComment = "0";
    }
}