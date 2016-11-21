package com.momenta_app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;


public class DashboardTaskStatsAdapter extends RecyclerView.Adapter<DashboardTaskStatsAdapter.ViewHolder>{
    private List<Task> tasks;
    Context context;

    public DashboardTaskStatsAdapter(Context c, List<Task> tasks){
        this.tasks = tasks;
        context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the item layout
        View activity = inflater.inflate(R.layout.dashboard_task_stats_item, parent, false);


        // Return a new holder instance
        return new ViewHolder(activity);
    }

    @Override
    public void onBindViewHolder(final DashboardTaskStatsAdapter.ViewHolder holder, int position) {
        //Get the tasks at index, position from the tasks list
        Task task = tasks.get(position);

        //Set the fields of the item_activity layout from the task object
        holder.taskName.setText(task.getName());

        Calendar taskLastMod = task.getLastModifiedValue();
        Calendar tempDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        String taskLastModString;

        //Display 'Today' string if task modified today
        if(Objects.equals(sdf.format(taskLastMod.getTime()), sdf.format(tempDate.getTime()))){
            taskLastModString = "Today";
        }
        else{
            tempDate.add(Calendar.DAY_OF_YEAR, -1);
            if(Objects.equals(sdf.format(taskLastMod.getTime()), sdf.format(tempDate.getTime()))){
                taskLastModString = "Yesterday";
            }
            else{
                sdf.applyPattern("MMM d, ''yy");
                taskLastModString = sdf.format(taskLastMod.getTime());
            }
        }
        holder.taskLastModified.setText(taskLastModString);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start TaskActivity for specific task clicked in card
                 String taskId = tasks.get(holder.getAdapterPosition()).getId();
                 Intent intent = new Intent(context,TaskActivity.class);
                 intent.putExtra(Task.ID, taskId);
                 context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final AutofitTextView taskName;
        public final TextView taskLastModified;

        public ViewHolder(View itemView) {
            super(itemView);
            taskName = (AutofitTextView) itemView.findViewById(R.id.dash_task_stat_item_task_name);
            taskLastModified = (TextView) itemView.findViewById(R.id.dash_task_stat_item_task_value);
        }
    }
}
