package com.momenta;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

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
        button = (Button)activityView.findViewById(R.id.button1);
        button.setOnClickListener(this);
        super.onCreate(savedInstanceState);

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
