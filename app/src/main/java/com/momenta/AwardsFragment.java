package com.momenta;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Joe on 2016-02-01.
 * For Momenta
 */
public class AwardsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static AwardsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        AwardsFragment fragment = new AwardsFragment();
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
        View view = inflater.inflate(R.layout.fragment_awards, container, false);
        TextView textView = (TextView) view.findViewById(R.id.textView2);
        textView.setText("Fragment #" + mPage);
        return view;
    }
}
