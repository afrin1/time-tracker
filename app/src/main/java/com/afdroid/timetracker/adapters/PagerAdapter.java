package com.afdroid.timetracker.adapters;

/**
 * Created by afrin on 12/7/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.afdroid.timetracker.fragments.StatsFragment;

import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    List<String> appList;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, List<String> applist) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.appList = applist;
    }

    @Override
    public Fragment getItem(int position) {
        StatsFragment statsFragment = new StatsFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("period", position);
        statsFragment.setArguments(args);
        return statsFragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

