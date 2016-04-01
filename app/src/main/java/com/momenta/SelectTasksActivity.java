package com.momenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class SelectTasksActivity extends AppCompatActivity {

    //UI items
    private RecyclerView mRecyclerView;
    private SelectTasksAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tasks);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Select tasks to add time to");
        actionbar.setElevation(0);

       //Display the task items in a recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.select_tasks_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SelectTasksAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
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
                prepareItems();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for extracting the task IDs and position from the tasks in the list and passing
     * them to the AddTimeToTaskActivity in the form of a stack.
    **/
    public void prepareItems() {
        Map<Integer, Integer> temp = new TreeMap<Integer, Integer>(mAdapter.getItemsClickedIDs());
        Stack<Integer> stack = new Stack<Integer>();
        Set set = temp.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry me = (Map.Entry) iterator.next();
            stack.push((Integer)me.getValue());
            //System.out.println(me.getKey());
        }

        Collections.reverse(stack);
        Intent intent = new Intent(this, AddTaskTimeActivity.class);
        intent.putExtra("Item IDs",stack);
        startActivity(intent);
    }
}
