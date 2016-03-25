package com.momenta;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Joe on 2016-01-31.
 * For Momenta
 */

public class LogFragment extends Fragment implements View.OnClickListener {
    public static final String ARG_PAGE = "ARG_PAGE";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText newActivity;
    private EditText activityTime;
    private EditText activityDeadline;
    private final Calendar deadlineCalendar = Calendar.getInstance();

//    private int mPage;

    public static LogFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        LogFragment fragment = new LogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ActivitiesAdapter(this.getContext());
        mRecyclerView.setAdapter(mAdapter);

        ImageButton button = (ImageButton) view.findViewById(R.id.new_activity_add_button);
        button.setOnClickListener(this);

        newActivity = (EditText) view.findViewById(R.id.new_activity_edit_text);

        activityTime = (EditText) view.findViewById(R.id.new_activity_goal_edit_text);
        activityTime.setOnClickListener(this);

        activityDeadline = (EditText) view.findViewById(R.id.new_activity_deadline_edit_text);
        activityDeadline.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_activity_add_button:
                addActivity();
                httpRequest();
                return;
            case R.id.new_activity_goal_edit_text:
                inputGoal();
                return;
            case R.id.new_activity_deadline_edit_text:
                inputDeadline();
                break;
        }
    }

    /**
     * Handler method for the add activity button on log fragment.
     */
    private void addActivity() {
        //If the text box is empty do nothing.
        if (!newActivity.getText().toString().trim().isEmpty()) {
            String timeFieldValue = Task.stripNonDigits(activityTime.getText().toString());
            int timeInMinutes = Task.convertHourMinuteToMinute(timeFieldValue);
            Task task = new Task(newActivity.getText().toString(), timeInMinutes, deadlineCalendar);
            DBHelper.getInstance(getContext()).insertTask(task);

            //Reset input fields
            newActivity.setText("");
            activityTime.setText("");
            activityDeadline.setText("");

            ((ActivitiesAdapter) mAdapter).retrieveTasks();
            toast(getContext().getString(R.string.toast_activity_added));
        } else {
            toast(getContext().getString(R.string.toast_no_name_activity_added));
        }
    }

    /**
     * Handler method for the Goal edit text on the log fragment
     * Used to input goal in time for the edit text.
     */
    private void inputGoal() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View alertView = inflater.inflate(R.layout.activity_alert_dialog, null);
        TimeDialogBuilder timeDialogBuilder = new TimeDialogBuilder(this, alertView,
                newActivity.getText().toString().trim(), activityTime);
        AlertDialog alertDialog = timeDialogBuilder.getAlertDialog();
        alertDialog.show();
    }

    /**
     * Helper method to input the Deadline/Due date if an activity.
     */
    private void inputDeadline() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                deadlineCalendar.set(year, monthOfYear, dayOfMonth);
                activityDeadline.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(deadlineCalendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        dialog.show();
    }

    /**
     * Convience method for toasting messages to the user
     * Toast message is set tot LENGTH_LONG.
     *
     * @param toToast the string to be displayed to the user
     */
    private void toast(String toToast) {
        Toast.makeText(getContext(), toToast, Toast.LENGTH_LONG).show();
    }

    private void httpRequest() {
        String ping_url = "http://momenta.herokuapp.com/people";
        new HttpTask(ping_url, "GET") {
            @Override
            protected void onPostExecute(JSONObject json) {
                super.onPostExecute(json);
                try {
                    if (json != null) {
                        JSONArray ping_result = json.getJSONArray("_items");
                        JSONObject status_obj = ping_result.getJSONObject(0);
                        String status = status_obj.getString("_created");
                        toast(status);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

}