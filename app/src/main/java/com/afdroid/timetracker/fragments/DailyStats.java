package com.afdroid.timetracker.fragments;

/**
 * Created by afrin on 12/7/17.
 */

import android.app.usage.UsageStats;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class DailyStats extends Fragment {

    private BarChart barChart;

    private TextView startTime;
    private TextView endTime;

    private View rootView;

    private int FB = AppHelper.FB;
    private int WA = AppHelper.WA;
    private int FB_MSG = AppHelper.FB_MSG;
    private int INST = AppHelper.INST;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        barChart = (BarChart) rootView.findViewById(R.id.chart1);
        startTime = (TextView) rootView.findViewById(R.id.tvStartTime);
        endTime = (TextView) rootView.findViewById(R.id.tvEndTime);
        getDailyStats();
    }

    private void getDailyStats() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //start of week
//        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        //start of month
//        calendar.set(Calendar.DAY_OF_MONTH, 1);

        long millis = calendar.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date startresultdate = new Date(millis);
        Date endresultdate = new Date(System.currentTimeMillis());
        Map<String, UsageStats> lUsageStatsMap = AppHelper.getUsageStatsManager().queryAndAggregateUsageStats(
                millis,
                System.currentTimeMillis());

        float[] values = new float[4];

        StringBuilder lStringBuilder = new StringBuilder();
        lStringBuilder.append("DAILY STATS \n");
        lStringBuilder.append("start date: "+sdf.format(startresultdate)+" \n");
        lStringBuilder.append("end date: "+sdf.format(endresultdate)+" \n");

        startTime.setText("From "+sdf.format(startresultdate));
        endTime.setText("To "+sdf.format(endresultdate));

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


/*
        Log.d(AppHelper.TAG, "size = "+lUsageStatsMap.size());

        for(Map.Entry<String, UsageStats> entry : lUsageStatsMap.entrySet()) {
            String key = entry.getKey();
            UsageStats value = entry.getValue();

            Log.d(AppHelper.TAG, "Usage stats = "+value.getPackageName());
        }*/

    }

    private void setChart(float[] values) {

//        barChart.setOnChartValueSelectedListener( this);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // barChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(xAxisFormatter);

//        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = barChart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(24f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
//        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setAxisMaximum(24f);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        setData(4, 24, values);
    }

    private void setData(int count, float range, float[] values) {

        int start = 0;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = start; i <  count; i++) {
            float mult = (range + 1);
            float val =  values[i];

//            if (Math.random() * 100 < 25) {
//                yVals1.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
//            } else {
                yVals1.add(new BarEntry(i, val));
//            }
        }

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "App usage in Hours");

            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
//            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            barChart.setData(data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}


