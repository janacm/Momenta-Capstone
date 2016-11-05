package com.momenta;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


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

    private int mPage;
    private View activityView;
    private NumberPicker numberPicker;
    private Button button;
    private RoundCornerProgressBar progressBar;
    private TextView totalTimeSpent;
    private TextView totalGoalTime;

    private TextView displayNameText;
    private ImageView imgView;

    private helperPreferences helperPreferences;
    private DashboardTaskStatsAdapter dAdapter;
    public RecyclerView dRecyclerView;
    DatabaseReference mDatabaseReference;


    // Firebase instance variables
    private String directory = "tests";

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
        mPage = getArguments().getInt(ARG_PAGE);
        mDatabaseReference = FirebaseProvider.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        button = (Button)activityView.findViewById(R.id.button1);
        button.setOnClickListener(this);
        totalTimeSpent = (TextView) activityView.findViewById(R.id.dash_goals_total_time_spent_value);
        totalGoalTime = (TextView) activityView.findViewById(R.id.dash_goals_total_goal_value);
        progressBar = (RoundCornerProgressBar) activityView.findViewById(R.id.dash_goals_progress_bar);
        dRecyclerView = (RecyclerView) activityView.findViewById(R.id.dashboard_tasks_stats_recycler_view);
        dRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getActivity().getIntent().getExtras();
        if(args != null) {
            String name = args.getString("displayName");
            Log.d("pls", name + " test");
            String photo = args.getString("personPhoto");
            displayNameText = (TextView) activityView.findViewById(R.id.displayName);
            displayNameText.setText(name);
            imgView = (ImageView) activityView.findViewById(R.id.userImage);
            Picasso.with(getActivity()).load(photo).into(imgView);
        }

        helperPreferences = new helperPreferences(getActivity());

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            directory = mFirebaseUser.getUid() + "/goals";
        }

        // New child entries
        mDatabaseReference.child(directory).addValueEventListener(
                new ValueEventListener() {
                    //TODO Change to ChildEventListener --> More efficient
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
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

                        /***Updating the Goal Progress card's fields**/
                        progressBar.setMax(totalGoal);
                        progressBar.setPadding(10);
                        progressBar.setProgress(totalTime);

                        if(isAdded()) {
                            progressBar.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.total_time_goal));
                            progressBar.setProgressColor(ContextCompat.getColor(getContext(), R.color.total_time_spent));
                        }

                        totalTimeSpent.setText(String.valueOf(totalTime));
                        totalGoalTime.setText(String.valueOf(totalGoal));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        return activityView;
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
