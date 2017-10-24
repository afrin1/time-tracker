package com.afdroid.timetracker;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.afdroid.timetracker.Utils.AppHelper.getTime;
import static com.afdroid.timetracker.Utils.AppHelper.initAppHelper;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;
    UsageStatsManager mUsageStatsManager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.daily_stats)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.weekly_stats)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.monthly_stats)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setVisibility(View.INVISIBLE);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        initAppHelper(getApplicationContext());

        fillStats();
    }

    private void fillStats() {
        if (hasPermission()){
            tabLayout.setVisibility(View.VISIBLE);
            getStats();
        }else{
            requestPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "resultCode " + resultCode);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                fillStats();
                tabLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void requestPermission() {
        Toast.makeText(this, "Need to request permission", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
    }

    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
//        return ContextCompat.checkSelfPermission(this,
//                Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED;
    }



    private void getStats() {
        mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
//        getDailyStats();
        getWeeklyStats();
        getMonthlyStats();
    }




    private HashMap<String, String> getWeeklyStats(){
        HashMap<String, String> map = new HashMap<String, String>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //start of week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

//        //start of month
//        calendar.set(Calendar.DAY_OF_MONTH, 1);

        long millis = calendar.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date startresultdate = new Date(millis);
        Date endresultdate = new Date(System.currentTimeMillis());
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(
                millis,
                System.currentTimeMillis());
//        TextView lTextView = (TextView) findViewById(R.id.weekly_usage_stats);

        StringBuilder lStringBuilder = new StringBuilder();
        lStringBuilder.append("WEEKLY STATS \n");
        lStringBuilder.append("start date: "+sdf.format(startresultdate)+" \n");
        lStringBuilder.append("end date: "+sdf.format(endresultdate)+" \n");
        if (lUsageStatsMap != null) {
            lStringBuilder.append("Facebook");
            lStringBuilder.append(" - aggregate : ");
            if (lUsageStatsMap.get("com.facebook.katana") != null) {
                lStringBuilder.append(getTime(lUsageStatsMap.get("com.facebook.katana").getTotalTimeInForeground()));
            }
            lStringBuilder.append("\r\n");
            lStringBuilder.append("Messenger");
            lStringBuilder.append(" - aggregate : ");
            if (lUsageStatsMap.get("com.facebook.orca") != null) {
                lStringBuilder.append(getTime(lUsageStatsMap.get("com.facebook.orca").getTotalTimeInForeground()));
            }
            lStringBuilder.append("\r\n");
            lStringBuilder.append("Whatsapp");
            lStringBuilder.append(" - aggregate : ");
            if (lUsageStatsMap.get("com.whatsapp") != null) {
                lStringBuilder.append(getTime(lUsageStatsMap.get("com.whatsapp").getTotalTimeInForeground()));
            }
            lStringBuilder.append("\r\n");
        }

//        lTextView.setText(lStringBuilder.toString());

        SimpleDateFormat sd1f = new SimpleDateFormat("MMM dd,yyyy");

        /*int i=0;
        boolean test=true;
        while(test) {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()+i);
            millis = calendar.getTimeInMillis();
            Date date = new Date(millis);
            Date system = new Date(System.currentTimeMillis());
            if (sd1f.format(date).toString().equals(sd1f.format(system).toString())){
                Log.d("Calendar", "break loop");
                test=false;
            }
            i++;
            Log.d("CAlendar", " day of week - "+date);
        }
        i=1;
        test = true;
        while(test) {
            Log.d("CAlendar", " Month::");
            calendar.set(Calendar.DAY_OF_MONTH,i);
            millis = calendar.getTimeInMillis();
            Date date = new Date(millis);
            Date system = new Date(System.currentTimeMillis());
            if (sd1f.format(date).toString().equals(sd1f.format(system).toString())){
                Log.d("Calendar", "break loop");
                test=false;
            }
            i++;
            Log.d("CAlendar", " day of month - "+date);
        }*/

        return map;
    }

    private void getMonthlyStats() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //start of week
//        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        //start of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        long millis = calendar.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date startresultdate = new Date(millis);
        Date endresultdate = new Date(System.currentTimeMillis());
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(
                millis,
                System.currentTimeMillis());

//        TextView lTextView = (TextView) findViewById(R.id.daily_usage_stats);

        StringBuilder lStringBuilder = new StringBuilder();
        lStringBuilder.append("MONTHLY STATS \n");
        lStringBuilder.append("start date: "+sdf.format(startresultdate)+" \n");
        lStringBuilder.append("end date: "+sdf.format(endresultdate)+" \n");
        if (lUsageStatsMap != null) {
            lStringBuilder.append("Facebook");
            lStringBuilder.append(" - aggregate : ");
            lStringBuilder.append(getTime(lUsageStatsMap.get("com.facebook.katana").getTotalTimeInForeground()));
            lStringBuilder.append("\r\n");
            lStringBuilder.append("Messenger");
            lStringBuilder.append(" - aggregate : ");
            lStringBuilder.append(getTime(lUsageStatsMap.get("com.facebook.orca").getTotalTimeInForeground()));
            lStringBuilder.append("\r\n");
            lStringBuilder.append("Whatsapp");
            lStringBuilder.append(" - aggregate : ");
            lStringBuilder.append(getTime(lUsageStatsMap.get("com.whatsapp").getTotalTimeInForeground()));
            lStringBuilder.append("\r\n");
        }

//        lTextView.setText(lStringBuilder.toString());
    }
}
