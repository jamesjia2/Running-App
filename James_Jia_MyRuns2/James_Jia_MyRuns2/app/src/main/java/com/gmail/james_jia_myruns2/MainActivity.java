package com.gmail.james_jia_myruns2;

/**
 * Created by James on 1/15/2017.
 */

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;



public class MainActivity extends Activity {
    StartFragment startFragment;
    HistoryFragment historyFragment;
    SettingsFragment settingsFragment;
    ViewPager viewPager;
    TabLayout tabLayout;
    MyFragmentPagerAdapter myFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        //set up viewpager and layout
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        tabLayout = (TabLayout)findViewById(R.id.tab);

        //intialize fragments
        startFragment = new StartFragment();
        historyFragment = new HistoryFragment();
        settingsFragment = new SettingsFragment();

        //put fragments into the fragment pager adapter
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager());
        myFragmentPagerAdapter.addFragment(startFragment);
        myFragmentPagerAdapter.addFragment(historyFragment);
        myFragmentPagerAdapter.addFragment(settingsFragment);

        //setup view pager with fragment pager adapter
        viewPager.setAdapter(myFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

}
