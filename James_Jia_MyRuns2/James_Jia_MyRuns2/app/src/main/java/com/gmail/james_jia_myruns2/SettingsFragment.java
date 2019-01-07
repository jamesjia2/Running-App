package com.gmail.james_jia_myruns2;

/**
 * Created by James on 1/15/2017.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

public class SettingsFragment extends PreferenceFragment {

    int preference = 0;

    void FragmentC(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load preferences xml
        addPreferencesFromResource(R.xml.preferences);

        //refresh history when unit preference is changed
        Preference unitPreference = findPreference("list_preference");
        unitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                HistoryFragment.adapter.notifyDataSetChanged();
                return true;
            }
        });

        //start profile activity when item is clicked
        Preference userProfile = findPreference("profile_preference");
        userProfile.setOnPreferenceClickListener((new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getActivity(), ProfileActivity.class);
                startActivity(i);
                return false;
            }
        }));
    }

}
