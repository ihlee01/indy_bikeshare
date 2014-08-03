package com.misabelleeli.pacers_bikeshare;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by M.Isabel on 7/7/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> f;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        f = new ArrayList<Fragment>();
        f.add(new GoogleMapFragment());
        f.add(new TimerFragment());
        f.add(new StationFragment());

        //f.add(new AboutFragment());
    }

    @Override
    public Fragment getItem(int index) {
        if(index >= 0 && index<=3)
        {
            return f.get(index);
        }

        return null;
    }

    @Override
    public int getCount() {
        return f.size();
    }
}
