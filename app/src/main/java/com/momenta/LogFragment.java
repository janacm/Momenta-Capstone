package com.momenta;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Joe on 2016-01-31.
 * For Momenta
 */

// In this case, the fragment displays simple text based on the page
public class LogFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

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
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText("Fragment #" + mPage);
        return view;
    }
}