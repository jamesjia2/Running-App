package com.gmail.james_jia_myruns2;

/**
 * Created by James on 1/15/2017.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    public MyFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    //method for adding fragments
    public void addFragment(Fragment frag){
        fragments.add(frag);
    }


    @Override
    public Fragment getItem(int pos){
        return fragments.get(pos);
    }

    @Override
    public int getCount(){
        return fragments.size();
    }

    //fragment titles
    @Override
    public CharSequence getPageTitle(int position){
        if(position == 0)
            return "Start";

        if(position == 1)
            return "History";

        if(position == 2)
            return "Settings";
        else
            return null;
    }
}

