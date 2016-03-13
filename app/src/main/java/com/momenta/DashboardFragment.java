package com.momenta;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

/**
 * Created by Joe on 2016-02-01.
 * For Momenta
 */
public class DashboardFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private View activityView;
    private NumberPicker numberPicker;

    public static DashboardFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        DashboardFragment fragment = new DashboardFragment();
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
        activityView= inflater.inflate(R.layout.fragment_dashboard, container, false);
        return activityView;
    }

//    public void sendBroadcast(View v){
//        Log.d("Dashboard", "Setting Alarm.");
//
//        int time = numberPicker.getValue() * 1000;
//        Log.d("Dashboard", "Setting Alarm..." + time);
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis( cal.getTimeInMillis() + time );
//
//        Intent intentAlarm = new Intent(getContext(), Reciever.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        Log.d("Dashboard", "Alarm Set.");
//
//
//    }
}
