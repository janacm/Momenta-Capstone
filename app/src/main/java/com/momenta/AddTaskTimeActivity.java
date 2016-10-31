package com.momenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AddTaskTimeActivity extends AppCompatActivity {
    helperPreferences hp;

    private static final String TAG = "AddTaskTimeActivity";

    //SeekBar values
    private int intervalTime;
    private int intervalHours;
    private int intervalMins;
    private int stepValue;
    private int seekbarValue;
    private Long currTimeSpent;

    //UI elements
    TextView taskName;
    TextView seekbarMin;
    TextView seekbarMax;
    TextView seekbarText;
    SeekBar seekbar;
    Button nextBtn;

    private Stack<Map.Entry<String, String>> store;
    private Stack<Map.Entry<String, String>> taskStask;

    //Firebase instances
    private DatabaseReference mFirebaseDatabaseReference;
    private String goalDirectory = "";
    private String timespentDirectory = "";

    //Array to store time spent for each task
    private int intervalValues[];

    //Counter for indicating current activity position. I.e current activity is activity #1
    private int position;

    //Integer for storing the number of tasks
    private int numofTasks;

    //Award manager for award's progress
    private AwardManager awardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_to_task);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setElevation(0);

        String date = SettingsActivity.formatDate(Calendar.getInstance().getTime(),
                Constants.TIME_SPENT_DATE_FORMAT);
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            goalDirectory = mFirebaseUser.getUid() + "/goals";
            timespentDirectory = mFirebaseUser.getUid() + "/" + Task.TIME_SPENT + "/" + date;
        }
        mFirebaseDatabaseReference = FirebaseProvider.getInstance().getReference();


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

        HashMap<String, String> selectedTasks;
        if ( bundle!= null ) {
            selectedTasks = (HashMap<String, String>) bundle.getSerializable("HASH_MAP");
            taskStask = new Stack<>();
            for (Map.Entry<String, String> entry : selectedTasks.entrySet()) {
                taskStask.push(entry);
            }
        }

        //Initialize a store stack, which will be used for moving backward.
        store = new Stack<>();


        //Initialize the interval values array to store the time intervals for each task
        intervalValues = new int[taskStask.size()];
        numofTasks = taskStask.size();

        //Initially set up the screen for first use
        setUpScreen(taskStask);

        nextBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNext();
            }
        });
        awardManager = AwardManager.getInstance(this);
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
     * @param stack Stack containing the remaining activities left require a time interval to be
     *                set.
     */
    public void setUpScreen(Stack<Map.Entry<String, String>> stack) {
        //Obtain the ID of the next task
        Map.Entry<String, String> entry = stack.peek();

        //Animate the TextView displaying its text name
        animate(taskName, entry.getValue(), 300);
        setUpSeekbar();

        //If last or only task, set the text of the next button to be "Done"
        if(taskStask.size() == 1){
            nextBtn.setText(R.string.add_time_to_task_done);
        }
        else{
            nextBtn.setText(R.string.add_time_to_task_next);
        }
    }

    //Method for setting up the seekbar intervals for a given task
    public void setUpSeekbar() {
        animate(seekbarText, formatSeekbarValue((intervalTime / 2)), 300);


        if ((intervalTime % 2) == 0) {
            seekbar.setProgress(intervalTime / 2);
            seekbarValue = intervalTime/2;
        } else {
            seekbar.setProgress((intervalTime / 2) + 1);
            seekbarValue = (intervalTime/2) + 1;
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

                value = Math.round((value / stepValue) * stepValue);
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
        if ((position < numofTasks - 1) && ((temp - seekbarValue) != temp)) {
            //Checking to see if user uses all time on a task while other tasks remain to be filled
            if (timeToSpare(numofTasks, position, temp, seekbarValue)) {
                intervalTime = intervalTime - seekbarValue;
                intervalValues[position] = seekbarValue;
                store.push(taskStask.pop());
                position++;
                setUpScreen(taskStask);
            } else {
                if(!timeToSpare(numofTasks, position, temp, seekbarValue))
                    toast("Not enough time left for remaining tasks");
                if((temp - seekbarValue) == temp)
                    toast("Must add a time value to the task");
            }
        } else {
            if ((position == (numofTasks - 1)) && ((temp - seekbarValue) != temp)) {
                //When we've completed adding time for the last task, go to the main activity
                intervalTime = intervalTime - seekbarValue;
                intervalValues[position] = seekbarValue;
                store.push(taskStask.pop());
                storeInDB();
                Intent intent = new Intent(this, MainActivity.class);

                //Clear activity stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);


                if (numofTasks > 1)
                    toast("Successfully added time to your tasks");
                else
                    toast("Successfully added time to your task");

            }
        }
    }


    // Method for handling the clicking of the back button
    public void moveBack() {
        if (position > 0) {
            taskStask.push(store.pop());
            intervalTime = intervalTime + intervalValues[position - 1];
            setUpScreen(taskStask);
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
        int storeSize = store.size();
        for (int i = 0; i < storeSize;  i++) {
            final String taskID = store.pop().getKey();
            final int index = i; // Save final index to be used in anonymous class
            final String tempTimeDir = timespentDirectory + "/" + taskID;
            final String tempGoalDir = goalDirectory + "/" + taskID;


            //Update the timeSpent directory
            mFirebaseDatabaseReference.child(tempTimeDir).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            currTimeSpent = 0L;  // Reset the value of current time spent
                            Long updatedTimeSpent = (long)intervalValues[index];
                            if ( snapshot.exists() ) {
                                // If exists update current time spent
                                currTimeSpent = (long)snapshot.child(Task.TIME_SPENT).getValue();
                                updatedTimeSpent += currTimeSpent;
                            }

                            // Update time spent in both places
                            mFirebaseDatabaseReference.child(tempTimeDir + "/" + Task.TIME_SPENT)
                                    .setValue(updatedTimeSpent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
            );

            //Update the goal directory
            mFirebaseDatabaseReference.child(tempGoalDir).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            currTimeSpent = 0L;  // Reset the value of current time spent
                            Long updatedTimeSpent = (long)intervalValues[index];
                            if ( snapshot.exists() ) {
                                Task task = snapshot.getValue(Task.class);
                                // If exists update current time spent
                                currTimeSpent = (long)snapshot.child(Task.TIME_SPENT).getValue();
                                updatedTimeSpent += currTimeSpent;
                                awardManager.handleAwardsProgress(updatedTimeSpent,task);
                            }

                            mFirebaseDatabaseReference.child(tempGoalDir + "/" +Task.TIME_SPENT)
                                    .setValue(updatedTimeSpent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
            );
        }
    }

    /**
     * Method for determining if there is time to spare for other tasks after time is deducted for a task
     * @param numTasks is the number of tasks selected to insert time into
     * @param pos current task that time is being inserted into
     * @param timeLeft time remaining after time is deducted for each task
     * @param seekvalue time value to be added to each task selected by the user
     * @return either true or false depending on if there is enough time to be equally divided for remaining tasks.
     */
    //TODO Set up method for blocking user from selecting a greater # of tasks from the select tasks screen than # of mins available
    private boolean timeToSpare(int numTasks, int pos, int timeLeft, int seekvalue){
        boolean flag = false;
        int remTasks, remTime;
        remTasks = numTasks - (pos + 1);
        remTime = timeLeft - seekvalue;

        if(pos + 1 < numTasks){
            flag = (remTime / remTasks) >= 1;
        }
        else{
            if((timeLeft - seekvalue) >=0){
                flag = true;
            }
        }
        return flag;
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
