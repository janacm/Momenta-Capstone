package com.momenta;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TaskActivity";
    private EditText activityName;
    private TextView activityDeadline;
    private TextView activityGoal;
    private TextView activityTimeSpent;
    private Task task;


    private  Integer goalHours = 2;
    private  Integer goalMins = 30;
    private  Integer timeSpentHours = 0;
    private  Integer timeSpentMins = 0;

    //Firebase instances
    private DatabaseReference mFirebaseDatabaseReference;
    private String directory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            directory = mFirebaseUser.getUid() + "/goals";
        }
        mFirebaseDatabaseReference = FirebaseProvider.getInstance().getReference();
        task = new Task();

        //Get the id of the activity and retrieve it from the DB
        Bundle bundle = getIntent().getExtras();
        final String id = (String) bundle.get(Task.ID);

        mFirebaseDatabaseReference.child(directory + "/" + id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        task.setId( (String)dataSnapshot.child("id").getValue() );
                        task.setName( (String)dataSnapshot.child("name").getValue() );
                        task.setGoal( dataSnapshot.child("goal").getValue(Integer.class) );
                        task.setDeadline( (Long)dataSnapshot.child("deadline").getValue() );
                        task.setDateCreated( (Long)dataSnapshot.child("dateCreated").getValue() );
                        task.setLastModified( (Long)dataSnapshot.child("lastModified").getValue() );
                        task.setTimeSpent( dataSnapshot.child("timeSpent").getValue(Integer.class) );
                        task.setPriority( (String)dataSnapshot.child("priority").getValue() );
                        initializeFields();
//                        initializePieChart();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        activityName = (EditText)findViewById(R.id.task_name_edit_text);
        activityGoal = (TextView)findViewById(R.id.task_goal_value);
        activityTimeSpent = (TextView)findViewById(R.id.task_timespent_value);
        activityDeadline = (TextView) findViewById(R.id.task_deadline_value);
    }

    private void initializeFields() {
        activityName.setText( task.getName() );
        activityDeadline.setText( task.getFormattedDeadline() );

        //Set the goal textView
        goalMins = task.getGoal();
        goalHours = 0;
        if ( goalMins >= 60 ) {
            goalHours = goalMins/60;
            goalMins = goalMins%60;
        }
        String goalText = timeSetText(goalHours, goalMins);
        activityGoal.setText(goalText);

        //Set the timeSpent textView
        timeSpentMins = task.getTimeSpent();
        timeSpentHours = 0;
        if ( timeSpentMins >= 60 ) {
            timeSpentHours = timeSpentMins/60;
            timeSpentMins = timeSpentMins%60;
        }
        String spentText = timeSetText(timeSpentHours, timeSpentMins);
        activityTimeSpent.setText(spentText);

        Spinner spinner = (Spinner)findViewById(R.id.task_priority_spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(spinnerPosition(task.getPriorityValue()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_done:
                save();
                break;
            case R.id.action_delete:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_string) + " "
                + activityName.getText().toString() + "?")
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseDatabaseReference.child(directory + "/" + task.getId()).removeValue();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.create().show();
    }

    /**
     * On click method for the goal relative layout
     * @param v the view that was clicked
     */
    public void goalOnClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        LayoutInflater inflater = this.getLayoutInflater();

        final View alertView = inflater.inflate(R.layout.dialog_timeentry, null);
        final EditText editTextHours  = (EditText) alertView.findViewById(R.id.dialog_hour_edittext);
        editTextHours.setText(Integer.toString(goalHours));

        final EditText editTextMinutes = (EditText) alertView.findViewById(R.id.dialog_minute_edittext);
        editTextMinutes.setText(Integer.toString(goalMins));

        final TextView dialogTitle = (TextView) alertView.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.timeentry_dialog_goal_title);
        builder.setView(alertView);


        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goalHours = Integer.valueOf(editTextHours.getText().toString());
                goalMins = Integer.valueOf(editTextMinutes.getText().toString());
                activityGoal.setText(timeSetText(goalHours, goalMins));
            }
        }).setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * On click method for the deadline layout
     * @param v the deadline layout
     */
    public void deadlineOnClick(View v){
        Calendar cal = task.getDeadlineValue();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar temp = Calendar.getInstance();
                temp.set(year, monthOfYear, dayOfMonth);
                task.setDeadlineValue(temp);
                activityDeadline.setText( Task.getDateFormat(temp) );
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dialog.setTitle("");
        dialog.show();
    }

    /**
     * On click method for the timeSpent layout
     * @param view view being clicked
     */
    public void timeSpentOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        LayoutInflater inflater = this.getLayoutInflater();

        final View alertView = inflater.inflate(R.layout.dialog_timeentry, null);
        final EditText editTextHours  = (EditText) alertView.findViewById(R.id.dialog_hour_edittext);
        editTextHours.setText(Integer.toString(timeSpentHours));

        final EditText editTextMinutes = (EditText) alertView.findViewById(R.id.dialog_minute_edittext);
        editTextMinutes.setText(Integer.toString(timeSpentMins));

        final TextView dialogTitle = (TextView) alertView.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.timeentry_dialog_timespent_title);

        builder.setView(alertView);


        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timeSpentHours = Integer.valueOf(editTextHours.getText().toString());
                timeSpentMins = Integer.valueOf(editTextMinutes.getText().toString());
                activityTimeSpent.setText(timeSetText(timeSpentHours, timeSpentMins) );
            }
        })
                .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * Convenience method to save after the user click on done button.
     */
    private void save()  {
        Long totalMinutes = TimeUnit.MINUTES.convert(goalHours, TimeUnit.HOURS)
                + goalMins;

        //Set updated values
        String name = activityName.getText().toString();
        if ( name.isEmpty() ) {
            toast(getString(R.string.toast_no_name_activity_added));
            return;
        } else if ( totalMinutes == 0 ) {
            toast(getString(R.string.toast_enter_goal));
            return;
        }
        task.setName(activityName.getText().toString());
        task.setGoal(totalMinutes.intValue());
        task.setLastModifiedValue(Calendar.getInstance());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(directory + "/" + task.getId(), task.toMap());
        mFirebaseDatabaseReference.updateChildren(childUpdates);
        finish();
    }

    /**
     * Convenience method for toasting messages to the user
     * Toast message is set tot LENGTH_LONG.
     * @param toToast the string to be displayed to the user
     */
    private void toast(String toToast) {
        Toast.makeText(this, toToast, Toast.LENGTH_LONG).show();
    }

    /**
     * Helper method to get the position of enum values in the spinner.
     * @param priority The priority obejct
     * @return the position of the priority in the spinner, or 1 if not found.
     */
    private int spinnerPosition(Task.Priority priority) {
        switch (priority) {
            case VERY_LOW:
                return 0;
            case LOW:
                return 1;
            case MEDIUM:
                return 2;
            case HIGH:
                return 3;
            case VERY_HIGH:
                return 4;
            default:
                return 2;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                task.setPriorityValue(Task.Priority.VERY_LOW);
                break;
            case 1:
                task.setPriorityValue(Task.Priority.LOW);
                break;
            case 2:
                task.setPriorityValue(Task.Priority.MEDIUM);
                break;
            case 3:
                task.setPriorityValue(Task.Priority.HIGH);
                break;
            case 4:
                task.setPriorityValue(Task.Priority.VERY_HIGH);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

//    private void initializePieChart() {
//        //Setting up the Pie Chart
////        PieChart pieChart = (PieChart) findViewById(R.id.task_activity_chart);
////        pieChart.setCenterText(task.getTimeSpent() + " minutes spent");
////        pieChart.setRotationEnabled(false);
////        pieChart.setHoleRadius(75);
////        pieChart.setDescription("");
//
//        ArrayList<PieEntry> entries = new ArrayList<>();
//        long goalDiff = task.getGoal() - task.getTimeSpent();
//        if ( goalDiff > 0) {
//            entries.add(new PieEntry(goalDiff, 0));
//        } else {
//            entries.add(new PieEntry(0, 0));
//        }
//        entries.add(new PieEntry(task.getTimeSpent(), 1));
//
//        PieDataSet dataSet = new PieDataSet(entries, "Percentage");
//
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//        colors.add( ContextCompat.getColor(this, R.color.hint_text) );
//        colors.add( ContextCompat.getColor(this, R.color.colorAccent) );
//
//        dataSet.setColors(colors);
//
//        //Initialize the Pie data
//        pieChart.invalidate();
//        PieData data = new PieData(dataSet);
//        data.setDrawValues(false);
//        pieChart.setData(data);
//    }

    /**
     * Convenience method for setting the time related values for the goal and time spent fields
     * @param hours hour value that is being set
     * @param minutes minute value that is being set
     * @return the string containing the minute and hour values that will be set in the TextView
     */
    private String timeSetText(int hours, int minutes) {
        if (minutes > 1 && hours > 1) {
            return hours + " " + getResources().getString(R.string.timeentry_dialog_hours_label) + " & " +
                    minutes + " " +  getResources().getString(R.string.timeentry_dialog_minutes_label) ;
        } else if (minutes == 0 && hours > 1) {
            return hours + " " + getResources().getString(R.string.timeentry_dialog_hours_label);
        }
        else if (minutes == 1 && hours > 1) {
            return hours + " " + getResources().getString(R.string.timeentry_dialog_hours_label) + " & " +
                    minutes + " " + getResources().getString(R.string.timeentry_dialog_minute_label);
        }
        else if (hours == 1 && minutes > 1) {
            return hours + " " + getResources().getString(R.string.timeentry_dialog_hour_label) + " & " +
                    minutes + " " + getResources().getString(R.string.timeentry_dialog_minutes_label);
        }
        else if (hours == 1 && minutes == 1) {
            return hours + " " + getResources().getString(R.string.timeentry_dialog_hour_label) + " & " +
                    minutes + " " + getResources().getString(R.string.timeentry_dialog_minute_label);
        }
        else if (hours == 1 && minutes == 0) {
            return hours + " " + getResources().getString(R.string.timeentry_dialog_hour_label);
        }
        else if (minutes == 1 && hours == 0) {
            return minutes + " " + getResources().getString(R.string.timeentry_dialog_minute_label);
        }
        else {
            return minutes + " " + getResources().getString(R.string.timeentry_dialog_minutes_label);
        }
    }

}