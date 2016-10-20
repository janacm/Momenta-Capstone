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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
public class StatsFragment extends Fragment implements OnChartValueSelectedListener {
    public static final String TAG = "StatsFragment";

    /******************  Line Chart Fields  ******************/
    // Holds the data for the line graph: <Date, TIME_SPENT>
    private HashMap<String, Integer> lineGraphData;

    // Maps the integers to dates: <Count, Date>
    private HashMap<Integer,String> countMapping;

    /****************** Pie Chart fields  ******************/
    private PieChart pieChart;
    private TextView pieTextView;

    // Entries to the pie chart
    private ArrayList<PieEntry> pieEntries;

    // Holds the data for the pie chart. <Date, List<Task>>
    private HashMap<String, ArrayList> pieGraphData;

    /******************  Firebase fields  ******************/
    private DatabaseReference databaseReference;
    private String directory = "";


    public static StatsFragment newInstance(int page) {
        Bundle args = new Bundle();
        StatsFragment fragment = new StatsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing the graph data
        lineGraphData = new HashMap<>();
        pieGraphData = new HashMap<>();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            directory = mFirebaseUser.getUid();
        }
        databaseReference = FirebaseProvider.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        databaseReference.child(directory).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot wholeSnap) {

                        DataSnapshot timeDir = wholeSnap.child(Task.TIME_SPENT);
                        //Iterate through to get all dates.
                        for ( DataSnapshot date: timeDir.getChildren() ) {
                            // Temp list to hold the tasks for the day
                            ArrayList<Task> pieDataList = new ArrayList<Task>();
                            // Variable to hold the sum of time spent, for the date.
                            int totalTime = 0;
                            for ( DataSnapshot id : date.getChildren() ) {
                                Task t =  new Task();
                                t.setId(id.getKey());
                                t.setTimeSpent( id.child(Task.TIME_SPENT).getValue(Integer.class) );
                                pieDataList.add(t);
                                totalTime += t.getTimeSpent();
                            }
                            // Save the list against the date.
                            pieGraphData.put(date.getKey(), pieDataList);
                            // Save date along with total time spent for the day.
                            lineGraphData.put(date.getKey(), totalTime);
                        }
                        drawLineGraph();

                        DataSnapshot goalDir = wholeSnap.child("goals");
                        for (String date : pieGraphData.keySet()) {
                            ArrayList<Task> list = (ArrayList<Task>) pieGraphData.get(date);
                            for ( Task t : list ) {
                                String name = (String)goalDir.child(t.getId()).child(Task.NAME).getValue();
                                t.setName(name);
                            }
                        }

                        drawPieChart();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        return view;
    }

    /**
     * Sets up & draws the line graph with data from lineGraphData.
     */
    private void drawLineGraph() {
        if (getView() == null) {
            return;
        }

        LineChart lineChart = (LineChart) getView().findViewById(R.id.trends_linechart);
        List<Entry> lineEntries = new ArrayList<>();
        pieTextView = (TextView) getView().findViewById(R.id.trends_day_pie_textview);

        final HashMap<Integer, String> weekdayLabels = new HashMap<>();
        countMapping = new HashMap<>();

        Calendar tempCal = Calendar.getInstance();

        // Get the date 6 days ago to get the past weeks data
        tempCal.setTimeInMillis( tempCal.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(6L, TimeUnit.DAYS) );
        int count = 0;

        // Check to see if time was logged on each dayt for the past 6 days.
        // If no time was logged for any day, set TIME_SPENT=0 for that day;
        while ( tempCal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() ) {
            String date = SettingsActivity.formatDate( tempCal.getTime(), Constants.TIME_SPENT_DATE_FORMAT );
            if ( lineGraphData.get(date) == null ) {
                //If no time was logged on the day, set time to 0;
                lineGraphData.put(date, 0);
                Log.w(TAG, "Nothing for date " + date);
            }

            int yAxis = lineGraphData.get(date);
            lineEntries.add(new Entry(count, yAxis));

            String xAxisDate = SettingsActivity.formatDate(tempCal.getTime(), "EEE");
            weekdayLabels.put(count, xAxisDate);

            //Incrementing day by one for next iteration
            countMapping.put(count, date);
            tempCal.setTimeInMillis( tempCal.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS) );
            count++;
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Label");
        lineDataSet.setColor( ContextCompat.getColor(getContext(), R.color.colorPrimary) );
        lineDataSet.setDrawFilled(true);
        lineDataSet.setHighLightColor( ContextCompat.getColor(getContext(), R.color.deep_purple) );

        //Setting labels for X & Y axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinValue(0f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new AxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int count = (int) value;
                return weekdayLabels.get(count);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });


        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
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
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.invalidate();
    }

    /**
     * Sets up and draws the pie chart
     */
    private void drawPieChart() {
        if (getView() == null) {
            return;
        }
        pieChart = (PieChart) getView().findViewById(R.id.trends_piechart);
        pieChart.setRotationEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDescription("");

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        pieEntries = new ArrayList<>();
        Calendar tempCal = Calendar.getInstance();

        tempCal.setTimeInMillis( Calendar.getInstance().getTimeInMillis() );

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

        String pieDate = SettingsActivity.formatDate(tempCal.getTime(), Constants.TIME_SPENT_DATE_FORMAT);
        setPieDayData(pieDate);
    }

    private void setPieDayData(String dateString){
        Date date = SettingsActivity.parseStringToDate(dateString, Constants.TIME_SPENT_DATE_FORMAT);
        pieTextView.setText( SettingsActivity.formatDate(date, "EEE, MMM d") );

        Log.d("Stats", "Getting data for " + dateString);
        ArrayList<Task> pieDateList = pieGraphData.get(dateString);
        pieEntries.clear();
        if ( pieDateList != null ) {
            Log.d("Stats", "Size of map for " + dateString + " is: " + pieDateList.size());
            for (Task t: pieDateList) {
                pieEntries.add(new PieEntry(t.getTimeSpent(), t.getName()));
            }
        }

        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Float countInFloat = e.getX();
        Integer count = countInFloat.intValue();

        String countDate = countMapping.get(count);
        setPieDayData( countDate );
    }

    @Override
    public void onNothingSelected() {
    }
}
