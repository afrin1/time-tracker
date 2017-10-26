package com.afdroid.timetracker.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.afdroid.timetracker.R;
import com.afdroid.timetracker.Utils.AppHelper;

import java.util.List;

/**
 * Created by afrin on 25/10/17.
 */

public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ApplicationInfo> data;
//    private Set<String> blockedApps;
    private Context context;
    private PackageManager packageManager;

    private class AppsViewHolder extends RecyclerView.ViewHolder implements
            CompoundButton.OnCheckedChangeListener {

        private TextView txtAppName;
        private ImageView ivAppIcon;
        private Switch appSwitch;

        private AppsViewHolder(View view) {
            super (view);
            txtAppName = (TextView) view.findViewById(R.id.txt_app_label);
            ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
            appSwitch = (Switch) view.findViewById(R.id.app_switch);
            appSwitch.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Log.d(AppHelper.TAG, " Switch changed - "+b);
        }

    }

    public AppListAdapter(Context context, List<ApplicationInfo> data) {
        super();
        this.context = context;
        this.data = data;
        packageManager = context.getPackageManager();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
     View view = LayoutInflater.from(context)
                .inflate(R.layout.app_list_row, parent, false);
        return new AppsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (data.size() > 0) {
            ApplicationInfo appInfo = data.get(position);
            AppsViewHolder holder = (AppsViewHolder) viewHolder;
            holder.txtAppName.setText(appInfo.loadLabel(packageManager));
            holder.ivAppIcon.setImageDrawable(appInfo.loadIcon(packageManager));
            holder.appSwitch.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}