package com.afdroid.timetracker.Utils;

import android.app.usage.UsageStatsManager;
import android.content.Context;

/**
 * Created by afrin on 23/10/17.
 */

public class AppHelper {

    public static final String TAG = "TIME_TRACKER";
    public static final String APP_ID = "ca-app-pub-3912594581926590~9557935914";
    public final static int DAILY_STATS = 0;
    public final static int YESTERDAY_STATS = 1;
    public final static int WEEKLY_STATS = 2;
    public final static int MONTHLY_STATS = 3;
    public final static int NETWORK_MODE = 1;

    private static UsageStatsManager mUsageStatsManager;

    public static void initAppHelper(Context context) {
        if (mUsageStatsManager == null) {
            mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }
    }

    public static UsageStatsManager getUsageStatsManager() {
        return mUsageStatsManager;
    }

    public static String getTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        String time = hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
        return time;
    }

    public static float getHours(long millis) {
        float seconds = millis / 1000;
        float minutes = seconds / 60;
        float hours = (minutes/60);
        return hours;
    }

    public static float getMinutes(long millis) {
        float seconds = millis / 1000;
        float minutes = seconds / 60;
        return minutes;
    }
}
