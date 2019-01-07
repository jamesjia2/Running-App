package com.gmail.james_jia_myruns2;

/**
 * Created by James on 1/15/2017.
 */

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

public class StartFragment extends Fragment {

    //manual by default
    static int inputType=0;
    //running by default
    static int activityType = 0;

    public String historyTag = "history";



    void FragmentA(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View view = inflater.inflate(R.layout.start_fragment, container, false);
        Button startButton = (Button) view.findViewById(R.id.button);

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

        return view;
    }

}
