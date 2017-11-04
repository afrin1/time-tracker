package com.afdroid.timetracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.afdroid.timetracker.R;
import com.afdroid.timetracker.Utils.AppInfo;
import com.afdroid.timetracker.screens.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by afrin on 25/10/17.
 */

public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AppInfo> data;
    private Context context;
    private OnSettingsChangedListener onSettingsChangedListener;

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
            data.get(getAdapterPosition()).setSelectedForStats(b);
            onSettingsChangedListener.onListChanged(data.get(getAdapterPosition()).getAppPkgName(),
                    appSwitch.isChecked());
        }

    }
    public void setOnSettingsChangedListener(SettingsActivity onSettingsChangedListener) {
        this.onSettingsChangedListener = onSettingsChangedListener;
    }

    public AppListAdapter(Context context, List<AppInfo> data) {
        super();
        this.context = context;
        this.data = data;
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
            AppInfo appInfo = data.get(position);
            AppsViewHolder holder = (AppsViewHolder) viewHolder;
            holder.txtAppName.setText(appInfo.getAppName());
            holder.ivAppIcon.setImageDrawable(appInfo.getAppIcon());
            holder.appSwitch.setChecked(appInfo.isSelectedForStats());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnSettingsChangedListener {
        void onListChanged(String pkgName,
                                boolean isBlocked);
    }

    public void setFilter(List<AppInfo> countryModels){
        data = new ArrayList<>();
        data.addAll(countryModels);
        notifyDataSetChanged();
    }

}