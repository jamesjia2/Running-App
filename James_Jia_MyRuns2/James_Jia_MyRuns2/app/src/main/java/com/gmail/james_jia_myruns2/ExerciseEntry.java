package com.gmail.james_jia_myruns2;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by James on 2/1/2017.
 */

//Model object - implements serializable so it can be passed through intents
public class ExerciseEntry implements Serializable{

    private Long id;
    private int mInputType;  // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private Long mDateTime;    // When does this entry happen
    private int mDuration;         // Exercise duration in seconds
    private float mDistance;      // Distance traveled. Either in meters or feet.
    private float mAvgPace;       // Average pace
    private float mAvgSpeed;     // Average speed
    private int mCalorie;        // Calories burnt
    private float mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;       // Heart rate
    private String mComment;       // Comments
    private ArrayList<LatLng> mLocationList; // Location list

    //setters and getters
    public long getmId() {
        return id;
    }
    public void setmId(long id) {
        this.id = id;
    }

    public int getmInputType() {
        return mInputType;
    }
    public void setmInputType(int mInputType) {
        this.mInputType = mInputType;
    }

    public int getmActivityType() {
        return mActivityType;
    }
    public void setmActivityType(int mActivityType) {
        this.mActivityType = mActivityType;
    }

    public long getmDateTime() {
        return mDateTime;
    }
    public void setmDateTime(long mDateTime) {
        this.mDateTime = mDateTime;
    }

    public int getmDuration() {
        return mDuration;
    }
    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public float getmDistance() {
        return mDistance;
    }
    public void setmDistance(float mDistance) {
        this.mDistance = mDistance;
    }

    public float getmAvgPace() {
        return mAvgPace;
    }
    public void setmAvgPace(float mAvgPace) {
        this.mAvgPace = mAvgPace;
    }

    public float getmAvgSpeed() {
        return mAvgSpeed;
    }
    public void setmAvgSpeed(float mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public int getmCalorie() {
        return mCalorie;
    }
    public void setmCalorie(int mCalorie) {
        this.mCalorie = mCalorie;
    }

    public float getmClimb() {
        return mClimb;
    }
    public void setmClimb(float mClimb) {
        this.mClimb = mClimb;
    }

    public int getmHeartRate() {
        return mHeartRate;
    }
    public void setmHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String getmComment() {
        return mComment;
    }
    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public ArrayList<LatLng> getmLocationList() {
        return mLocationList;
    }
    public void setmLocationList(ArrayList<LatLng> mLocationList) {this.mLocationList = mLocationList;
    }

    //turns the latLngList into a byte[] for storage in sqlite database
    public byte[] makeByteArray(){

        //LatLng[] latLngArray = (LatLng[]) mLocationList.toArray();
        LatLng[] latLngArray = new LatLng[mLocationList.size()];
        for(int i=0; i<mLocationList.size(); i++){
            latLngArray[i] = mLocationList.get(i);
        }

        int[] intArray = new int[latLngArray.length * 2];

        for (int i = 0; i < latLngArray.length; i++) {
            intArray[i * 2] = (int) (latLngArray[i].latitude * 1E6);
            intArray[(i * 2) + 1] = (int) (latLngArray[i].longitude * 1E6);
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * Integer.SIZE);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);

        return byteBuffer.array();
    }

    //turns the byte[] back into a latLng ArrayList for reading from database
    public static ArrayList<LatLng> makeArrayList(byte[] bytePointArray) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytePointArray);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        int[] intArray = new int[bytePointArray.length / Integer.SIZE];
        intBuffer.get(intArray);

        LatLng[] latLngArray = new LatLng[intArray.length / 2];

        assert (latLngArray != null);

        for (int i = 0; i < latLngArray.length; i++) {
            latLngArray[i] = new LatLng(((double) intArray[i * 2] / 1E6F), ((double) intArray[i * 2 + 1] / 1E6F));
        }

        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();

        for(int i=0; i<latLngArray.length; i++){
            latLngArrayList.add(latLngArray[i]);
        }

        return latLngArrayList;
    }




}
