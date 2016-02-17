package com.momenta;
import android.test.ActivityInstrumentationTestCase2;
import android.support.test.runner.AndroidJUnit4;

import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import org.junit.runner.RunWith;

/**
 * Created by joesi on 2016-02-16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
    }

    @SmallTest
    public void textTextViewNotNull(){
        TextView textView = (TextView) activity.findViewById(R.id.textView2);
        assertNotNull(textView);
    }

    @SmallTest
    public void textTextViewNotNull2(){
        TextView textView = (TextView) activity.findViewById(R.id.textView2);
        assertNotNull(textView);
    }

    @SmallTest
    public void textTextViewNotNull3(){
        TextView textView = (TextView) activity.findViewById(R.id.textView2);
        assertNotNull(textView);
    }
}