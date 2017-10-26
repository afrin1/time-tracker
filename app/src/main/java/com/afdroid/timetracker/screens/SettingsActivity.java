package com.afdroid.timetracker.screens;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afdroid.timetracker.R;
import com.afdroid.timetracker.Utils.AppInfo;
import com.afdroid.timetracker.Utils.ListItemDecoration;
import com.afdroid.timetracker.adapters.AppListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private List<AppInfo> appList = new ArrayList<>();
    private AppListAdapter appUsageAdapter;
    private RecyclerView appRecyclerList;
    private GetAppsDataTask getAppsDataTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appRecyclerList = (RecyclerView) findViewById(R.id.app_list);

        /*Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getApplicationContext().getPackageManager().
                queryIntentActivities( mainIntent, 0);
        for (int i=0; i<pkgAppsList.size();i++) {
            Log.d(AppHelper.TAG, "pkg name = "+pkgAppsList.get(i).resolvePackageName);
        }*/

        /*// Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();*/

        setAppList();
        getAppsDataTask = new GetAppsDataTask();
        if (getAppsDataTask != null) {
            getAppsDataTask.execute();
        }

    }

    private void setAppList() {
        appUsageAdapter = new AppListAdapter(this, appList);
        if (appUsageAdapter != null) {
            appRecyclerList.setAdapter(appUsageAdapter);
            appRecyclerList.setLayoutManager(new LinearLayoutManager(this));
            appRecyclerList.addItemDecoration(new ListItemDecoration(
                    Math.round(getResources().getDisplayMetrics().density * 5)));
//            appUsageAdapter.setOnSettingsChangedListener(this);
        }
    }

    class GetAppsDataTask extends AsyncTask<Void, Void, List<AppInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            adapter.setLoading(true);
        }

        @Override
        protected List<AppInfo> doInBackground(Void... params) {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            for (ApplicationInfo app: apps) {
//                Log.d(AppHelper.TAG, "pkg name = "+app.packageName);
                try {
                    appList.add(new AppInfo(app.name,
                            pm.getApplicationIcon(app.packageName), false));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return (appList);
        }


        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            appUsageAdapter.notifyDataSetChanged();
        }
    }

}
