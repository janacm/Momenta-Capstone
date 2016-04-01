package com.momenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

public class AddTaskTimeActivity extends AppCompatActivity {

    //UI elements
    TextView addTimeToTaskTitle;
    TextView taskName;
    TextView seekbarValue;
    SeekBar seekbar;
    Button nextBtn;

    //Datastructures to store Task IDs
    ArrayList<Integer> tempIDs;
    Stack<Integer> itemIDs;

    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_to_task);

        //Retrieve the task id stack from previous activity
        Bundle bundle = getIntent().getExtras();

        //Set an arraylist to hold the task id's temporarily (as a stack cannot be used)
        tempIDs = (ArrayList<Integer>) bundle.get("Item IDs");
        itemIDs = new Stack<Integer>();

        //Contents from the tempIDs list are copied into the stack
        for (int i = 0; i < tempIDs.size(); i++) {
            itemIDs.push(tempIDs.get(i));
        }
        task = DBHelper.getInstance(this).getTask(itemIDs.pop());

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setElevation(0);

        addTimeToTaskTitle = (TextView) findViewById(R.id.add_time_to_task_title);
        taskName = (TextView) findViewById(R.id.add_time_to_task_taskname);
        seekbar = (SeekBar) findViewById(R.id.add_time_to_task_seekbar);
        seekbarValue = (TextView) findViewById(R.id.add_time_to_task_seekbar_value);
        nextBtn = (Button) findViewById(R.id.add_time_to_task_nextBtn);
        taskName.setText(task.getName());
        seekbar.setMax(59);

        /**
         * If the last item in the stack is being displayed, and the stack is empty.
         * There should besome additional functionality here. Will add.
         */
        if (itemIDs.isEmpty() == true) {
            nextBtn.setText("Done");
        }

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                int value = progress;
                seekbarValue.setText(value + " mins");
            }
        });

        nextBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * If the itemIDs stack is not empty, call this class once again and pass the
                 * remaining task IDs.
                 **/
                if (itemIDs.isEmpty() == false) {
                    Intent intent = new Intent(AddTaskTimeActivity.this, AddTaskTimeActivity.class);
                    intent.putExtra("Item IDs", itemIDs);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AddTaskTimeActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
