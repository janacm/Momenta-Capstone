package com.momenta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        LineChart chart = (LineChart) view.findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();

        //Retrieve data from db
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        HashMap<String, Integer> data = dbHelper.getDayData();
        final HashMap<Integer, String> xLabels = new HashMap<>();


        SimpleDateFormat sdf = new SimpleDateFormat(DBHelper.TIME_SPENT_DATE_FORMAT);

        Calendar tempCal = Calendar.getInstance();

        //Get the date 6 days ago to get the past weeks data
        tempCal.setTimeInMillis( tempCal.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(6L, TimeUnit.DAYS) );
        int count = 0;

        //Check if the data from each day is in the map
        while ( tempCal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() ) {
            String date = sdf.format( tempCal.getTime() );
            if ( data.get(date) == null ) {
                //If no time was logged on the day, set time to 0;
                data.put(date, 0);
            }

            int yAxis = data.get(date);
            entries.add(new Entry(count, yAxis));

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
            String xAxisDate = dayFormat.format(tempCal.getTime());
            xLabels.put(count, xAxisDate);


            //Incrementing day by one for next iteration
            tempCal.setTimeInMillis( tempCal.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS) );
            count++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(getResources().getColor(R.color.colorPrimaryDark));
        dataSet.setDrawFilled(true);

        //Setting labels for X & Y axis
        XAxis xAxis = chart.getXAxis();
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


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisMinValue(0);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setAxisMinValue(0);
        rightAxis.setDrawGridLines(true);
        rightAxis.setDrawZeroLine(true);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);


        LineData lineData = new LineData(dataSet);
        lineData.setDrawValues(false);

        chart.setData(lineData);
        chart.setDescription("");
        chart.invalidate();



        return view;
    }
}
