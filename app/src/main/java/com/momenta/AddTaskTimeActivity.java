package com.momenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class AddTaskTimeActivity extends AppCompatActivity {
    helperPreferences hp;

    //SeekBar values
    int intervalTime;
    int intervalHours;
    int intervalMins;
    int stepValue;
    int seekbarValue;

    //UI elements
    TextView taskName;
    TextView seekbarMin;
    TextView seekbarMax;
    TextView seekbarText;
    SeekBar seekbar;
    Button nextBtn;

    //Data structures to store Task IDs
    ArrayList<Integer> tempIDs;
    Stack<Integer> taskIDs;
    Stack<Integer> store;
    Task task;

    //Array to store time spent for each task
    int intervalValues[];

    //Counter for indicating current activity position. I.e current activity is activity #1
    int position;

    //Integer for storing the number of tasks
    int numofTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_to_task);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setElevation(0);

        taskName = (TextView) findViewById(R.id.add_time_to_task_taskname);
        seekbar = (SeekBar) findViewById(R.id.add_time_to_task_seekbar);
        seekbarText = (TextView) findViewById(R.id.add_time_to_task_seekbar_value);
        seekbarMin = (TextView) findViewById(R.id.add_time_to_task_seekbar_value_min);
        seekbarMax = (TextView) findViewById(R.id.add_time_to_task_seekbar_value_max);
        nextBtn = (Button) findViewById(R.id.add_time_to_task_nextBtn);

        //Initialize helperPreferences and extract interval values from preferences
        hp = new helperPreferences(this);
        intervalHours = Integer.parseInt(hp.getPreferences(Constants.SHPREF_INTERVAL_HOURS, "0"));
        intervalMins = Integer.parseInt(hp.getPreferences(Constants.SHPREF_INTERVAL_MINS, "0"));

        //Initialize the time interval for each task, the initial step value and task position
        intervalTime = (intervalHours * 60) + intervalMins;
        stepValue = 1;
        position = 0;

        //Retrieve the Task ID stack from previous activity
        Bundle bundle = getIntent().getExtras();

        //Set an arraylist to hold the task id's temporarily (stacks cannot be inserted into bundle)
        tempIDs = (ArrayList<Integer>) bundle.get("Task IDs");
        taskIDs = new Stack<>();

        //Initialize a store stack, which will be used for moving backward.
        store = new Stack<>();

        //Contents from the tempIDs arraylist are copied into the item IDs stack
        for (int i = 0; i < tempIDs.size(); i++) {
            taskIDs.push(tempIDs.get(i));
        }

        //Initialize the interval values array to store the time intervals for each task
        intervalValues = new int[taskIDs.size()];
        numofTasks = taskIDs.size();

        //Initially set up the screen for first use
        setUpScreen(taskIDs);

        nextBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNext();
            }
        });
    }

    /**
     * Method for animating a given TextView using a simple fade in/out animation
     *
     * @param tv       the TextView to be animated
     * @param text     the text to appear after the fade in/out animation
     * @param duration the duration of the animation
     */
    public void animate(final TextView tv, final String text, int duration) {
        if (position > 0) {
            final Animation in = new AlphaAnimation(0.0f, 1.0f);
            in.setDuration(duration);

            final Animation out = new AlphaAnimation(1.0f, 0.0f);
            out.setDuration(duration + 100);

            tv.startAnimation(out);

            out.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tv.startAnimation(in);
                    tv.setText(text);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            tv.setText(text);
        }
    }

    /**
     * Method for setting up the activity to display information for a particular task
     *
     * @param taskIDs Stack containing the remaining activities left require a time interval to be
     *                set.
     */
    public void setUpScreen(Stack<Integer> taskIDs) {
        //Obtain the ID of the next task
        task = DBHelper.getInstance(this).getTask(taskIDs.peek());

        //Animate the TextView displaying its text name
        animate(taskName, task.getName(), 300);
        setUpSeekbar();
    }

    //Method for setting up the seekbar intervals for a given task
    public void setUpSeekbar() {
        animate(seekbarText, formatSeekbarValue((intervalTime / 2)), 300);


        if ((intervalTime % 2) == 0) {
            seekbar.setProgress(intervalTime / 2);
        } else {
            seekbar.setProgress((intervalTime / 2) + 1);
        }

        intervalHours = intervalTime / 60;
        intervalMins = intervalTime % 60;

        String hoursMinsString = intervalHours + getString(R.string.add_time_to_task_hours) +
                " " + intervalMins + getString(R.string.add_time_to_task_minutes);
        String hoursOnlyString = intervalHours + getString(R.string.add_time_to_task_hours);
        String maxMinsString = intervalTime + getString(R.string.add_time_to_task_minutes);
        String minMinsString = 0 + getString(R.string.add_time_to_task_minutes);

        //Convert intervalHours and interv,alMins into mins for intervalTime
        if (intervalHours >= 1) {
            if (intervalMins >= 1) {
                animate(seekbarMax, hoursMinsString, 300);
                animate(seekbarMin, minMinsString, 300);
                seekbar.setMax(intervalTime);
            } else {
                animate(seekbarMax, hoursOnlyString, 300);
                animate(seekbarMin, minMinsString, 300);
                seekbar.setMax(intervalTime);
            }
        } else {
            animate(seekbarMax, maxMinsString, 300);
            animate(seekbarMin, minMinsString, 300);
            seekbar.setMax(intervalTime);
        }


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                //Setting the step value
                if (intervalHours >= 1) {
                    if (intervalHours < 4) {
                        stepValue = 5;
                    } else if (intervalHours >= 4 && intervalHours < 10) {
                        stepValue = 30;
                    } else {
                        stepValue = (intervalHours / 10) * 60;
                    }
                } else {
                    stepValue = 1;
                }

                value = (value / stepValue) * stepValue;
                seekBar.setProgress(value);

                //Store seekbar progress globally to to be used by other methods.
                seekbarValue = value;

                //Display seekbar value in textview
                seekbarText.setText(formatSeekbarValue(seekbarValue));
            }
        });
    }

    /**
     * Method for converting the interval time in mins to different forms such as hours and mins,
     * hours or simply minutes.
     *
     * @param interval is the time interval in minutes
     * @return a string with the correct format of the interval value and accompanying text denoting
     * the hours and/or mins
     */
    public String formatSeekbarValue(int interval) {
        String string;
        int hours = interval / 60;
        int mins = interval % 60;

        String hoursMinsString = hours + getString(R.string.add_time_to_task_hours) + " " +
                mins + getString(R.string.add_time_to_task_minutes);

        String hoursOnlyString = hours + getString(R.string.add_time_to_task_hours);

        String minsOnlyString = mins + getString(R.string.add_time_to_task_minutes);

        if ((hours >= 1) && (mins >= 1)) {
            string = hoursMinsString;
        } else if ((hours >= 1) && (mins == 0)) {
            string = hoursOnlyString;
        } else {
            string = minsOnlyString;
        }
        return string;
    }

    // Method for handling the clicking of the next button
    public void moveNext() {
        int temp = intervalTime;
        if (position < numofTasks - 1 && (temp - seekbarValue) != temp) {
            //Checking to see if user uses all time on a task while other tasks remain to be filled
            if ((temp - seekbarValue) > 0) {
                intervalTime = intervalTime - seekbarValue;
                intervalValues[position] = seekbarValue;
                store.push(taskIDs.pop());
                position++;
                setUpScreen(taskIDs);
            } else {
                toast("Error: Invalid interval time");
            }

        } else if (position == (numofTasks - 1) && (temp - seekbarValue) != temp) {
            //When we've completed adding time for the last task, go to the main activity
            storeInDB();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            if (numofTasks > 1)
                toast("Successfully added time to your tasks");
            else
                toast("Successfully added time to your task");

        } else {
            toast("Error: Invalid interval time");
        }
    }


    // Method for handling the clicking of the next button
    public void moveBack() {
        if (position > 0) {
            taskIDs.push(store.pop());
            intervalTime = intervalTime + intervalValues[position - 1];
            setUpScreen(taskIDs);
            position--;
        } else if (position == 0 && store.isEmpty()) {
            finish();
            Intent intent = new Intent(this, SelectTasksActivity.class);
            startActivity(intent);
        }
    }

    //Method used to store interval values for each task in the DB
    public void storeInDB() {
        Collections.reverse(store);
        for (int i = 0; i < store.size(); i++) {
            int taskID = store.pop();
            task = DBHelper.getInstance(this).getTask(taskID);
            //FIXME Do you wanna set time spent here? or add time?
//            task.setTimeSpent(intervalValues[i]);
            Log.e("Adding-Time-to-task", "Adding " + intervalValues[i] +" minute(s) to " + task.getName());
            Log.e("Adding-Time-to-task", "Before time spent " + task.getTimeSpent());
            task.setTimeSpent(intervalValues[i]);
            Log.e("Adding-Time-to-task", "After time spent " + task.getTimeSpent());

            DBHelper.getInstance(this).updateTask(task);

            //int timeSpent = DBHelper.getInstance(this).getTask(taskID).getTimeSpent();
            //System.out.println(timeSpent);
        }
    }

    /**
     * Convenience method for toasting messages to the user
     * Toast message is set tot LENGTH_LONG.
     *
     * @param toToast the string to be displayed to the user
     */
    private void toast(String toToast) {
        Toast.makeText(this, toToast, Toast.LENGTH_LONG).show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                moveBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
