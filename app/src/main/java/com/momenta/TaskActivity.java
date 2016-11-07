package com.momenta;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class TaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        EasyPermissions.PermissionCallbacks{
    private static final String TAG = "TaskActivity";

    static final int REQUEST_AUTHORIZATION = 1000;
    static final int REQUEST_CONTACTS = 1001;
    static final int REQUEST_INVITE = 1002;
    static final int REQUEST_FAILED = 1003;

    private EditText activityName;
    private TextView activityDeadline;
    private TextView activityGoal;
    private helperPreferences helperPreferences;
    private Task task;
    private Integer goalHours = 2;
    private Integer goalMins = 30;
    private Integer timeLogged = 0;

    private boolean wasEdited = false;
    private Task.Priority priority;

    //Firebase instances
    private DatabaseReference mFirebaseDatabaseReference;
    private String directory = "";
    private String timeSpentDirectory = "";


    //Award manager for award's progress
    private AwardManager awardManager;

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

        Calendar cal = Calendar.getInstance();
        String date = SettingsActivity.formatDate(cal.getTime(), Constants.TIME_SPENT_DATE_FORMAT);
        directory = FirebaseProvider.getUserPath() + "/goals";
        timeSpentDirectory = FirebaseProvider.getUserPath() + "/" + Task.TIME_SPENT + "/" + date;

        mFirebaseDatabaseReference = FirebaseProvider.getInstance().getReference();
        task = new Task();

        //Get the id of the activity and retrieve it from the DB
        Bundle bundle = getIntent().getExtras();
        String bundleId = "";
        if (bundle != null) {
            bundleId = (String) bundle.get(Task.ID);
        }
        final String id = bundleId;

        mFirebaseDatabaseReference.child(directory + "/" + id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        task.setId( (String)dataSnapshot.child(Task.ID).getValue() );
                        task.setName( (String)dataSnapshot.child(Task.NAME).getValue() );
                        task.setGoal( dataSnapshot.child(Task.GOAL).getValue(Integer.class) );
                        task.setDeadline( (Long)dataSnapshot.child(Task.DEADLINE).getValue() );
                        task.setDateCreated( (Long)dataSnapshot.child(Task.DATE_CREATED).getValue() );
                        task.setOwner( (String)dataSnapshot.child(Task.OWNER).getValue() );
                        task.setLastModified( (Long)dataSnapshot.child(Task.LAST_MODIFIED).getValue() );
                        task.setLastModifiedBy( (String)dataSnapshot.child(Task.LAST_MODIFIED_BY).getValue() );
                        task.setTimeSpent( dataSnapshot.child(Task.TIME_SPENT).getValue(Integer.class) );
                        task.setPriority( (String)dataSnapshot.child(Task.PRIORITY).getValue() );
                        priority = task.getPriorityValue();

                        for ( DataSnapshot member : dataSnapshot.child(Task.TEAM).getChildren()) {
                            task.addTeamMember(member.getValue().toString());
                        }
                        initializeFields();
                        initializeProgressBar();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        activityName = (EditText)findViewById(R.id.task_name_edit_text);
        activityGoal = (TextView)findViewById(R.id.task_goal_value);
        activityDeadline = (TextView) findViewById(R.id.task_deadline_value);

        awardManager = AwardManager.getInstance(this);
        helperPreferences = new helperPreferences(this);
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

        Spinner spinner = (Spinner)findViewById(R.id.task_priority_spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(spinnerPosition(task.getPriorityValue()));

    }


    private void initializeProgressBar() {
        TextRoundCornerProgressBar pBar = (TextRoundCornerProgressBar)findViewById(R.id.progressBar);
        pBar.setMax(task.getGoal());
        pBar.setProgress(task.getTimeSpent());
        pBar.setProgressText(task.getTimeSpent() + "/" + task.getGoal() + " mins");
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = null;
        if (task.getOwner().equals(FirebaseProvider.getUserPath())) {
            message = getString(R.string.delete_string);
        } else {
            message = getString(R.string.leave_team);
        }
        builder.setMessage(message + " "
                + activityName.getText().toString() + "?")
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String user = FirebaseProvider.getUserPath();
                        Map<String, Object> childUpdates = new HashMap<>();
                        if (task.getOwner().equals(user)) {
                            for (String teamMember : task.getTeamMembers()) {
                                childUpdates.put(teamMember + "/goals/" + task.getId(), null);
                            }
                            mFirebaseDatabaseReference.updateChildren(childUpdates);
                        } else {
                            task.getTeamMembers().remove(user);
                            for (String teamMember : task.getTeamMembers()) {
                                childUpdates.put(teamMember + "/goals/" + task.getId(), task.toMap());
                            }
                            mFirebaseDatabaseReference.updateChildren(childUpdates);
                            mFirebaseDatabaseReference.child(directory + "/" + task.getId()).removeValue();
                        }
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
                wasEdited= true;
                String hour = editTextHours.getText().toString().trim();
                if (hour.isEmpty()) {
                    goalHours = 0;
                } else {
                    goalHours = Integer.valueOf(hour);
                }
                String minute = editTextMinutes.getText().toString().trim();
                if (minute.isEmpty()) {
                    goalMins = 0;
                } else {
                    goalMins = Integer.valueOf(minute);
                }
                activityGoal.setText(timeSetText(goalHours, goalMins));
                task.setGoal( (goalHours*60) + goalMins );
                initializeProgressBar();
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
                wasEdited = true;
                Calendar temp = Calendar.getInstance();
                temp.set(year, monthOfYear, dayOfMonth);
                temp.set(Calendar.HOUR_OF_DAY, 23);
                temp.set(Calendar.MINUTE, 59);
                temp.set(Calendar.SECOND, 59);
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
        //TODO: Changed @string/timespent_string to Add time spent (FR)
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        LayoutInflater inflater = this.getLayoutInflater();

        final View alertView = inflater.inflate(R.layout.dialog_timeentry, null);
        final EditText editTextHours  = (EditText) alertView.findViewById(R.id.dialog_hour_edittext);
        final EditText editTextMinutes = (EditText) alertView.findViewById(R.id.dialog_minute_edittext);

        final TextView dialogTitle = (TextView) alertView.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.timeentry_dialog_timespent_title);
        builder.setView(alertView);

        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hourString = editTextHours.getText().toString().trim();
                int hourValue = 0;
                if (!hourString.isEmpty()) {
                    hourValue = Integer.valueOf(hourString);
                }
                final Integer hours = hourValue;

                String minString = editTextMinutes.getText().toString().trim();
                int minValue = 0;
                if (!minString.isEmpty()) {
                    minValue = Integer.valueOf(minString);
                }
                final Integer minutes = minValue;

                task.setTimeSpent( task.getTimeSpent() + minutes + (hours*60) );
                initializeProgressBar();
                timeSpentDirectory += "/" + task.getId();
                mFirebaseDatabaseReference.child(timeSpentDirectory)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                timeLogged = minutes + (hours*60);
                                Long totalTimeForDay = timeLogged.longValue();
                                if (timeLogged == 0) {
                                    return;
                                }
                                if (snapshot.exists()) {
                                    Long prevTimeLogged = (long)snapshot.child(Task.TIME_SPENT).getValue();
                                    totalTimeForDay += prevTimeLogged;
                                }
                                createCalendarEvent();
                                awardManager.handleAwardsProgress(timeLogged.longValue(), task);
                                mFirebaseDatabaseReference.child(timeSpentDirectory + "/" + Task.TIME_SPENT)
                                        .setValue(totalTimeForDay);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        }
                );
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
        String name = activityName.getText().toString().trim();
        if (name.isEmpty()) {
            activityName.setError(getResources().getString(R.string.toast_no_name_activity_added));
            return;
        } else if ( totalMinutes == 0 ) {
            toast(getString(R.string.toast_enter_goal));
            return;
        }
        task.setName(name);
        task.setGoal(totalMinutes.intValue());
        task.setLastModifiedValue(Calendar.getInstance());
        task.setLastModifiedBy(FirebaseProvider.getUserPath());

        Map<String, Object> childUpdates = new HashMap<>();
        for (String teamMember : task.getTeamMembers()) {
            childUpdates.put(teamMember + "/goals/" + task.getId(), task.toMap());
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_done:
                save();
                break;
            case R.id.action_delete:
                delete();
                break;
            case R.id.action_share:
                share();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if (!priority.equals(task.getPriorityValue())){
            wasEdited = true;
        }
    }

    @Override
    public void onBackPressed() {
        boolean nameEdited = !activityName.getText().toString().trim().equals(task.getName());
        if (nameEdited || wasEdited) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.discard_changes))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TaskActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.create().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch(requestCode) {
            case REQUEST_CONTACTS:
                startShareActivity();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch(requestCode) {
            case REQUEST_CONTACTS:
                startShareActivity();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    createCalendarEvent();
                }
                break;
            case REQUEST_INVITE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> result = data.getStringArrayListExtra(Task.TEAM);
                    addFriends(result);
                }
        }
    }

    /**
     * Executes the Async task to create the calendar event.
     */
    private void createCalendarEvent() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isEnabled = prefs.getBoolean("integration_switch", false);
        if (!isEnabled) {
            return;
        }
        Account account = getAccount();
        if ( account!= null ) {
            String eventSummary = getString(R.string.calendar_spent);
            eventSummary += timeLogged + getString(R.string.calendar_minutes_on) + task.getName();
            GoogleCalendarIntegration gci = new GoogleCalendarIntegration(TaskActivity.this, account,
                    eventSummary, timeLogged);
            gci.execute();
        }
    }

    /**
     * Used to get the Google Account currently signed in
     * @return The Google Account currently signed in to the Momenta
     */
    private Account getAccount() {
        Account result = null;
        String accountName = helperPreferences.getPreferences(Constants.ACCOUNT_NAME, null);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PERMISSION_GRANTED) {
            return null;
        }
        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
        for (Account account : accounts) {
            if (accountName.equals(account.name)) {
                result = account;
                break;
            }
        }
        return result;
    }

    /**
     * Launches the ShareActivity, to grab the details from the user.
     */
    private void share() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PERMISSION_GRANTED) {
            askPermissions(REQUEST_CONTACTS, Manifest.permission.READ_CONTACTS);
        } else {
            startShareActivity();
        }
    }

    private void startShareActivity() {
        Intent i = new Intent(this, ShareActivity.class);
        i.putExtra(Task.TEAM, task.getTeamMembers());
        i.putExtra(Task.OWNER, task.getOwner());
        i.putExtra(Task.LAST_MODIFIED, task.getLastModified());
        i.putExtra(Task.LAST_MODIFIED_BY, task.getLastModifiedBy());
        startActivityForResult(i, REQUEST_INVITE);
    }

    /**
     * Adds new team member to the task, and duplicates the data.
     * @param teamMembers the list of new team members to be added to the task.
     */
    private void addFriends(ArrayList<String> teamMembers) {
        task.addTeamMembers(teamMembers);
        Map<String, Object> childUpdates = new HashMap<>();
        for (String teamMember : task.getTeamMembers()) {
            childUpdates.put(teamMember + "/goals/" + task.getId() + "/", task.toMap());
        }
        mFirebaseDatabaseReference.updateChildren(childUpdates);
    }

    /**
     * Prompts the user for permissions.
     * @param callbackId the unique callbackId
     * @param permissionsId the permissions to request from the user
     */
    private void askPermissions(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions) {
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
        }
    }
}