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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Joe on 2016-01-31.
 * For Momenta
 */

public class LogFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    private String sortString = Task.LAST_MODIFIED;
    private String orderString = ASC;
    private HelperPreferences helperPreferences;
    private RecyclerView mRecyclerView;
    private ProgressBar loadingProgressBar;

    // Firebase instance variables
    private String directory = "tests";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Task, TaskViewHolder> mFirebaseAdapter;

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

        directory = FirebaseProvider.getUserPath() + "/goals";

        // New child entries
        mFirebaseDatabaseReference = FirebaseProvider.getInstance().getReference();
        mFirebaseAdapter = buildAdapter(helperPreferences.getPreferences(Constants.COLUMN, Task.LAST_MODIFIED));

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_log, container, false);

        if ( FirebaseProvider.getUserPath().length() > 0 ) {
            loadingProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_recycler_view);

        setLayoutManger();
        mRecyclerView.setAdapter(mFirebaseAdapter);

        mFirebaseDatabaseReference.child(directory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadingProgressBar.setVisibility(View.GONE);

                if (!dataSnapshot.hasChildren()) {
                    view.findViewById(R.id.empty_state_layout).setVisibility(View.VISIBLE);
                }

                addObserver();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
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

    /**
     * On click method for the sort by menu option.
     * Inflates the sort by dialog.
     */
    private void sortDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        AlertDialog.Builder sortDialogBuilder = new AlertDialog.Builder(getContext());

        View dialogView =  inflater.inflate(R.layout.sort_dialog, null);
        sortDialogBuilder.setView(dialogView)
                .setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helperPreferences.savePreferences(Constants.COLUMN, sortString);
                        helperPreferences.savePreferences(Constants.ORDER, orderString);

                        mFirebaseAdapter = buildAdapter(sortString);
                        mFirebaseAdapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(mFirebaseAdapter);

                        setLayoutManger();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        RadioGroup sortRadioGroup = (RadioGroup)dialogView.findViewById(R.id.sort_dialog_group);
        RadioGroup orderRadioGroup = (RadioGroup)dialogView.findViewById(R.id.order_dialog_group);


        sortRadioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch ( checkedId ) {
                            case R.id.radio_button_name:
                                sortString = Task.NAME;
                                break;
                            case R.id.radio_button_date_created:
                                sortString = Task.DATE_CREATED;
                                break;
                            case R.id.radio_button_deadline:
                                sortString = Task.DEADLINE;
                                break;
                            case R.id.radio_button_last_modified:
                                sortString = Task.LAST_MODIFIED;
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
        switch ( helperPreferences.getPreferences(Constants.COLUMN, Task.LAST_MODIFIED) ) {
            case Task.NAME:
                sortRadioGroup.check(R.id.radio_button_name);
                break;
            case Task.LAST_MODIFIED:
                sortRadioGroup.check(R.id.radio_button_last_modified);
                break;
            case Task.DATE_CREATED:
                sortRadioGroup.check(R.id.radio_button_date_created);
                break;
            case Task.DEADLINE:
                sortRadioGroup.check(R.id.radio_button_deadline);
                break;
        }
        switch ( helperPreferences.getPreferences(Constants.ORDER, ASC)) {
            case ASC:
                orderRadioGroup.check(R.id.radio_button_ascending);
                break;
            case DESC:
                orderRadioGroup.check(R.id.radio_button_descending);
                break;
        }

        sortDialogBuilder.create().show();
    }

    /**
     * Helper method to build a recycler adapter
     * @param sortBy the field to sort the tasks by
     * @return an adapter
     */
    private FirebaseRecyclerAdapter<Task, TaskViewHolder> buildAdapter(String sortBy) {
        return new FirebaseRecyclerAdapter<Task, TaskViewHolder>(
                Task.class,
                R.layout.list_item,
                TaskViewHolder.class,
                mFirebaseDatabaseReference.child(directory).orderByChild(sortBy)) {

            @Override
            protected void populateViewHolder(TaskViewHolder viewHolder,
                                              Task task, int position) {
                switch (task.getTypeValue()){
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

            }

            @Override
            public void onBindViewHolder(TaskViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);

                final Task task = getItem(position);
                //Set onClick listener for each activity
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), TaskActivity.class);
                        intent.putExtra(Task.ID, task.getId());
                        getContext().startActivity(intent);
                    }
                });
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
            }

        };
    }

    /**
     * Used to order the layout of the recycler view.
     */
    private void setLayoutManger() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderString = helperPreferences.getPreferences(Constants.ORDER, DESC);

        if ( orderString.equals(DESC) ) {
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            mRecyclerView.setLayoutManager(layoutManager);
        }

    }

    /**
     *  Adds an observer too the RecyclerView.Adapter, listens for an empty adapter
     *  and sets the visibility of the empty state layout.
     */
    private void addObserver() {
        RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (itemCount != 0 && getView()!=null) {
                    getView().findViewById(R.id.empty_state_layout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (positionStart == 0 && getView()!=null) {
                    getView().findViewById(R.id.empty_state_layout).setVisibility(View.VISIBLE);
                }
            }
        };
        mFirebaseAdapter.registerAdapterDataObserver(mObserver);
    }

    /**
     * ViewHolder class for each item in the recycler view.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
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

    /**
     * Sets the adapter of the recycler view
     * @param adapter the new adapter to change to
     *  TODO: Get ride of method
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
}