package com.momenta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Adapter to handle the task data in the SelectTasksActivity and serve the recycler view
 */
public class SelectTasksAdapter extends RecyclerView.Adapter<SelectTasksAdapter.ViewHolder> {

    private List<Task> tasks;
    private Stack<Integer> taskPositions;
    private Context context;

    //A map of all the task items and positions that have been selected from the list of tasks.
    private HashMap<Integer,Boolean> itemClickedMap;


    public SelectTasksAdapter(Context context) {
        List<Task> list = DBHelper.getInstance(context).getAllTasks();
        Collections.reverse(list);
        this.tasks = list;

        //Initializing the itemClickedMap for all positions to be false
        itemClickedMap = new HashMap<Integer,Boolean>();
        for(int i = 0; i < list.size(); i++ ){
            itemClickedMap.put(i, false);
        }
    }

    @Override
    public SelectTasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the item_activity layout
        View activity = inflater.inflate(R.layout.item_select_task, parent, false);

        // Return a new holder instance
        return new ViewHolder(activity);
    }

    @Override
    public void onBindViewHolder(final SelectTasksAdapter.ViewHolder holder, final int position) {
        //Get the tasks at index, position from the tasks list
        Task task = tasks.get(position);

        //Set the fields of the item_activity layout from the task object
        holder.activityName.setText(task.getName());

        //Set onClick listener each task item in the recyclerview list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.activityCheckbox.isChecked() == false) {
                    holder.activityCheckbox.setChecked(true);
                    //When clicked and the checkbox is checked, insert into itemClickedMap
                    itemClickedMap.put(position, true);
                    System.out.println(position + " true");
                }
                else{
                    holder.activityCheckbox.setChecked(false);
                    itemClickedMap.put(position, false);
                    System.out.println(position + " false");
                }

            }
        });

        /**
         * Set onClick listener for each checkbox to correspond with the behaviour of the entire
         * list item
         **/
        holder.activityCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.activityCheckbox.isChecked() == true) {
                    itemClickedMap.put(position, true);
                    System.out.println(position + " true");
                }
                else{
                    itemClickedMap.put(position, false);
                    System.out.println(position + " false");
                }
            }
        });
    }

    /**
     * Method for extracting the items that selected as well as their task ID and storing
     * them in another HashMap to be used by the AddTimeToTaskActivity.
     */
    public HashMap<Integer,String> getItemsClickedIDs(){
        HashMap<Integer,String> itemIDs = new HashMap<Integer,String>();
        for (Map.Entry<Integer, Boolean> entry : itemClickedMap.entrySet()){
            if(entry.getValue() == true){
                itemIDs.put(entry.getKey(), tasks.get(entry.getKey()).getId());
                //System.out.println(tasks.get(entry.getKey()).getId());
            }
        }
        return itemIDs;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView activityName;
        public CheckBox activityCheckbox;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            activityName = (TextView) itemView.findViewById(R.id.item_select_task_name);
            activityCheckbox = (CheckBox) itemView.findViewById(R.id.item_select_item_checkbox);
        }
    }
}
