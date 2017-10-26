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
import android.util.Log;

import com.afdroid.timetracker.R;
import com.afdroid.timetracker.Utils.AppHelper;
import com.afdroid.timetracker.Utils.ListItemDecoration;
import com.afdroid.timetracker.adapters.AppListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private List<ApplicationInfo> appList = new ArrayList<>();
    private AppListAdapter appUsageAdapter;
    private RecyclerView appRecyclerList;
//    private GetAppsDataTask getAppsDataTask;
    private PackageManager packageManager = null;

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
        appUsageAdapter = new AppListAdapter(this, appList);
        if (appUsageAdapter != null) {
            appRecyclerList.setAdapter(appUsageAdapter);
            appRecyclerList.setLayoutManager(new LinearLayoutManager(this));
            appRecyclerList.addItemDecoration(new ListItemDecoration(
                    Math.round(getResources().getDisplayMetrics().density * 5)));
        }
    }

    private void checkForLaunchIntent(List<ApplicationInfo> list) {
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    Log.d(AppHelper.TAG, "info = "+info.packageName);
                    appList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            appUsageAdapter.notifyDataSetChanged();
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

}
