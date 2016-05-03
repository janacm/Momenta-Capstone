package com.momenta;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joe on 2016-01-31.
 * For Momenta
 */

public class LogFragment extends Fragment implements View.OnClickListener {
    public static final String ARG_PAGE = "ARG_PAGE";
    private ActivitiesAdapter mAdapter;
    private EditText newActivity;
    private EditText activityHour;
    private EditText activityMinute;
    private EditText activityDeadline;
    private final Calendar deadlineCalendar = Calendar.getInstance();
    private boolean deadlineSet = false;
    private String sortString;
    private String orderString;
    private HelperPreferences helperPreferences;
    private ImageView image;
    private TextView sortColumnnName;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ActivitiesAdapter(this.getContext(), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        ImageButton addButton = (ImageButton)view.findViewById(R.id.new_activity_add_button);
        addButton.setOnClickListener(this);

        newActivity = (EditText) view.findViewById(R.id.new_activity_edit_text);

        activityHour = (EditText) view.findViewById(R.id.new_activity_hour_edit_text);
        activityMinute = (EditText) view.findViewById(R.id.new_activity_minute_edit_text);

        //Add watcher to move focus to minute text view
        activityHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2) {
                    activityMinute.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activityDeadline = (EditText) view.findViewById(R.id.new_activity_deadline_edit_text);
        activityDeadline.setOnClickListener(this);

        ImageButton sortButton = (ImageButton) view.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(this);

        helperPreferences = new HelperPreferences(getActivity());

        image = (ImageView)view.findViewById(R.id.asc_desc_image);
        sortColumnnName = (TextView)view.findViewById(R.id.asc_desc_name);
        sortColumnnName.setText( getSortLabel() );
        if ( helperPreferences.getPreferences(DBHelper.ORDER, DBHelper.DESC).equals(DBHelper.ASC) ) {
            image.setBackgroundResource(R.drawable.ic_up_arrow);
        } else {
            image.setBackgroundResource(R.drawable.ic_down_arrow);
        }

        LinearLayout sortLayout = (LinearLayout)view.findViewById(R.id.sort_order_layout);
        sortLayout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.retrieveTasks();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_activity_add_button:
                addActivity();
                httpRequest();
                return;
            case R.id.new_activity_deadline_edit_text:
                inputDeadline();
                break;
            case R.id.sort_button:
                sortDialog();
                break;
            case R.id.sort_order_layout:
                orderOnClick();
                break;
        }
    }

    private void orderOnClick() {
        if ( helperPreferences.getPreferences(DBHelper.ORDER, DBHelper.DESC).equals(DBHelper.ASC) ) {
            helperPreferences.savePreferences(DBHelper.ORDER, DBHelper.DESC);
            image.setBackgroundResource(R.drawable.ic_down_arrow);
        } else {
            helperPreferences.savePreferences(DBHelper.ORDER, DBHelper.ASC);
            image.setBackgroundResource(R.drawable.ic_up_arrow);
        }
        mAdapter.retrieveTasks();
    }

    /**
     * On click method for the sort button
     */
    private void sortDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        AlertDialog.Builder sortDialogBuilder = new AlertDialog.Builder(getContext());

        View dialogView =  inflater.inflate(R.layout.sort_dialog, null);
        sortDialogBuilder.setView(dialogView)
                .setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helperPreferences.savePreferences(DBHelper.COLUMN, sortString);
                        helperPreferences.savePreferences(DBHelper.ORDER, orderString);
                        sortColumnnName.setText(getSortLabel());
                        if ( orderString.equals(DBHelper.ASC) ) {
                            image.setBackgroundResource(R.drawable.ic_up_arrow);
                        } else {
                            image.setBackgroundResource(R.drawable.ic_down_arrow);
                        }
                        mAdapter.retrieveTasks();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Color.DKGRAY
                        , Color.rgb(33, 150, 243),
                }
        );

        RadioGroup radioGroup = (RadioGroup)dialogView.findViewById(R.id.sort_dialog_group);

        //Setup for radio buttons to filter.
        final AppCompatRadioButton radioButtonName = (AppCompatRadioButton)dialogView
                .findViewById(R.id.radio_button_name);
        radioButtonName.setSupportButtonTintList(colorStateList);

        AppCompatRadioButton radioButtonLastModified = (AppCompatRadioButton)dialogView
                .findViewById(R.id.radio_button_last_modified);
        radioButtonLastModified.setSupportButtonTintList(colorStateList);

        AppCompatRadioButton radioButtonCreated = (AppCompatRadioButton)dialogView
                .findViewById(R.id.radio_button_date_created);
        radioButtonCreated.setSupportButtonTintList(colorStateList);

        AppCompatRadioButton radioButtonDeadline = (AppCompatRadioButton)dialogView
                .findViewById(R.id.radio_button_deadline);
        radioButtonDeadline.setSupportButtonTintList(colorStateList);

        radioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch ( checkedId ) {
                            case R.id.radio_button_name:
                                sortString = DBHelper.ACTIVITY_NAME;
                                orderString = DBHelper.ASC;
                                break;
                            case R.id.radio_button_date_created:
                                sortString = DBHelper.ACTIVITY_DATE_CREATED;
                                orderString = DBHelper.DESC;
                                break;
                            case R.id.radio_button_deadline:
                                sortString = DBHelper.ACTIVITY_DEADLINE;
                                orderString = DBHelper.DESC;
                                break;
                            case R.id.radio_button_last_modified:
                                sortString = DBHelper.ACTIVITY_LAST_MODIFIED;
                                orderString = DBHelper.DESC;
                                break;
                        }
                    }
                }
        );

        switch ( helperPreferences.getPreferences(DBHelper.COLUMN, DBHelper.ACTIVITY_LAST_MODIFIED) ) {
            case DBHelper.ACTIVITY_NAME:
                radioGroup.check(R.id.radio_button_name);
                break;
            case DBHelper.ACTIVITY_LAST_MODIFIED:
                radioGroup.check(R.id.radio_button_last_modified);
                break;
            case DBHelper.ACTIVITY_DATE_CREATED:
                radioGroup.check(R.id.radio_button_date_created);
                break;
            case DBHelper.ACTIVITY_DEADLINE:
                radioGroup.check(R.id.radio_button_deadline);
                break;
        }

        sortDialogBuilder.create().show();
    }

    private String getSortLabel() {
        String columnName = "";
        switch ( helperPreferences.getPreferences(DBHelper.COLUMN, DBHelper.ACTIVITY_LAST_MODIFIED) ) {
            case DBHelper.ACTIVITY_NAME:
                columnName = getString(R.string.sort_dialog_name);
                break;
            case DBHelper.ACTIVITY_LAST_MODIFIED:
                columnName = getString(R.string.sort_dialog_last_modified);
                break;
            case DBHelper.ACTIVITY_DATE_CREATED:
                columnName = getString(R.string.sort_dialog_date_created);
                break;
            case DBHelper.ACTIVITY_DEADLINE:
                columnName = getString(R.string.sort_dialog_dealine);
                break;
        }
        return columnName;
    }

    /**
     * Handler method for the add activity button on log fragment.
     */
    private void addActivity() {
        //If the text box is empty do nothing.
        if (!newActivity.getText().toString().trim().isEmpty()) {
            long hourField = Long.valueOf( "0" + activityHour.getText().toString() );
            long minuteField = Long.valueOf( "0" + activityMinute.getText().toString());
            Long totalMinutes = TimeUnit.MINUTES.convert(hourField, TimeUnit.HOURS)
                    + minuteField;

            //If no goal is chosen, default to 2 hours. TODO Make default a preference
            if (totalMinutes == 0l) {
                totalMinutes = TimeUnit.MINUTES.convert(2, TimeUnit.HOURS);
            }

            //If no deadline is chosen, default to one week from now. TODO Make default a preference
            if (!deadlineSet) {
                deadlineCalendar.setTimeInMillis( deadlineCalendar.getTimeInMillis()
                        + TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));
            }

            Task task = new Task(newActivity.getText().toString(), totalMinutes.intValue(),
                    deadlineCalendar, Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
            DBHelper.getInstance(getContext()).insertTask(task);

            //Reset input fields
            newActivity.setText("");
            activityHour.setText("");
            activityMinute.setText("");
            activityDeadline.setText("");

            mAdapter.retrieveTasks();
            toast("Activity added!");
            deadlineSet = false;
        } else {
            toast(getContext().getString(R.string.toast_no_name_activity_added));
        }
    }

    /**
     * Helper method to input the Deadline/Due date if an activity.
     */
    private void inputDeadline() {//TODO Date Format string literal
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                deadlineCalendar.set(year, monthOfYear, dayOfMonth);
                activityDeadline.setText(Task.getDateFormat(deadlineCalendar));
                deadlineSet = true;
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