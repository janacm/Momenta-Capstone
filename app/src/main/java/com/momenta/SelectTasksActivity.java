package com.momenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class SelectTasksActivity extends AppCompatActivity {

    //UI items
    public RecyclerView mRecyclerView;
    private SelectTasksAdapter mAdapter;

    helperPreferences hp;

    int intervalHours, intervalMins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tasks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(R.string.select_tasks_to_add_time_to_title);
        actionbar.setElevation(0);

        //Display the task items in a recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.select_tasks_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SelectTasksAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        hp = new helperPreferences(this);
        intervalHours = Integer.parseInt(hp.getPreferences(Constants.SHPREF_INTERVAL_HOURS, "0"));
        intervalMins = Integer.parseInt(hp.getPreferences(Constants.SHPREF_INTERVAL_MINS, "0"));
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
        }
        return super.onOptionsItemSelected(item);
    }

    //Method for extracting the task IDs and position from the tasks in the list and passing
    //them to the AddTimeToTaskActivity in the form of a stack.
    public void prepareItems() {
        Map<Integer, Integer> temp = new TreeMap<>(mAdapter.getItemsClickedIDs());
        int size = temp.size();
        int intervalTime = (intervalHours * 60) + intervalMins;
        if ((size > 0) && ((intervalTime/size) >= 1)){
            Stack<Integer> taskIDs = new Stack<>();
            Set set = temp.entrySet();
            for (Object aSet : set) {
                Map.Entry me = (Map.Entry) aSet;
                taskIDs.push((Integer) me.getValue());
            }

            Collections.reverse(taskIDs);

            Intent intent = new Intent(this, AddTaskTimeActivity.class);
            intent.putExtra("Task IDs", taskIDs);
            startActivity(intent);
            finish();
        } else {
            if((size > 0) && (intervalTime/size) < 1)
                toast(getResources().getString(R.string.select_tasks_cannot_divide_tasks));
            else{
                toast(getResources().getString(R.string.select_tasks_no_tasks_selected));
            }
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
}


