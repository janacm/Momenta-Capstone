package com.momenta;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText activityName;
    TextView activityDeadline;
    TextView activityGoal;
    Task task;

    //TODO Remove focus from view activity edit text
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get the id of the activity and retrieve it from the DB
        Bundle bundle = getIntent().getExtras();
        int id = (int) bundle.get(DBHelper.ACTIVITY_ID);
        task = DBHelper.getInstance(this).getTask(id);
        Log.e("TaskActivity", task.getName());

        //Set the text of the view
        activityName = (EditText)findViewById(R.id.task_name_edit_text);
        activityName.setText( task.getName() );

        activityGoal = (TextView)findViewById(R.id.task_time_set_time);
        activityGoal.setText( task.getTimeString() );

        activityDeadline = (TextView) findViewById(R.id.task_time_set_deadline);
        activityDeadline.setText( task.getFormattedDeadline() );

        Spinner spinner = (Spinner)findViewById(R.id.task_priority_spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(spinnerPosition(task.getPriority()));
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
                        DBHelper.getInstance(getApplicationContext()).deleteTask(task.getId());
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

    //Handler method for the goal view being clicked
    public void goalOnClick(View v) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View alertView = inflater.inflate(R.layout.activity_alert_dialog, null);
        TimeDialogBuilder timeDialogBuilder = new TimeDialogBuilder(this, alertView,
                activityName.getText().toString().trim(), activityGoal);
        AlertDialog alertDialog = timeDialogBuilder.getAlertDialog();
        alertDialog.show();
    }

    //Handler method for the deadline view being clicked
    public void deadlineOnClick(View v){
        Calendar cal = task.getDeadline();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar temp = Calendar.getInstance();
                temp.set(year, monthOfYear, dayOfMonth);
                task.setDeadline(temp);
                activityDeadline.setText( new SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA).format(temp.getTime()) );
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        dialog.show();
    }

    //TODO handle priority being clicked
    public void priorityOnClick(View v) {

    }

    /**
     * Convenience method to save after the user click on done button.
     */
    private void save()  {
        String goal = Task.stripNonDigits(activityGoal.getText().toString());
        int goalInMinutes = Task.convertHourMinuteToMinute( goal );
        task.setName(activityName.getText().toString());
        task.setTimeInMinutes(goalInMinutes);
        task.setLastModified(Calendar.getInstance());
//        priority.get

        if ( DBHelper.getInstance(this).updateTask(task) ) {
            toast("Activity has been updated");
        } else {
            toast("There was a problem updating your activity");
        }
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
                task.setPriority(Task.Priority.VERY_LOW);
                break;
            case 1:
                task.setPriority(Task.Priority.LOW);
                break;
            case 2:
                task.setPriority(Task.Priority.MEDIUM);
                break;
            case 3:
                task.setPriority(Task.Priority.HIGH);
                break;
            case 4:
                task.setPriority(Task.Priority.VERY_HIGH);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

}
