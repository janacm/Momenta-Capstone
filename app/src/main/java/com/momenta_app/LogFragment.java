package com.momenta_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by Joe on 2016-01-31.
 * For Momenta
 */

public class LogFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    private String sortString = Task.DEADLINE;
    private String orderString = ASC;
    private HelperPreferences helperPreferences;
    private RecyclerView mRecyclerView;
    private ProgressBar loadingProgressBar;

    // Firebase instance variables
    private String directory = "tests";
    private DatabaseReference mFirebaseDatabaseReference;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private ArrayList<Task> overdueTasksList = new ArrayList<>();
    private ArrayList<Task> tomorrowtasksList = new ArrayList<>();
    private ArrayList<Task> next7TasksList = new ArrayList<>();
    private ArrayList<Task> laterTasksList = new ArrayList<>();
    private ArrayList<Task> ongoingTasksList = new ArrayList<>();
    private ArrayList<Task> tasksList = new ArrayList<>();
    private ArrayList<Task> veryLowTasksList = new ArrayList<>();
    private ArrayList<Task> lowTasksList = new ArrayList<>();
    private ArrayList<Task> mediumTasksList = new ArrayList<>();
    private ArrayList<Task> highTasksList = new ArrayList<>();
    private ArrayList<Task> veryHighTasksList = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    public static LogFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        LogFragment fragment = new LogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helperPreferences = new HelperPreferences(getActivity());
        sortString = helperPreferences.getPreferences(Constants.COLUMN, Task.DEADLINE);
        orderString = helperPreferences.getPreferences(Constants.ORDER, ASC);
        directory = FirebaseProvider.getUserPath() + "/goals";

        mFirebaseDatabaseReference = FirebaseProvider.getInstance().getReference();

        mFirebaseDatabaseReference.child(directory).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Task task = dataSnapshot.getValue(Task.class);
                String key = dataSnapshot.getKey();

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    tasksList.add(0, task);
                    keys.add(0, key);
                } else {
                    int previousIndex = keys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == tasksList.size()) {
                        tasksList.add(task);
                        keys.add(key);
                    } else {
                        tasksList.add(nextIndex, task);
                        keys.add(nextIndex, key);
                    }
                }
                sectionAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("LogFragment","onChildChanged");
                // One of the tasks changed. Replace it in our list and name mapping
                Task task = dataSnapshot.getValue(Task.class);
                String key = dataSnapshot.getKey();
                int index = keys.indexOf(key);
                tasksList.set(index,task);
                sectionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("LogFragment","onChildRemoved");
                // A task was removed from the list. Remove it from our list and the name mapping
                String key = dataSnapshot.getKey();
                int index = keys.indexOf(key);
                keys.remove(index);
                tasksList.remove(index);
                sectionAdapter.notifyDataSetChanged();

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // A model changed position in the list. Update our list accordingly
                String key = dataSnapshot.getKey();
                Task task = dataSnapshot.getValue(Task.class);
                int index = keys.indexOf(key);
                tasksList.remove(index);
                keys.remove(index);
                if (previousChildName == null) {
                    tasksList.add(0, task);
                    keys.add(0, key);
                } else {
                    int previousIndex = keys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == tasksList.size()) {
                        tasksList.add(task);
                        keys.add(key);
                    } else {
                        tasksList.add(nextIndex, task);
                        keys.add(nextIndex, key);
                    }
                }
                sectionAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_log, container, false);
        Log.d("LogFragment","onCreateView");

        if (FirebaseProvider.getUserPath().length() > 0) {
            loadingProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        mFirebaseDatabaseReference.child(directory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadingProgressBar.setVisibility(View.GONE);

                if (!dataSnapshot.hasChildren()) {
                    view.findViewById(R.id.empty_state_layout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("LogFragment","onViewCreated");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_recycler_view);
        setLayoutManger();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LogFragment","onResume");
        buildSortedListAdapter(sortString, orderString);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("LogFragment","setUserVisibleHint");
        sectionAdapter = new SectionedRecyclerViewAdapter();
        if(isVisibleToUser){
            buildSortedListAdapter(sortString, orderString);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.add(getString(R.string.sort_by));
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sortDialog();
                return true;
            }
        });
    }

    private void sortList(List<Task> list, String sortString, String orderString) {
        switch (sortString){
            case Task.DEADLINE:
                //Sort tasks list based on due date
                Collections.sort(list, new Comparator<Task>() {
                    @Override
                    public int compare(Task t1, Task t2) {
                        if (t1.getDeadline() > t2.getDeadline())
                            return 1;
                        if (t1.getDeadline() < t2.getDeadline())
                            return -1;
                        return 0;
                    }
                });
                break;
            case Task.NAME:
                //Sort tasks list based on name
                Collections.sort(list, new Comparator<Task>() {
                    @Override
                    public int compare(Task t1, Task t2) {
                        return (t1.getName().compareTo(t2.getName()));
                    }
                });
                break;
            case Task.PRIORITY:
                //Sort tasks list based on priority
                Collections.sort(list, new Comparator<Task>() {
                    @Override
                    public int compare(Task t1, Task t2) {
                        return t1.getPriorityValue().compareTo(t2.getPriorityValue());
                    }
                });
                break;
        }
        //Reverse tasks list to be in descending order
        if(orderString.equals(DESC))
            Collections.reverse(list);
    }

    private Date getDateInFuture(int numberOfDaysInFuture) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, numberOfDaysInFuture);
        return calendar.getTime();
    }

    private boolean findTaskInList(ArrayList<Task> list, Task taskToFind) {
        for (Task task : list) {
            if (taskToFind.getId() == task.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * On click method for the sort by menu option.
     * Inflates the sort by dialog.
     */
    private void sortDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        AlertDialog.Builder sortDialogBuilder = new AlertDialog.Builder(getContext());

        View dialogView = inflater.inflate(R.layout.sort_dialog, null);
        sortDialogBuilder.setView(dialogView)
                .setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helperPreferences.savePreferences(Constants.COLUMN, sortString);
                        helperPreferences.savePreferences(Constants.ORDER, orderString);

                        buildSortedListAdapter(sortString, orderString);

                        setLayoutManger();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        RadioGroup sortRadioGroup = (RadioGroup) dialogView.findViewById(R.id.sort_dialog_group);
        RadioGroup orderRadioGroup = (RadioGroup) dialogView.findViewById(R.id.order_dialog_group);

        sortRadioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.radio_button_name:
                                sortString = Task.NAME;
                                break;
                            case R.id.radio_button_deadline:
                                sortString = Task.DEADLINE;
                                break;
                            case R.id.radio_button_priority:
                                sortString = Task.PRIORITY;
                                break;
                        }
                    }
                }
        );

        orderRadioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int id) {
                        switch (id) {
                            case R.id.radio_button_ascending:
                                orderString = ASC;
                                break;
                            case R.id.radio_button_descending:
                                orderString = DESC;
                        }
                    }
                });

        //Check current sort and order preference
        switch (helperPreferences.getPreferences(Constants.COLUMN, Task.DEADLINE)) {
            case Task.NAME:
                sortRadioGroup.check(R.id.radio_button_name);
                break;
            case Task.DEADLINE:
                sortRadioGroup.check(R.id.radio_button_deadline);
                break;
            case Task.PRIORITY:
                sortRadioGroup.check(R.id.radio_button_priority);
                break;
        }
        switch (helperPreferences.getPreferences(Constants.ORDER, ASC)) {
            case ASC:
                orderRadioGroup.check(R.id.radio_button_ascending);
                break;
            case DESC:
                orderRadioGroup.check(R.id.radio_button_descending);
                break;
        }

        sortDialogBuilder.create().show();
    }

    private void buildSortedListAdapter(String sortString, String orderString) {
        sectionAdapter.removeAllSections();
        ongoingTasksList.clear();
        overdueTasksList.clear();
        tomorrowtasksList.clear();
        next7TasksList.clear();
        laterTasksList.clear();
        veryLowTasksList.clear();
        lowTasksList.clear();
        mediumTasksList.clear();
        highTasksList.clear();
        veryHighTasksList.clear();
        Log.d("LogFragment","buildSortedListAdapter");
        if (sortString.equals(Task.DEADLINE)) {
            for (Task task : tasksList) {
                if (task.getTypeValue() == Task.Type.ONGOING && !findTaskInList(ongoingTasksList, task)) {
                    ongoingTasksList.add(task);
                }
                else if (task.getDeadlineValue().before(Calendar.getInstance()) && !findTaskInList(overdueTasksList, task) && !findTaskInList(ongoingTasksList, task)) {
                    overdueTasksList.add(task);
                }
                else if (task.getDeadlineValue().getTime().before(getDateInFuture(2)) && !findTaskInList(tomorrowtasksList, task) && !findTaskInList(ongoingTasksList, task) && !findTaskInList(overdueTasksList, task)) {
                    tomorrowtasksList.add(task);
                }
                else if (task.getDeadlineValue().getTime().before(getDateInFuture(8)) && !findTaskInList(next7TasksList, task) && !findTaskInList(ongoingTasksList, task) && !findTaskInList(overdueTasksList, task) && !findTaskInList(tomorrowtasksList, task)) {
                    next7TasksList.add(task);
                }
                else if (!findTaskInList(laterTasksList, task) && !findTaskInList(ongoingTasksList, task) && !findTaskInList(next7TasksList, task) && !findTaskInList(tomorrowtasksList, task) && !findTaskInList(overdueTasksList, task)) {
                    laterTasksList.add(task);
                }
            }
            LogSection overdueSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.overdue_section)), overdueTasksList);
            LogSection tomorrowSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.tomorrow_section)), tomorrowtasksList);
            LogSection next7Section = new LogSection(String.valueOf("   "+getContext().getString(R.string.next7days_section)), next7TasksList);
            LogSection laterSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.later_section)), laterTasksList);
            LogSection ongoingSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.ongoing_section)), ongoingTasksList);
            sortList(overdueTasksList, Task.DEADLINE, orderString);
            sortList(laterTasksList, Task.DEADLINE, orderString);
            sortList(next7TasksList, Task.DEADLINE, orderString);

            if (overdueTasksList.size() > 0)
                sectionAdapter.addSection(overdueSection);
            if (tomorrowtasksList.size() > 0)
                sectionAdapter.addSection(tomorrowSection);
            if (next7TasksList.size() > 0)
                sectionAdapter.addSection(next7Section);
            if (laterTasksList.size() > 0)
                sectionAdapter.addSection(laterSection);
            if (ongoingTasksList.size() > 0)
                sectionAdapter.addSection(ongoingSection);

        } else if (sortString.equals(Task.NAME)) {
                sortList(tasksList, Task.NAME, orderString);
                LogSection tasksSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.tasks_section)), tasksList);
                sectionAdapter.addSection(tasksSection);

        } else if (sortString.equals(Task.PRIORITY)) {
            for (Task task : tasksList) {
                switch (task.getPriorityValue()) {
                    case VERY_LOW:
                        if(!findTaskInList(veryLowTasksList, task)) {
                            veryLowTasksList.add(task);
                        }
                        break;
                    case LOW:
                        if(!findTaskInList(lowTasksList, task)) {
                            lowTasksList.add(task);
                        }
                        break;
                    case MEDIUM:
                        if(!findTaskInList(mediumTasksList, task)) {
                            mediumTasksList.add(task);
                        }
                        break;
                    case HIGH:
                        if(!findTaskInList(highTasksList, task)) {
                            highTasksList.add(task);
                        }
                        break;
                    case VERY_HIGH:
                        if(!findTaskInList(veryHighTasksList, task)) {
                            veryHighTasksList.add(task);
                        }
                        break;
                }
            }
            LogSection veryLowSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.very_low_section)), veryLowTasksList);
            LogSection lowSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.low_section)), lowTasksList);
            LogSection mediumSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.medium_section)), mediumTasksList);
            LogSection highSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.high_section)), highTasksList);
            LogSection veryHighSection = new LogSection(String.valueOf("   "+getContext().getString(R.string.very_high_section)), veryHighTasksList);
            sortList(veryLowTasksList, Task.DEADLINE, orderString);
            sortList(lowTasksList, Task.DEADLINE, orderString);
            sortList(mediumTasksList, Task.DEADLINE, orderString);
            sortList(highTasksList, Task.DEADLINE, orderString);
            sortList(veryHighTasksList, Task.DEADLINE, orderString);

            if (veryLowTasksList.size() > 0)
                sectionAdapter.addSection(veryLowSection);
            if (lowTasksList.size() > 0)
                sectionAdapter.addSection(lowSection);
            if (mediumTasksList.size() > 0)
                sectionAdapter.addSection(mediumSection);
            if (highTasksList.size() > 0)
                sectionAdapter.addSection(highSection);
            if (veryHighTasksList.size() > 0)
                sectionAdapter.addSection(veryHighSection);

        }
        mRecyclerView.setAdapter(sectionAdapter);
        sectionAdapter.notifyDataSetChanged();
    }

    /**
     * Used to order the layout of the recycler view.
     */
    private void setLayoutManger() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderString = helperPreferences.getPreferences(Constants.ORDER, DESC);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Adds an observer too the RecyclerView.Adapter, listens for an empty adapter
     * and sets the visibility of the empty state layout.
     */
    private void addObserver() {
        RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (itemCount != 0 && getView() != null) {
                    getView().findViewById(R.id.empty_state_layout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (positionStart == 0 && getView() != null) {
                    getView().findViewById(R.id.empty_state_layout).setVisibility(View.VISIBLE);
                }
            }
        };
        sectionAdapter.registerAdapterDataObserver(mObserver);
    }


    /**
     * Sets the adapter of the recycler view
     *
     * @param adapter the new adapter to change to
     */
    public void setAdapter(final RecyclerView.Adapter adapter) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.invalidate();
            }
        });
    }

    public void setTasksList(ArrayList<Task> tasksList) {
        this.tasksList = tasksList;
    }

class LogSection extends StatelessSection {
    String title;
    List<Task> list;

    public LogSection(String title, List<Task> list) {
        // call constructor with layout resources for this Section header, footer and items
        super(R.layout.log_section_header, R.layout.list_item);

        this.title = title;
        this.list = list;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskViewHolder viewHolder = (TaskViewHolder) holder;
        // bind your view here
        final Task task = list.get(position);

        switch (task.getTypeValue()) {
            case DEADLINE:
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.todoCheckbox.setVisibility(View.INVISIBLE);
                viewHolder.deadline.setVisibility(View.VISIBLE);
                viewHolder.timeSpent.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setMax(task.getGoal());
                viewHolder.progressBar.setProgress(task.getTimeSpent());
                break;
            case ONGOING:
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.todoCheckbox.setVisibility(View.INVISIBLE);
                viewHolder.deadline.setVisibility(View.INVISIBLE);
                viewHolder.timeSpent.setVisibility(View.VISIBLE);
                break;
            case TODO:
                viewHolder.timeSpent.setVisibility(View.INVISIBLE);
                viewHolder.todoCheckbox.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.todoCheckbox.setOnCheckedChangeListener(null);
                viewHolder.todoCheckbox.setChecked(!task.getStateValue().equals(Task.State.ACTIVE));
                viewHolder.todoCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            task.setStateValue(Task.State.DONE);
                        } else {
                            task.setStateValue(Task.State.ACTIVE);
                        }
                        // TODO: Changes only saved on this directory, team members?
                        mFirebaseDatabaseReference.child(directory + "/" + task.getId() + "/"
                                + Task.STATE).setValue(task.getState());
                    }
                });
                break;
            default:
                break;

        }
        viewHolder.name.setText(task.getName());
        viewHolder.timeSpent.setText(task.getFormattedTimeSpent());
        viewHolder.deadline.setText(task.getFormattedDeadline());
        switch (task.getPriorityValue()) {
            case VERY_LOW:
                viewHolder.priority.setBackgroundResource(R.drawable.priotity_very_low_circle);
                break;
            case LOW:
                viewHolder.priority.setBackgroundResource(R.drawable.priotity_low_circle);
                break;
            case MEDIUM:
                viewHolder.priority.setBackgroundResource(R.drawable.priotity_medium_circle);
                break;
            case HIGH:
                viewHolder.priority.setBackgroundResource(R.drawable.priotity_high_circle);
                break;
            case VERY_HIGH:
                viewHolder.priority.setBackgroundResource(R.drawable.priotity_very_high_circle);
            default:
                viewHolder.priority.setBackgroundResource(R.drawable.priotity_very_high_circle);
                break;
        }
        //Set onClick listener for each activity
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TaskActivity.class);
                intent.putExtra(Task.ID, task.getId());
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        // bind your header view here
        headerHolder.tvTitle.setText(title);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        public HeaderViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.section_name);
        }
    }

    /**
     * ViewHolder class for each item in the recycler view.
     */
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView timeSpent;
        public TextRoundCornerProgressBar progressBar;
        public TextView deadline;
        public CheckBox todoCheckbox;
        public View priority;

        public TaskViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.list_item_name);
            timeSpent = (TextView) itemView.findViewById(R.id.list_item_time_spent);
            progressBar = (TextRoundCornerProgressBar) itemView.findViewById(R.id.progressBar);
            deadline = (TextView) itemView.findViewById(R.id.deadline_textView);
            todoCheckbox = (CheckBox) itemView.findViewById(R.id.toDo_checkBox);
            priority = itemView.findViewById(R.id.priority_imageView);
        }
    }
}

}