package com.afdroid.timetracker.screens;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afdroid.timetracker.R;
import com.afdroid.timetracker.Utils.AppHelper;
import com.afdroid.timetracker.Utils.AppInfo;
import com.afdroid.timetracker.Utils.ListItemDecoration;
import com.afdroid.timetracker.adapters.AppListAdapter;
import com.afdroid.timetracker.preferences.TimeTrackerPrefHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements
        AppListAdapter.OnSettingsChangedListener,
        SearchView.OnQueryTextListener {

    private List<AppInfo> appList = new ArrayList<AppInfo>();
    private AppListAdapter appListAdapter;
    private RecyclerView appRecyclerList;
    private PackageManager packageManager = null;
    private List<String> prefList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appRecyclerList = (RecyclerView) findViewById(R.id.app_list);
        setAppList();
        packageManager = getPackageManager();
        new LoadApplications().execute();
    }

    private void setAppList() {
        String serialized = TimeTrackerPrefHandler.INSTANCE.getPkgList(getApplicationContext());
        if (serialized != null) {
            prefList = new LinkedList<String>(Arrays.asList
                    (TextUtils.split(serialized, ",")));
        }

        appListAdapter = new AppListAdapter(this, appList);
        if (appListAdapter != null) {
            appRecyclerList.setAdapter(appListAdapter);
            appRecyclerList.setLayoutManager(new LinearLayoutManager(this));
            appRecyclerList.addItemDecoration(new ListItemDecoration(
                    Math.round(getResources().getDisplayMetrics().density * 5)));
            appListAdapter.setOnSettingsChangedListener(this);
        }
    }

    private void checkForLaunchIntent(List<ApplicationInfo> list) {
        int count = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long millis = calendar.getTimeInMillis();
        Map<String, UsageStats> lUsageStatsMap = AppHelper.getUsageStatsManager().
                queryAndAggregateUsageStats(
                        millis,
                        System.currentTimeMillis());
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    boolean isChecked = false;
                    if (prefList.contains(info.packageName)) {
                        isChecked = true;
                    }
                    appList.add(new AppInfo((String) info.loadLabel(packageManager),
                            info.packageName,
                            info.loadIcon(packageManager),
                            isChecked));
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onListChanged(String pkgName, boolean isChecked) {
        if (isChecked) {
            if (!prefList.contains(pkgName)) {
                prefList.add(pkgName);
            }
        }
        else {
            if (prefList.contains(pkgName)) {
                prefList.remove(pkgName);
            }
        }
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            checkForLaunchIntent(
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            Collections.sort(appList, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo lhs, AppInfo rhs) {
                    return (lhs.getAppName().compareTo(rhs.getAppName()));
                }
            });
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            appListAdapter.notifyDataSetChanged();
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(SettingsActivity.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_settings_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        final List<AppInfo> filteredModelList = filter(appList, query);
        appListAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<AppInfo> filter(List<AppInfo> models, String query) {
        query = query.toLowerCase();
        final List<AppInfo> filteredModelList = new ArrayList<>();
        for (AppInfo model : models) {
            final String text = model.getAppName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeTrackerPrefHandler.INSTANCE.savePkgList
                (TextUtils.join(",", prefList), this);
    }
}
