package com.momenta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


/**
 * Created by Joe on 2016-02-01.
 * For Momenta
 */
public class AwardsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    WaterWaveProgress waveProgress;


    public static AwardsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        AwardsFragment fragment = new AwardsFragment();
        fragment.setArguments(args);
        return fragment;
    }

   // @Override
   // public void onCreate(Bundle savedInstanceState) {
   //     super.onCreate(savedInstanceState);
   //     mPage = getArguments().getInt(ARG_PAGE);
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_awards, container, false);
       // super.onCreate(savedInstanceState);
        SeekBar bar = (SeekBar) view.findViewById(R.id.seekBar1);
        waveProgress = (WaterWaveProgress) view.findViewById(R.id.waterWaveProgress1);
        waveProgress.setShowProgress(true);
        waveProgress.animateWave();
        bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                waveProgress.setProgress(progress);

            }
        });
        ((CheckBox)view.findViewById(R.id.checkBox1)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                waveProgress.setShowProgress(isChecked);
            }
        });
        ((CheckBox)view.findViewById(R.id.checkBox2)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                waveProgress.setShowNumerical(isChecked);
            }
        });
        return view;
    }

}
