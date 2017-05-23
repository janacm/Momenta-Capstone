package com.momenta_app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Joe on 2016-02-01.
 * For Momenta
 */
public class DashboardFragment extends Fragment implements View.OnClickListener{
    public static final String ARG_PAGE = "ARG_PAGE";

    private RoundCornerProgressBar progressBar;
    private TextView totalTimeSpent;
    private TextView totalGoalTime;

    private ProgressBar loadingProgressBar;

    private HelperPreferences helperPreferences;
    private DashboardTaskStatsAdapter dAdapter;
    public RecyclerView dRecyclerView;
    DatabaseReference mDatabaseReference;
    User mUser;


    // Firebase instance variables
    private String directory = null;

    public static DashboardFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseProvider.getInstance().getReference();
        helperPreferences = new HelperPreferences(getActivity());

        mUser = FirebaseProvider.getUser();
        if (mUser != null) {
            directory = mUser.getPath() + "/goals";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View activityView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Button button = (Button) activityView.findViewById(R.id.button1);
        if (mUser.getPath()!= null) {
            loadingProgressBar = (ProgressBar)activityView.findViewById(R.id.progressBar);
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        button.setOnClickListener(this);
        totalTimeSpent = (TextView) activityView.findViewById(R.id.dash_goals_total_time_spent_value);
        totalGoalTime = (TextView) activityView.findViewById(R.id.dash_goals_total_goal_value);
        progressBar = (RoundCornerProgressBar) activityView.findViewById(R.id.dash_goals_progress_bar);
        dRecyclerView = (RecyclerView) activityView.findViewById(R.id.dashboard_tasks_stats_recycler_view);
        dRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dRecyclerView.setNestedScrollingEnabled(false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String name = user.getDisplayName();
            Uri photo = user.getPhotoUrl();

            TextView displayNameText = (TextView) activityView.findViewById(R.id.displayName);
            displayNameText.setText(name);

            ImageView imgView = (ImageView) activityView.findViewById(R.id.userImage);
            Glide.with(this).load(photo).into(imgView);
        }

        // New child entries
        mDatabaseReference.child(directory).addValueEventListener(
                new ValueEventListener() {
                    //TODO Change to ChildEventListener --> More efficient
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        loadingProgressBar.setVisibility(View.GONE);
                        Integer totalTime = 0;
                        Integer totalGoal = 0;

                        //List to store tasks to be displayed in dashboard stats
                        List<Task> tasks = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            totalTime += snapshot.child(Task.TIME_SPENT).getValue(Integer.class);
                            totalGoal += snapshot.child(Task.GOAL).getValue(Integer.class);

                            Task task = new Task();
                            task.setId((String) snapshot.child("id").getValue());
                            task.setName((String) snapshot.child("name").getValue());
                            task.setGoal(snapshot.child("goal").getValue(Integer.class));
                            task.setDeadline((Long) snapshot.child("deadline").getValue());
                            task.setDateCreated((Long) snapshot.child("dateCreated").getValue());
                            task.setLastModified((Long) snapshot.child("lastModified").getValue());
                            task.setTimeSpent(snapshot.child("timeSpent").getValue(Integer.class));
                            task.setPriority((String) snapshot.child("priority").getValue());

                            // Add task to the list
                            tasks.add(task);
                        }

                        //Sort ArrayList items based on last modified task
                        Collections.sort(tasks, new Comparator<Task>() {
                            @Override
                            public int compare(Task t1, Task t2) {
                                if (t1.getLastModified() > t2.getLastModified())
                                    return 1;
                                if (t1.getLastModified() < t2.getLastModified())
                                    return -1;
                                return 0;
                            }
                        });

                        //Reverse tasks list to be in descending order
                        Collections.reverse(tasks);

                        //Sublist of top 5 tasks is displayed in adapter
                        if (!tasks.isEmpty()) {
                            if (tasks.size() <= 5) {
                                tasks = tasks.subList(0, tasks.size());
                            } else {
                                tasks = tasks.subList(0, 4);
                            }
                        }

                        dAdapter = new DashboardTaskStatsAdapter(getContext(), tasks);
                        dRecyclerView.setAdapter(dAdapter);

                        if (isAdded()) {
                            /***Updating the Goal Progress card's fields**/
                            progressBar.setMax(totalGoal);
                            progressBar.setPadding(10);
                            progressBar.setProgress(totalTime);
                            progressBar.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.total_time_goal));
                            progressBar.setProgressColor(ContextCompat.getColor(getContext(), R.color.total_time_spent));

                            int ttsh = totalTime / 60;
                            int ttsm = totalTime % 60;

                            int tgh = totalGoal / 60;
                            int tgm = totalGoal % 60;

                            totalTimeSpent.setText(timeSetText(ttsh, ttsm));
                            totalGoalTime.setText(timeSetText(tgh, tgm));

                            if (getView() != null) {
                                getView().findViewById(R.id.awardsCard).setVisibility(View.VISIBLE);
                                getView().findViewById(R.id.goalsCompletedCard).setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        return activityView;
    }

    /**
     * Convenience method for setting the time related values for the goal and time spent fields
     * @param hours hour value that is being set
     * @param minutes minute value that is being set
     * @return the string containing the minute and hour values that will be set in the TextView
     */
    private String timeSetText(int hours, int minutes) {
        if (minutes > 1 && hours > 1) {
            return hours + " " + getResources().getString(R.string.add_time_to_task_hours) + " & " +
                    minutes + " " +  getResources().getString(R.string.add_time_to_task_minutes) ;
        } else if (minutes == 0 && hours > 1) {
            return hours + " " + getResources().getString(R.string.add_time_to_task_hours);
        }
        else if (minutes == 1 && hours > 1) {
            return hours + " " + getResources().getString(R.string.add_time_to_task_hours) + " & " +
                    minutes + " " + getResources().getString(R.string.add_time_to_task_minutes);
        }
        else if (hours == 1 && minutes > 1) {
            return hours + " " + getResources().getString(R.string.add_time_to_task_hours) + " & " +
                    minutes + " " + getResources().getString(R.string.add_time_to_task_minutes);
        }
        else if (hours == 1 && minutes == 1) {
            return hours + " " + getResources().getString(R.string.add_time_to_task_hours) + " & " +
                    minutes + " " + getResources().getString(R.string.add_time_to_task_minutes);
        }
        else if (hours == 1 && minutes == 0) {
            return hours + " " + getResources().getString(R.string.add_time_to_task_hours);
        }
        else if (minutes == 1 && hours == 0) {
            return minutes + " " + getResources().getString(R.string.add_time_to_task_minutes);
        }
        else {
            return minutes + " " + getResources().getString(R.string.add_time_to_task_minutes);
        }
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.button1:
                Intent intent = new Intent(this.getContext(), SelectTasksActivity.class);
                startActivity(intent);
                break;
        }
    }
}
