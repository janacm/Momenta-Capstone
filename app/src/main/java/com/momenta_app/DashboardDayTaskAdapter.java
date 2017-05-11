package com.momenta_app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class DashboardDayTaskAdapter extends RecyclerView.Adapter<DashboardDayTaskAdapter.ViewHolder>{
    private List<Task> tasks;
    private DatabaseReference ref;
    private Context context;

    public DashboardDayTaskAdapter(DatabaseReference ref, List<Task> tasks) {
        this.tasks = tasks;
        this.ref = ref;
    }

    @Override
    public DashboardDayTaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View activity = inflater.inflate(R.layout.dashboard_day_task_item, parent, false);
        return new ViewHolder(activity);
    }

    @Override
    public void onBindViewHolder(DashboardDayTaskAdapter.ViewHolder holder, int position) {
        final Task task = tasks.get(position);

        holder.checkBox.setText(task.getName());
        holder.checkBox.setChecked(!task.getStateValue().equals(Task.State.ACTIVE));
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                task.setStateValue(Task.State.DONE);
            } else {
                task.setStateValue(Task.State.ACTIVE);
            }
            // TODO: Changes only saved on this directory, team members?
            ref.child(FirebaseProvider.getUserPath() + "/goals" + "/" + task.getId() + "/"
                    + Task.STATE).setValue(task.getState());
        });

//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, TaskActivity.class);
//            intent.putExtra(Task.ID, task.getId());
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.day_task_checkbox);
        }
    }
}
