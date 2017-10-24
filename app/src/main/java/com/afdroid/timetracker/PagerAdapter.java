package com.afdroid.timetracker;

/**
 * Created by afrin on 12/7/17.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.afdroid.timetracker.fragments.DailyStats;
import com.afdroid.timetracker.fragments.MonthlyStats;
import com.afdroid.timetracker.fragments.WeeklyStats;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DailyStats tab1 = new DailyStats();
                return tab1;
            case 1:
                WeeklyStats tab2 = new WeeklyStats();
                return tab2;
            case 2:
                MonthlyStats tab3 = new MonthlyStats();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

