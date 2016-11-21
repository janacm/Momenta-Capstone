package com.momenta_app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
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

    /******************************  Line Chart Fields  ******************************/
    // Holds the data for the week's line graph: <Date, TIME_SPENT>
    private HashMap<String, Integer> weekLineData;

    // Holds the index(order) of the each date in the graph
    private HashMap<Integer,String> dateIndex;

    // Holds the data for the month's line graph: <Date, TIME_SPENT>
    private HashMap<String, Integer> monthLineData;

    /****************************** Pie Chart fields  ******************************/
    private PieChart pieChart;
    private TextView pieTextView;

    // Entries to the pie chart
    private ArrayList<PieEntry> pieEntries;

    // Holds the data for the week's pie chart. <Date, List<Task>>
    private HashMap<String, ArrayList> pieData;
    /******************************  Firebase fields  ******************************/
    private DatabaseReference databaseReference;
    private String directory = "";
    private RecyclerView recyclerView;
    private StatsAdapter adapter;

    private boolean weekSelected = true;


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
        weekLineData = new HashMap<>();
        pieData = new HashMap<>();
        monthLineData = new HashMap<>();

        directory = FirebaseProvider.getUserPath();
        databaseReference = FirebaseProvider.getInstance().getReference();
        // Fetch the data from firebase
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        fetchWeekData();
        fetchMonthData();

        Spinner spinner = (Spinner)view.findViewById(R.id.stats_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==1) {
                    weekSelected = false;
                } else {
                    weekSelected = true;
                }
                drawLineGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        recyclerView = (RecyclerView)view.findViewById(R.id.legendRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StatsAdapter(new ArrayList());
        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * Sets up & draws the line graph with data from weekLineData.
     */
    private void drawLineGraph() {
        if (getView() == null) {
            return;
        }
        LineChart lineChart = (LineChart) getView().findViewById(R.id.trends_linechart);
        List<Entry> lineEntries = new ArrayList<>();
        pieTextView = (TextView) getView().findViewById(R.id.trends_day_pie_textview);
        HashMap<String, Integer> lineGraph;

        final HashMap<Integer, String> dayLabels = new HashMap<>();
        dateIndex = new HashMap<>();

        Calendar tempCal = Calendar.getInstance();

        // Get start date
        long days = 0L;
        if ( weekSelected ) {
            days = 6L;
            lineGraph = weekLineData;
        } else {
            days = 31L;
            lineGraph = monthLineData;
        }
        tempCal.setTimeInMillis( tempCal.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS) );
        int index = 0;

        // Check to see if no time was long on any of the days.
        // If no time was logged on any day, set TIME_SPENT=0 for that day;
        while ( tempCal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() ) {
            String date = SettingsActivity.formatDate( tempCal.getTime(), Constants.TIME_SPENT_DATE_FORMAT );
            if ( lineGraph.get(date) == null ) {
                //If no time was logged on the day, set time to 0;
                lineGraph.put(date, 0);
            }

            int yAxis = lineGraph.get(date);
            lineEntries.add(new Entry(index, yAxis));

            String xAxisDate = "";
            if (weekSelected) {
                xAxisDate = SettingsActivity.formatDate(tempCal.getTime(), "EEE");
            } else {
                xAxisDate = SettingsActivity.formatDate(tempCal.getTime(), "d/M");
            }
            dayLabels.put(index, xAxisDate);

            //Incrementing day by one for next iteration
            dateIndex.put(index, date);
            tempCal.setTimeInMillis( tempCal.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS) );
            index++;
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setColor( ContextCompat.getColor(getContext(), R.color.colorPrimary) );
        lineDataSet.setDrawFilled(true);
        lineDataSet.setHighLightColor( ContextCompat.getColor(getContext(), R.color.deep_purple) );
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        //Setting labels for X & Y axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinValue(0f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new AxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                return dayLabels.get(index);
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
        leftAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int minutes = (int) value;
                int hours = 0;

                if ( ! (minutes < 60) ) {
                    hours = minutes/60;
                    minutes = minutes% 60;
                }

                if ( hours >0 && minutes>0 ) {
                    return hours + "H " + minutes + "M";
                } else if ( hours ==0 && minutes>0 ) {
                    return minutes + "M";
                } else if ( hours >0 ) {
                    return hours + "H";
                } else {
                    return "";
                }
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

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
        lineChart.setPinchZoom(true);
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
        pieChart.setHoleRadius(55);
        pieChart.setDescription("");
        pieChart.getLegend().setEnabled(false);

        pieEntries = new ArrayList<>();
        Calendar tempCal = Calendar.getInstance();

        tempCal.setTimeInMillis( Calendar.getInstance().getTimeInMillis() );

        PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.time_in_minutes));
        pieDataSet.setDrawValues(false);
        pieDataSet.setSliceSpace(1f);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        colors.add( ContextCompat.getColor(getContext(), R.color.deep_purple) );

        pieDataSet.setColors(colors);

        //Initialize the Pie data
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(false);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);

        String pieDate = SettingsActivity.formatDate(tempCal.getTime(), Constants.TIME_SPENT_DATE_FORMAT);
        setPieDayData(pieDate);
    }

    private void setPieDayData(String dateString){
        Date date = SettingsActivity.parseStringToDate(dateString, Constants.TIME_SPENT_DATE_FORMAT);
        pieTextView.setText( SettingsActivity.formatDate(date, "EEE, MMM d") );

        Log.d("Stats", "Getting data for " + dateString);
        ArrayList<Task> pieDateList = pieData.get(dateString);
        pieEntries.clear();
        if ( pieDateList != null ) {
            Log.d("Stats", "Size of map for " + dateString + " is: " + pieDateList.size());
            for (Task t: pieDateList) {
                pieEntries.add(new PieEntry(t.getTimeSpent(), t.getFormattedTimeSpent().toLowerCase(), t));
            }
        }

        if (recyclerView != null && adapter != null) {
            ArrayList list = new ArrayList();
            int[] colors = pieChart.getData().getColors();
            int i = 0;
            for (PieEntry pieEntry : pieEntries) {
                Task t = (Task)pieEntry.getData();
                Pair<Integer, String> pair = new Pair(colors[i], t.getName());
                list.add(pair);
                i++;
            }
            adapter.setPairs(list);
            recyclerView.invalidate();
        }

        pieChart.setCenterText("");
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    /**
     * Retrieves the week's data from firebase
     */
    private void fetchWeekData(){
        Calendar endCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        long startTime = endCal.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(7L, TimeUnit.DAYS);
        startCal.setTimeInMillis(startTime);
        String endDate = SettingsActivity.formatDate( startCal.getTime(), Constants.TIME_SPENT_DATE_FORMAT );

        databaseReference.child(directory).endAt(endDate).addListenerForSingleValueEvent(
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
                            weekLineData.put(date.getKey(), totalTime);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }

    /**
     * Retrieves the month's data from firebase
     */
    private void fetchMonthData() {
        Calendar endCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        long startTime = endCal.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(31L, TimeUnit.DAYS);
        startCal.setTimeInMillis(startTime);
        String endDate = SettingsActivity.formatDate( startCal.getTime(), Constants.TIME_SPENT_DATE_FORMAT );

        databaseReference.child(directory).endAt(endDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot timeDir = dataSnapshot.child(Task.TIME_SPENT);
                for (DataSnapshot date : timeDir.getChildren()) {
                    ArrayList<Task> data = new ArrayList<Task>();
                    // Variable to hold the sum of time spent, for the date.
                    int totalTime = 0;
                    for ( DataSnapshot id : date.getChildren() ) {
                        Task t =  new Task();
                        t.setId(id.getKey());
                        t.setTimeSpent( id.child(Task.TIME_SPENT).getValue(Integer.class) );
                        data.add(t);
                        totalTime += t.getTimeSpent();
                    }
                    monthLineData.put(date.getKey(), totalTime);
                    pieData.put(date.getKey(), data);
                }

                DataSnapshot goalDir = dataSnapshot.child("goals");
                for (String date : pieData.keySet()) {
                    ArrayList<Task> list = (ArrayList<Task>) pieData.get(date);
                    for ( Task t : list ) {
                        String name = (String)goalDir.child(t.getId()).child(Task.NAME).getValue();
                        t.setName(name);
                    }
                }
                drawLineGraph();
                drawPieChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e instanceof PieEntry) {
            Task t = (Task)e.getData();
            String name = t.getName();
            if (name == null) {
                name = "";
            }
            SpannableString s = new SpannableString(name);
            pieChart.setCenterText(s);
        } else {
            Float indexInFloat = e.getX();
            Integer index = indexInFloat.intValue();

            String indexDate = dateIndex.get(index);
            setPieDayData( indexDate );
        }
    }

    @Override
    public void onNothingSelected() {
    }
}
