package com.momenta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Adapter to handle the activities data and serve the recycler view
 */
public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {

    private List<Task> tasks;
    private Context context;

    public ActivitiesAdapter(Context context, Activity activity) {
        List<Task> list = DBHelper.getInstance(context).getAllTasks();
        Collections.reverse(list);
        this.tasks = list;
    }

    @Override
    public ActivitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the item_activity layout
        View activity = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(activity);
    }

    @Override
    public void onBindViewHolder(ActivitiesAdapter.ViewHolder holder, final int position) {
        //Get the tasks at index, position from the tasks list
        Task task = tasks.get(position);

        //Set the fields of the item_activity layout from the task object
        holder.name.setText(task.getName());
        holder.timeSpent.setText(task.getFormattedTimeSpent());
        holder.progressBar.setProgress(task.getTimeSpent());
        holder.progressBar.setMax(task.getGoal());


        //Set onClick listener for each activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra(DBHelper.ACTIVITY_ID, tasks.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    public void retrieveTasks() {
        List<Task> list = DBHelper.getInstance(context).getAllTasks();
        tasks.clear();
        tasks.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView name;
        public TextView timeSpent;
//        public TextView timePercentage;
        public ProgressBar progressBar;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.list_item_name);
            timeSpent = (TextView) itemView.findViewById(R.id.list_item_time_spent);
//            timePercentage = (TextView) itemView.findViewById(R.id.list_item_time_percentage);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

}
