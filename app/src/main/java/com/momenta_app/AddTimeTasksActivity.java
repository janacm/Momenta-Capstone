package com.momenta_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AddTimeTasksActivity extends AppCompatActivity {

    private static final String TAG = "SelectTasksActivity";
    private static final int NEW_TASK_REQUEST_CODE = 1;
    //UI items
    public RecyclerView mRecyclerView;
    private SelectTasksAdapter mAdapter;

    HelperPreferences hp;

    int intervalHours, intervalMins;

    //Firebase directory
    private String goalDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_tasks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(R.string.select_tasks_to_add_time_to_title);
        actionbar.setElevation(0);

        mRecyclerView = (RecyclerView) findViewById(R.id.select_tasks_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Get all goals from firebase
        goalDirectory = FirebaseProvider.getUserPath() + "/goals";
        final List<Task> tasks = new ArrayList<>();

        DatabaseReference mDatabaseReference = FirebaseProvider.getInstance().getReference();
        mDatabaseReference.child(goalDirectory).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Iterate over all tasks
                        for (DataSnapshot snapshot: dataSnapshot.getChildren() ) {
                            long currentTime = System.currentTimeMillis();
                            //If the deadline hasn't passed and task is still active
                            if (currentTime < (Long) snapshot.child(Task.DEADLINE).getValue() && String.valueOf(snapshot.child(Task.STATE).getValue()).equals(String.valueOf((Task.State.ACTIVE)))) {
                                Task task = new Task();
                                task.setId((String) snapshot.child(Task.ID).getValue());
                                task.setName((String) snapshot.child(Task.NAME).getValue());
                                task.setGoal(snapshot.child(Task.GOAL).getValue(Integer.class));
                                task.setDeadline((Long) snapshot.child(Task.DEADLINE).getValue());
                                task.setDateCreated((Long) snapshot.child(Task.DATE_CREATED).getValue());
                                task.setLastModified((Long) snapshot.child(Task.LAST_MODIFIED).getValue());
                                task.setTimeSpent(snapshot.child(Task.TIME_SPENT).getValue(Integer.class));
                                task.setPriority((String) snapshot.child(Task.PRIORITY).getValue());
                                task.setType((String) snapshot.child(Task.TYPE).getValue());

                                // Add task to the list
                                if(task.getTypeValue()!=Task.Type.TODO){
                                    tasks.add(task);
                                }
                            }
                        }

                        //Sort tasks list based on last modified
                        Collections.sort(tasks, new Comparator<Task>() {
                            @Override
                            public int compare(Task t1, Task t2) {
                                if (t1.getLastModified() > t2.getLastModified())
                                    return 1;
                                if (t1.getLastModified() < t2.getLastModified())
                                    return -1;
                                return 0;
                            }
                        });

                        //Reverse tasks list to be in descending order
                        Collections.reverse(tasks);

                        mAdapter = new SelectTasksAdapter(AddTimeTasksActivity.this, tasks);
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        hp = new HelperPreferences(this);
        //Initialize snooze interval time to the base interval time for first run
        if(Integer.parseInt(hp.getPreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_HOURS, "0")) == 0 && Integer.parseInt(hp.getPreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_MINS, "0")) == 0){
            hp.savePreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_MINS,hp.getPreferences(Constants.SHPREF_INTERVAL_MINS, "0"));
            hp.savePreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_HOURS,hp.getPreferences(Constants.SHPREF_INTERVAL_HOURS, "0"));
        }
        intervalHours = Integer.parseInt(hp.getPreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_HOURS, "0"));
        intervalMins = Integer.parseInt(hp.getPreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_MINS, "0"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_done:
                prepareItems();
                break;
            case R.id.action_add:
                addNewActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //Method for adding a new activity from the SelectTasksActivity
    public void addNewActivity(){
        Intent intent = new Intent(this, AddNewTaskActivity.class);
        //Simple boolean used to verify that task is being added from SelectTasksActivity
        intent.putExtra("NewTaskFromSelectTasks", true);
        startActivityForResult(intent, NEW_TASK_REQUEST_CODE);
    }
    //Method for extracting the task IDs and position from the tasks in the list and passing
    //them to the AddTimeToTaskActivity in the form of a stack.
    public void prepareItems() {
        HashMap<String, String> selectedTasks = mAdapter.getSelectedTasks();

        int size = selectedTasks.size();
        int intervalTime = (intervalHours * 60) + intervalMins;
        if ((size > 0) && ((intervalTime/size) >= 1)) {
            Intent intent = new Intent(this, AddTaskTimeActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable("HASH_MAP", selectedTasks);
            intent.putExtras(extras);
            startActivity(intent);
            finish();
        } else {
            Log.d("interval time", String.valueOf(intervalTime));
            if((size > 0) && (intervalTime/size) < 1)
                toast(getResources().getString(R.string.select_tasks_cannot_divide_tasks));
            else{
                toast(getResources().getString(R.string.select_tasks_no_tasks_selected));
            }
        }
    }

    /**
     * Sets the adapter of the recycler view
     * @param adapter
     */
    public void setAdapter(final SelectTasksAdapter adapter){
        mAdapter = adapter;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.invalidate();
            }
        });
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

    //Override method used for getting a result from creating a new task in SelectTasksActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check to see if results
        if (requestCode == NEW_TASK_REQUEST_CODE) {
            // If result code is OK, refresh or reset activity
            if (resultCode == RESULT_OK) {
                finish();
                startActivity(getIntent());
            }
        }
    }
}


