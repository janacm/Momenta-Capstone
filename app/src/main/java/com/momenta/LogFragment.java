package com.momenta;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    private EditText newActivity;
    private EditText activityHour;
    private EditText activityMinute;
    private EditText activityDeadline;
    private final Calendar deadlineCalendar = Calendar.getInstance();
    private boolean deadlineSet = false;
    private String sortString = Task.LAST_MODIFIED;
    private String orderString = ASC;
    private helperPreferences helperPreferences;
    private RecyclerView mRecyclerView;

    // Firebase instance variables
    private String directory = "tests";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Task, TaskViewHolder> mFirebaseAdapter;

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
        helperPreferences = new helperPreferences(getActivity());

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            directory = mFirebaseUser.getUid() + "/goals";
        }

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = buildAdapter(helperPreferences.getPreferences(DBHelper.COLUMN, Task.LAST_MODIFIED));

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AutoCompleteTextView actv;

        View view = inflater.inflate(R.layout.fragment_log, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_recycler_view);

        actv = (AutoCompleteTextView) view.findViewById(R.id.new_activity_edit_text);
        String[] suggestions = getResources().getStringArray(R.array.suggestions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, suggestions);
        actv.setAdapter(adapter);

        setLayoutManger();
        mRecyclerView.setAdapter(mFirebaseAdapter);

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

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.add(getString(R.string.sort_by));
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sortDialog();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
        }
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

                        mFirebaseAdapter = buildAdapter(sortString);
                        mFirebaseAdapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(mFirebaseAdapter);

                        setLayoutManger();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        RadioGroup sortRadioGroup = (RadioGroup)dialogView.findViewById(R.id.sort_dialog_group);
        RadioGroup orderRadioGroup = (RadioGroup)dialogView.findViewById(R.id.order_dialog_group);


        sortRadioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch ( checkedId ) {
                            case R.id.radio_button_name:
                                sortString = Task.NAME;
                                break;
                            case R.id.radio_button_date_created:
                                sortString = Task.DATE_CREATED;
                                break;
                            case R.id.radio_button_deadline:
                                sortString = Task.DEADLINE;
                                break;
                            case R.id.radio_button_last_modified:
                                sortString = Task.LAST_MODIFIED;
                                break;
                        }
                    }
                }
        );

        orderRadioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.radio_button_ascending:
                        orderString = ASC;
                        break;
                    case R.id.radio_button_descending:
                        orderString = DESC;
                }
            }
        });

        //Check current sort and order preference
        switch ( helperPreferences.getPreferences(DBHelper.COLUMN, Task.LAST_MODIFIED) ) {
            case Task.NAME:
                sortRadioGroup.check(R.id.radio_button_name);
                break;
            case Task.LAST_MODIFIED:
                sortRadioGroup.check(R.id.radio_button_last_modified);
                break;
            case Task.DATE_CREATED:
                sortRadioGroup.check(R.id.radio_button_date_created);
                break;
            case Task.DEADLINE:
                sortRadioGroup.check(R.id.radio_button_deadline);
                break;
        }
        switch ( helperPreferences.getPreferences(DBHelper.ORDER, ASC)) {
            case ASC:
                orderRadioGroup.check(R.id.radio_button_ascending);
                break;
            case DESC:
                orderRadioGroup.check(R.id.radio_button_descending);
                break;
        }

        sortDialogBuilder.create().show();
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

            //If no goal is chosen, default to 2 hours.
            if (totalMinutes == 0L) {
                totalMinutes = TimeUnit.MINUTES.convert(2, TimeUnit.HOURS);
            }

            //If no deadline is chosen, default to one week from now.
            if (!deadlineSet) {
                deadlineCalendar.setTimeInMillis( deadlineCalendar.getTimeInMillis()
                        + TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));
            }

            Task task = new Task(newActivity.getText().toString(), totalMinutes.intValue(),
                    deadlineCalendar, Calendar.getInstance().getTimeInMillis(), Calendar.getInstance());
            String id = mFirebaseDatabaseReference.child(directory).push().getKey();
            task.setId(id);
            mFirebaseDatabaseReference.child(directory + "/" + id).setValue(task);

            //Reset input fields
            newActivity.setText("");
            activityHour.setText("");
            activityMinute.setText("");
            activityDeadline.setText("");

            toast("Activity added!");
            deadlineSet = false;
        } else {
            toast(getContext().getString(R.string.toast_no_name_activity_added));
        }
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
                        //TODO: Removed toast, T'was causing the build to fail on older devices
//                        String status = status_obj.getString("_created");
//                        toast(status);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    /**
     * Helper method to build a recycler adapter
     * @param sortBy the field to sort the taskas by
     * @return an adapter
     */
    private FirebaseRecyclerAdapter<Task, TaskViewHolder> buildAdapter(String sortBy) {
        return new FirebaseRecyclerAdapter<Task, TaskViewHolder>(
                Task.class,
                R.layout.list_item,
                TaskViewHolder.class,
                mFirebaseDatabaseReference.child(directory).orderByChild(sortBy)) {

            @Override
            protected void populateViewHolder(TaskViewHolder viewHolder,
                                              Task task, int position) {
                viewHolder.name.setText(task.getName());
                viewHolder.timeSpent.setText(task.getFormattedTimeSpent());
                viewHolder.progressBar.setMax(task.getGoal());
                viewHolder.progressBar.setProgress(task.getTimeSpent());
            }

            @Override
            public void onBindViewHolder(TaskViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);

                final Task task = getItem(position);
                //Set onClick listener for each activity
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), TaskActivity.class);
                        intent.putExtra(DBHelper.ACTIVITY_ID, task.getId());
                        getContext().startActivity(intent);
                    }
                });
            }

        };
    }

    private void setLayoutManger() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderString = helperPreferences.getPreferences(DBHelper.ORDER, ASC);

        if ( orderString.equals(DESC) ) {
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            mRecyclerView.setLayoutManager(layoutManager);
        }

    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView timeSpent;
        public ProgressBar progressBar;

        public TaskViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.list_item_name);
            timeSpent = (TextView) itemView.findViewById(R.id.list_item_time_spent);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

}