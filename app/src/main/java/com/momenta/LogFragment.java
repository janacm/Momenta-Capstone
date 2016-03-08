package com.momenta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Joe on 2016-01-31.
 * For Momenta
 */

public class LogFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText newActivity;

    private int mPage;

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
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ActivitiesAdapter(this.getContext());
        mRecyclerView.setAdapter(mAdapter);

        newActivity = (EditText) view.findViewById(R.id.new_activity_edit_text);

        return view;
    }

    /**
     * Call back method for the add activity button on log fragment.
     * This method is called by the addButton in MainActivity.java
     */
    public void addActivity() {
        //If the text box is empty do nothing.
        if ( !newActivity.getText().toString().trim().isEmpty() ) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View alertView = inflater.inflate(R.layout.activity_alert_dialog, null);
            TimeDialogBuilder timeDialogBuilder = new TimeDialogBuilder(this, alertView,
                    newActivity.getText().toString().trim());
            AlertDialog alertDialog = timeDialogBuilder.getAlertDialog();
            alertDialog.show();
        }
    }

    /**
     * Public method to update the LogFragment view after an activity has been added
     * This method retrieves clears the recycyler view and retrieves all the activites from the db,
     * Clears the new activity edit text.
     */
    public void updateView( ) {
        ((ActivitiesAdapter) mAdapter).retrieveTasks();
        newActivity.setText("");
    }
}