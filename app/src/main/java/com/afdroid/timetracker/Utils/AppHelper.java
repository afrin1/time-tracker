package com.afdroid.timetracker.Utils;

import android.app.usage.UsageStatsManager;
import android.content.Context;

/**
 * Created by afrin on 23/10/17.
 */

public class AppHelper {

    public static String TAG = "TIME_TRACKER";
    public static String FB_PKG_NAME = "com.facebook.katana";
    public static String FB_MSG_PKG_NAME = "com.facebook.orca";
    public static String WHATSAPP_PKG_NAME = "com.whatsapp";
    public static String INSTAGRAM_PKG_NAME = "com.instagram";

    public static int FB = 0;
    public static int WA = 1;
    public static int FB_MSG = 2;
    public static int INST = 3;


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

    public static long getHours(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
//        String time = hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
        return hours;
    }
}
