package com.momenta;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joe on 2016-02-01.
 * For Momenta
 */
public class StatsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static StatsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        StatsFragment fragment = new StatsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        LineChart lineChart = (LineChart) view.findViewById(R.id.trends_linechart);
        List<Entry> lineEntries = new ArrayList<>();

        //Retrieve data from db
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        HashMap<String, Integer> lineMap = dbHelper.getTimeSpentByDay();
        final HashMap<Integer, String> xLabels = new HashMap<>();


        Calendar tempCal = Calendar.getInstance();

        //Get the date 6 days ago to get the past weeks data
        tempCal.setTimeInMillis( tempCal.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(6L, TimeUnit.DAYS) );
        int count = 0;

        //Check if the data from each day is in the map
        while ( tempCal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() ) {
            String date = formatDate( tempCal.getTime(), DBHelper.TIME_SPENT_DATE_FORMAT );
            if ( lineMap.get(date) == null ) {
                //If no time was logged on the day, set time to 0;
                lineMap.put(date, 0);
            }

            int yAxis = lineMap.get(date);
            lineEntries.add(new Entry(count, yAxis));

            String xAxisDate = formatDate(tempCal.getTime(), "EEE");
            xLabels.put(count, xAxisDate);

            //Incrementing day by one for next iteration
            tempCal.setTimeInMillis( tempCal.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS) );
            count++;
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Label");
        lineDataSet.setColor(getResources().getColor(R.color.colorPrimaryDark));
        lineDataSet.setDrawFilled(true);

        //Setting labels for X & Y axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinValue(0f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new AxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int count = (int) value;
                return xLabels.get(count);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });


        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisMinValue(0);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMinValue(0);
        rightAxis.setDrawGridLines(true);
        rightAxis.setDrawZeroLine(true);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);


        LineData lineData = new LineData(lineDataSet);
        lineData.setDrawValues(false);

        lineChart.setData(lineData);
        lineChart.setDescription("");
        lineChart.invalidate();


        /****************************Setting up the Pie Chart****************************/
        TextView pieTextView = (TextView) view.findViewById(R.id.trends_day_pie_textview);
        PieChart pieChart = (PieChart) view.findViewById(R.id.trends_piechart);
        pieChart.setRotationEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDescription("");

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();


        tempCal.setTimeInMillis( Calendar.getInstance().getTimeInMillis() );

        String pieDate = formatDate(tempCal.getTime(), "EEE, MMM d");
        pieTextView.setText(pieDate);

        pieDate = formatDate(tempCal.getTime(), DBHelper.TIME_SPENT_DATE_FORMAT);
        Log.d("Stats", "Getting data for " + pieDate);
        HashMap<String, Integer> pieMap = dbHelper.getTimeSpentForDay(pieDate);
        Log.d("Stats", "Size of map for " + pieDate + " is: " + pieMap.size());

        for ( String key : pieMap.keySet()) {
            Integer integer = pieMap.get(key);
            float value = integer.floatValue();
            pieEntries.add(new PieEntry(value,key));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Time spent");

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        colors.add( ContextCompat.getColor(getContext(), R.color.deep_purple) );

        pieDataSet.setColors(colors);

        //Initialize the Pie data
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.invalidate();

        return view;
    }

    private String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
