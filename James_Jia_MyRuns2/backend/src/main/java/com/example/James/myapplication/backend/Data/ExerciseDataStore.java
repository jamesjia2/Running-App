package com.example.James.myapplication.backend.Data;

/**
 * Created by James on 2/21/2017.
 */

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import java.util.ArrayList;

public class ExerciseDataStore {

    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();

    private static Key getKey() {
        return KeyFactory.createKey(Exercise.EXERCISE_PARENT_ENTITY_NAME,
                Exercise.EXERCISE_PARENT_KEY_NAME);
    }


    public static boolean add(Exercise entry) {

        //get key
        Key parentKey = getKey();

        //initialize new identity
        Entity entity = new Entity(Exercise.EXERCISE_ENTITY_NAME, entry.mID,
                parentKey);

        //get values for properties and set them
        entity.setProperty(Exercise.FIELD_NAME_ID, entry.mID);
        entity.setProperty(Exercise.FIELD_NAME_INPUT, entry.mInput);
        entity.setProperty(Exercise.FIELD_NAME_ACTIVITY, entry.mActivity);
        entity.setProperty(Exercise.FIELD_NAME_DATETIME, entry.mDateTime);
        entity.setProperty(Exercise.FIELD_NAME_DURATION, entry.mDuration);
        entity.setProperty(Exercise.FIELD_NAME_DISTANCE, entry.mDistance);
        entity.setProperty(Exercise.FIELD_NAME_AVGSPEED, entry.mAverageSpeed);
        entity.setProperty(Exercise.FIELD_NAME_CALORIES, entry.mCalories);
        entity.setProperty(Exercise.FIELD_NAME_CLIMB, entry.mClimb);
        entity.setProperty(Exercise.FIELD_NAME_HEARTRATE, entry.mHeartRate);
        entity.setProperty(Exercise.FIELD_NAME_COMMENT, entry.mComment);

        //write to datastore
        mDatastore.put(entity);
        return true;
    }

    //delete a specific datastore entry
    public static boolean delete(String name) {

        Query.Filter filter = new Query.FilterPredicate(Exercise.FIELD_NAME_ID,
                Query.FilterOperator.EQUAL, name);
        Query query = new Query(Exercise.EXERCISE_ENTITY_NAME);
        query.setFilter(filter);
        PreparedQuery pq = mDatastore.prepare(query);
        Entity result = pq.asSingleEntity();

        boolean ret = false;
        if (result != null) {
            mDatastore.delete(result.getKey());
            ret = true;
        }
        return ret;
    }

    //clear the whole datastore
    public static void deleteAll(){
        ArrayList<Exercise> list = query();
        for(int i = 0; i<list.size(); i++){
            ExerciseDataStore.delete(list.get(i).mID);
        }
    }

    //get an arraylist of all the exercise entries in the datastore
    public static ArrayList<Exercise> query() {

        ArrayList<Exercise> resultList = new ArrayList<>();
        Query query = new Query(Exercise.EXERCISE_ENTITY_NAME);
        query.setFilter(null);
        query.setAncestor(getKey());
        PreparedQuery pq = mDatastore.prepare(query);
        for (Entity entity : pq.asIterable()) {
            Exercise data = getExerciseFromEntity(entity);
            if (data != null) {
                resultList.add(data);
            }
        }
        return resultList;
    }

    //convert exercise to entity
    private static Exercise getExerciseFromEntity(Entity entity) {

        if (entity == null) {
            return null;
        }
        return new Exercise(
                (String) entity.getProperty(Exercise.FIELD_NAME_ID),
                (String) entity.getProperty(Exercise.FIELD_NAME_INPUT),
                (String) entity.getProperty(Exercise.FIELD_NAME_ACTIVITY),
                (String) entity.getProperty(Exercise.FIELD_NAME_DATETIME),
                (String) entity.getProperty(Exercise.FIELD_NAME_DURATION),
                (String) entity.getProperty(Exercise.FIELD_NAME_DISTANCE),
                (String) entity.getProperty(Exercise.FIELD_NAME_AVGSPEED),
                (String) entity.getProperty(Exercise.FIELD_NAME_CALORIES),
                (String) entity.getProperty(Exercise.FIELD_NAME_CLIMB),
                (String) entity.getProperty(Exercise.FIELD_NAME_HEARTRATE),
                (String) entity.getProperty(Exercise.FIELD_NAME_COMMENT));
    }
}
