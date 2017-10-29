package com.afdroid.timetracker.chartformatter;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * Created by afrin on 23/10/17.
 */

public class DayAxisValueFormatter implements IAxisValueFormatter {

    protected String[] appList;

    private BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart, List<String> applist) {
        this.chart = chart;
        appList = new String[applist.size()];
        appList = applist.toArray(appList);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        //Insert code here to return value from your custom array or based on some processing
        return appList[(int)value];

    }
}
