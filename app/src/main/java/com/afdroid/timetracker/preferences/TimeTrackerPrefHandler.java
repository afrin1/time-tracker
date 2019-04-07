package com.afdroid.timetracker.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by afrin on 26/10/17.
 */

public enum TimeTrackerPrefHandler {
    INSTANCE;
    public static final String PREF_LIST = "pref_list";
    public static final String IS_FIRST_TIME = "is_first_time";
    public static final String MODE = "mode";
    private SharedPreferences sharedPreferences = null;

    public void savePkgList(String pkgName, Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(PREF_LIST, pkgName);
        e.apply();
    }

    public String getPkgList(Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getString(PREF_LIST, null);
    }

    public void saveIsFirstTime(boolean isFirstTime, Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(IS_FIRST_TIME, isFirstTime);
        e.apply();
    }

    public boolean getIsFirstTime(Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true);
    }

    public void setMode(int mode, Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putInt(MODE, mode);
        e.apply();
    }

    public int getMode(Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getInt(MODE, 0);
    }
}
