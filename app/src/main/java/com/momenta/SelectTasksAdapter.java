package com.momenta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Adapter to handle the task data in the SelectTasksActivity and serve the recycler view
 */
public class SelectTasksAdapter extends RecyclerView.Adapter<SelectTasksAdapter.ViewHolder> {

    private static final String TAG = "SelectTasksAdapter";
    private List<Task> tasks;

    private String goalDirectory;

    //A map of all the task items and positions that have been selected from the list of tasks.
    private HashMap<Integer,Boolean> itemClickedMap;


    public SelectTasksAdapter(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            goalDirectory = user.getUid() + "/goals";
        } else {
            Log.e(TAG, "User is null");
        }
        Log.d(TAG, goalDirectory);
        tasks = new ArrayList<>();

        DatabaseReference mDatabaseReference = FirebaseProvider.getInstance().getReference();
        mDatabaseReference.child(goalDirectory).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "Processing call back");
                        // Iterate over all tasks
                        for (DataSnapshot snapshot: dataSnapshot.getChildren() ){

                            Task task = new Task();
                            task.setId( (String)snapshot.child("id").getValue() );
                            task.setName( (String)snapshot.child("name").getValue() );
                            Long goal = (long)snapshot.child("goal").getValue();
                            task.setGoal( goal.intValue() );
                            task.setDeadline( (Long)snapshot.child("deadline").getValue() );
                            task.setDateCreated( (Long)snapshot.child("dateCreated").getValue() );
                            task.setLastModified( (Long)snapshot.child("lastModified").getValue() );
                            Long timeSpent = (long)snapshot.child("timeSpent").getValue();
                            task.setTimeSpent( timeSpent.intValue() );
                            task.setPriority( (String)snapshot.child("priority").getValue() );

                            // Add task to the list
                            tasks.add(task);
                        }
                        Log.e(TAG, "Finished processing " + tasks.size() + " tasks");
                        //Initializing the itemClickedMap for all positions to be false
                        itemClickedMap = new HashMap<>();
                        for(int i = 0; i < tasks.size(); i++ ){
                            itemClickedMap.put(i, false);
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        Log.e(TAG, "Waiting for callback");
    }

    @Override
    public SelectTasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
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
                if(!holder.activityCheckbox.isChecked()) {
                    holder.activityCheckbox.setChecked(true);
                    //When clicked and the checkbox is checked, insert into itemClickedMap
                    itemClickedMap.put(holder.getAdapterPosition(), true);
                    System.out.println(holder.getAdapterPosition() + " true");
                }
                else{
                    holder.activityCheckbox.setChecked(false);
                    itemClickedMap.put(holder.getAdapterPosition(), false);
                    System.out.println(holder.getAdapterPosition() + " false");
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
                if(holder.activityCheckbox.isChecked()) {
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

//    /**
//     * Method for extracting the items that selected as well as their task ID and storing
//     * them in another HashMap to be used by the AddTimeToTaskActivity.
//     */
//    public HashMap<Integer,String> getItemsClickedIDs(){
//        HashMap<Integer,String> itemIDs = new HashMap<>();
//        for (Map.Entry<Integer, Boolean> entry : itemClickedMap.entrySet()){
//            if(entry.getValue()){
//                itemIDs.put(entry.getKey(), tasks.get(entry.getKey()).getId());
//                //System.out.println(tasks.get(entry.getKey()).getId());
//            }
//        }
//        return itemIDs;
//    }

    /**
     * Returns a map containing the ids & names of selected tasks
     * @return HashMap Key: the ids of the tasks
     *                 Value: the names of the tasks
     */
    public HashMap<String, String> getSelectedTasks() {
        HashMap<String, String> selectedTasks = new HashMap<>();
        for (Map.Entry<Integer, Boolean> entry : itemClickedMap.entrySet()){
            if(entry.getValue()){
                Task t = tasks.get(entry.getKey());
                selectedTasks.put( t.getId(), t.getName() );
            }
        }
        return selectedTasks;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public final TextView activityName;
        public final CheckBox activityCheckbox;

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
