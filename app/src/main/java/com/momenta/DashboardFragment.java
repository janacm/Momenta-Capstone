package com.momenta;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    private helperPreferences helperPreferences;



    // Firebase instance variables
    private String directory = "tests";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Task, LogFragment.TaskViewHolder> mFirebaseAdapter;

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



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activityView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        super.onCreate(savedInstanceState);
        helperPreferences = new helperPreferences(getActivity());

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            directory = mFirebaseUser.getUid() + "/goals";
        }


        // New child entries
        mFirebaseDatabaseReference = FirebaseProvider.getInstance().getReference();

        mFirebaseDatabaseReference.child(directory).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snap) {
                        Integer totalTime = 0;
                        Integer totalGoal = 0;
                        for (DataSnapshot data : snap.getChildren()) {
                            totalTime += data.child(Task.TIME_SPENT).getValue(Integer.class);
                            totalGoal += data.child(Task.GOAL).getValue(Integer.class);
                        }

                        TextView timeSpent = (TextView)activityView.findViewById(R.id.totalTimeSpent);
                        timeSpent.setText(totalTime.toString());

                        TextView goal = (TextView)activityView.findViewById(R.id.goalTime);
                        goal.setText(totalGoal.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        Bundle args = getActivity().getIntent().getExtras();

        String name = args.getString("displayName");
        Log.d("pls", name + " test");
        String email = args.getString("email");

        String photo = args.getString("personPhoto");

        TextView displayNameText = (TextView)activityView.findViewById(R.id.displayName);
        displayNameText.setText(name);

        TextView emailText = (TextView)activityView.findViewById(R.id.email);
        emailText.setText(email);

        ImageView imgView = (ImageView)activityView.findViewById(R.id.imageView);
        Picasso.with(getActivity()).load(photo).into(imgView);

        return activityView;
    }

    @Override
    public void onClick(View v) {
        //  switch ( v.getId() ) {
        //      case R.id.button1:
        //         Intent intent = new Intent(this.getContext(), SelectTasksActivity.class);
        //         startActivity(intent);
        //          break;
        // }
    }
}
