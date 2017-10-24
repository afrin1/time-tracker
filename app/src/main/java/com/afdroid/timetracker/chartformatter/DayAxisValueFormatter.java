package com.afdroid.timetracker.chartformatter;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by afrin on 23/10/17.
 */

public class DayAxisValueFormatter implements IAxisValueFormatter {

    protected String[] mMonths = new String[]{
            "Facebook", "FB Messenger", "Whatsapp", "Instagram"
    };

    private BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        //Insert code here to return value from your custom array or based on some processing
        return mMonths[(int)value];

    }
}
