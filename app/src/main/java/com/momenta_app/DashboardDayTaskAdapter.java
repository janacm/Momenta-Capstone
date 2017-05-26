package com.momenta_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class DashboardDayTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Task> tasks;
    private DatabaseReference ref;
    private Context context;

    public DashboardDayTaskAdapter(DatabaseReference ref, List<Task> tasks) {
        this.tasks = tasks;
        this.ref = ref;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.dashboard_task_stats_item, parent, false);
                return new DeadlineViewHolder(view);
            case 1:
                view = inflater.inflate(R.layout.dashboard_day_task_item, parent, false);
                return new TodoViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Task task = tasks.get(position);

        switch (holder.getItemViewType()) {
            case 0:
                DeadlineViewHolder deadlineViewHolder = (DeadlineViewHolder)holder;
                deadlineViewHolder.textview.setText(task.getName());
                deadlineViewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, TaskActivity.class);
                    intent.putExtra(Task.ID, task.getId());
                    context.startActivity(intent);
                });
                break;
            case 1:
                TodoViewHolder todoViewHolder = (TodoViewHolder)holder;
                todoViewHolder.checkBox.setText(task.getName());
                boolean isChecked = !task.getStateValue().equals(Task.State.ACTIVE);
                if (isChecked) {
                    todoViewHolder.checkBox.setPaintFlags(todoViewHolder.checkBox.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                todoViewHolder.checkBox.setChecked(isChecked);
                todoViewHolder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) {
                        task.setStateValue(Task.State.DONE);
                        todoViewHolder.checkBox.setPaintFlags(todoViewHolder.checkBox.getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG);

                    } else {
                        task.setStateValue(Task.State.ACTIVE);
                        todoViewHolder.checkBox.setPaintFlags(todoViewHolder.checkBox.getPaintFlags()
                                & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    // TODO: Changes only saved on this directory, team members?
                    ref.child(FirebaseProvider.getUserPath() + "/goals" + "/" + task.getId() + "/"
                            + Task.STATE).setValue(task.getState());
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Task t = tasks.get(position);
        int result = 0;
        if (t.getTypeValue().equals(Task.Type.TODO)) {
            result = 1;
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        public final CheckBox checkBox;

        public TodoViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.day_task_checkbox);
        }
    }

    public static class DeadlineViewHolder extends RecyclerView.ViewHolder {
        public final TextView textview;

        public DeadlineViewHolder(View itemView) {
            super(itemView);
            textview = (TextView) itemView.findViewById(R.id.dash_task_stat_item_task_name);
        }
    }
}
