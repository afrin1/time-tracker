package com.afdroid.timetracker.screens;

import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.afdroid.timetracker.R;
import com.afdroid.timetracker.Utils.AppInfo;
import com.afdroid.timetracker.Utils.ListItemDecoration;
import com.afdroid.timetracker.adapters.AppListAdapter;
import com.afdroid.timetracker.preferences.TimeTrackerPrefHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements
        AppListAdapter.OnSettingsChangedListener{

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
        prefList = new LinkedList<String>(Arrays.asList(TextUtils.split(serialized, ",")));

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
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
//                    Log.d(AppHelper.TAG, "info = "+info.packageName);
                    boolean isChecked = false;
                    if (prefList.contains(info.packageName)) {
                        isChecked = true;
                    }
                    appList.add(new AppInfo((String) info.loadLabel(packageManager),
                            info.packageName,
                            info.loadIcon(packageManager),
                            isChecked));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onListChanged(int position, boolean isChecked) {
        String pkgName = appList.get(position).getAppPkgName();
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
    public void onBackPressed() {
        super.onBackPressed();
        TimeTrackerPrefHandler.INSTANCE.savePkgList
                (TextUtils.join(",", prefList), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeTrackerPrefHandler.INSTANCE.savePkgList
                (TextUtils.join(",", prefList), this);
    }
}
