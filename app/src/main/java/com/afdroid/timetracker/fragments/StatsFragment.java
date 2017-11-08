package com.afdroid.timetracker.fragments;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afdroid.timetracker.R;
import com.afdroid.timetracker.Utils.AppHelper;
import com.afdroid.timetracker.chartformatter.DayAxisValueFormatter;
import com.afdroid.timetracker.preferences.TimeTrackerPrefHandler;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {
    private BarChart barChart;

    private TextView startTime;
    private TextView endTime;

    private View rootView;

    private final int DAILY = AppHelper.DAILY_STATS;
    private final int WEEKLY = AppHelper.WEEKLY_STATS;
    private final int MONTHLY = AppHelper.MONTHLY_STATS;

    private int selectedPeriod = 0;
    private List<String> appList = null;
    private List<String> appNameList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        Bundle args = getArguments();
        selectedPeriod = args.getInt("period", 0);
        barChart = (BarChart) rootView.findViewById(R.id.chart1);
        barChart.setNoDataText(getResources().getString(R.string.no_data));
        startTime = (TextView) rootView.findViewById(R.id.tvStartTime);
        endTime = (TextView) rootView.findViewById(R.id.tvEndTime);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String serialized = TimeTrackerPrefHandler.INSTANCE.getPkgList
                (getActivity().getApplicationContext());
        if (serialized != null) {
            appList = null;
            appNameList = null;
            appList = new LinkedList<String>(Arrays.asList(TextUtils.
                    split(serialized, ",")));
            appNameList = new LinkedList<String>();
        }
        getStatsInfo();
    }

    private void getStatsInfo() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long millis = 0;

        switch (selectedPeriod) {
            case DAILY:
                millis = calendar.getTimeInMillis();
                break;
            case WEEKLY:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                millis = calendar.getTimeInMillis();
                break;
            case MONTHLY:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                millis = calendar.getTimeInMillis();
                break;
            default:
                break;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date startresultdate = new Date(millis);
        Date endresultdate = new Date(System.currentTimeMillis());
        Map<String, UsageStats> lUsageStatsMap = AppHelper.getUsageStatsManager().
                queryAndAggregateUsageStats(
                millis,
                System.currentTimeMillis());

        startTime.setText("From "+sdf.format(startresultdate));
        endTime.setText("To "+sdf.format(endresultdate));

        if (appList != null) {
            PackageManager packageManager= getActivity().getApplicationContext().getPackageManager();
            float[] values = new float[appList.size()];

            for (int i = 0; i < appList.size(); i++) {
                String appPkg = appList.get(i);
                try {
                    appNameList.add((String) packageManager.getApplicationLabel(packageManager.
                            getApplicationInfo(appPkg, PackageManager.GET_META_DATA)));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (lUsageStatsMap.containsKey(appPkg)) {
                    if (selectedPeriod == DAILY) {
                        values[i] = AppHelper.getMinutes(lUsageStatsMap.get(appPkg).
                                getTotalTimeInForeground());

                    } else {
                        values[i] = AppHelper.getHours(lUsageStatsMap.get(appPkg).
                                getTotalTimeInForeground());
                    }
                } else {
                    //if device does not contain the app usage data
                    values[i] = 0.0f;
                }
            }
            saveAppPreference();
            if (values.length != 0) {
                setChart(values);
            }
        }
    }

    private void saveAppPreference() {
        TimeTrackerPrefHandler.INSTANCE.savePkgList
                (TextUtils.join(",", appList), getActivity().getApplicationContext());
    }

    private void setChart(float[] values) {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
//        barChart.setMaxVisibleValueCount(60);
        barChart.setVisibleXRangeMaximum(6);
        barChart.moveViewToX(10);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // barChart.setDrawYLabels(false);
        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart, appNameList);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(appList.size());
        xAxis.setValueFormatter(xAxisFormatter);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(10, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
//        l.setXEntrySpace(4f);

        setData(values);
    }

    private void setData(float[] values) {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i <  appList.size(); i++) {
            float val =  values[i];
            yVals1.add(new BarEntry(i, val));
        }

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        }
        else {
            if (selectedPeriod == DAILY) {
                set1 = new BarDataSet(yVals1, "App usage in Minutes");
            }
            else {
                set1 = new BarDataSet(yVals1, "App usage in Hours");
            }
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.2f);
            barChart.setData(data);
            barChart.setVisibleXRangeMaximum(4.0f);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
