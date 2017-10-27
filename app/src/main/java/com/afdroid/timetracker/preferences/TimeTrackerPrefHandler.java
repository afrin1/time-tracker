package com.afdroid.timetracker.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.afdroid.timetracker.Utils.AppHelper;

/**
 * Created by afrin on 26/10/17.
 */

public enum TimeTrackerPrefHandler {
    INSTANCE;
    public static final String PREF_LIST = "pref_list";
    private SharedPreferences sharedPreferences = null;

    public void savePkgList(String pkgName, Context ctx) {
        Log.d(AppHelper.TAG, "***saved package name = "+pkgName);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(PREF_LIST, pkgName);
        e.apply();
    }

    public String getPkgList(Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        Log.d(AppHelper.TAG, "***get package name = "+sharedPreferences.
                getString(PREF_LIST, null));
        return sharedPreferences.getString(PREF_LIST, null);
    }
}
