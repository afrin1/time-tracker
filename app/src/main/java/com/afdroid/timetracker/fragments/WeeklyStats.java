package com.afdroid.timetracker.fragments;

/**
 * Created by afrin on 12/7/17.
 */
import android.app.usage.UsageStats;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afdroid.timetracker.R;
import com.afdroid.timetracker.Utils.AppHelper;
import com.afdroid.timetracker.chartformatter.DayAxisValueFormatter;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class WeeklyStats extends Fragment {

    protected BarChart mChart;

    View RootView;

    public int FB = AppHelper.FB;
    public int WA = AppHelper.WA;
    public int FB_MSG = AppHelper.FB_MSG;
    public int INST = AppHelper.INST;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_weekly, container, false);
        return RootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mChart = (BarChart) RootView.findViewById(R.id.chart1);
        getWeeklyStats();
    }

    private void getWeeklyStats() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //start of week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        long millis = calendar.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date startresultdate = new Date(millis);
        Date endresultdate = new Date(System.currentTimeMillis());
        Map<String, UsageStats> lUsageStatsMap = AppHelper.getUsageStatsManager().queryAndAggregateUsageStats(
                millis,
                System.currentTimeMillis());

        float[] values = new float[4];

        StringBuilder lStringBuilder = new StringBuilder();
        lStringBuilder.append("WEEKLY STATS \n");
        lStringBuilder.append("start date: "+sdf.format(startresultdate)+" \n");
        lStringBuilder.append("end date: "+sdf.format(endresultdate)+" \n");

        Log.d(AppHelper.TAG, lStringBuilder.toString());

        if (lUsageStatsMap.containsKey(AppHelper.FB_PKG_NAME)) {
            values[FB] = AppHelper.getHours(lUsageStatsMap.get(AppHelper.FB_PKG_NAME).
                    getTotalTimeInForeground()) ;
//            long seconds = fb / 1000;
//            long hours = seconds / 3600;
//            fb = hours;

            Log.d(AppHelper.TAG, "FB hours = "+values[FB]);
        }

        if (lUsageStatsMap.containsKey(AppHelper.FB_MSG_PKG_NAME)) {
            values[FB_MSG] = AppHelper.getHours(lUsageStatsMap.get(AppHelper.FB_MSG_PKG_NAME).
                    getTotalTimeInForeground()) ;
            Log.d(AppHelper.TAG, "FB_MSG_PKG_NAME hours = "+values[FB_MSG]);
        }

        if (lUsageStatsMap.containsKey(AppHelper.WHATSAPP_PKG_NAME)) {
            values[WA] = AppHelper.getHours(lUsageStatsMap.get(AppHelper.WHATSAPP_PKG_NAME).
                    getTotalTimeInForeground()) ;
            Log.d(AppHelper.TAG, "WHATSAPP_PKG_NAME hours = "+values[WA]);
        }

        if (lUsageStatsMap.containsKey(AppHelper.INSTAGRAM_PKG_NAME)) {
            values[INST] = AppHelper.getHours(lUsageStatsMap.get(AppHelper.INSTAGRAM_PKG_NAME).
                    getTotalTimeInForeground()) ;
            Log.d(AppHelper.TAG, "INSTAGRAM hours = "+values[INST]);
        }

        setChart(values);

    }

    private void setChart(float[] values) {

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(xAxisFormatter);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(168f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setAxisMaximum(168f);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        setData(4, values);
    }

    private void setData(int count, float[] values) {

        int start = 0;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = start; i <  count; i++) {
            float val =  values[i];
            yVals1.add(new BarEntry(i, val));
        }

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "App usage in Hours");

            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }

}


