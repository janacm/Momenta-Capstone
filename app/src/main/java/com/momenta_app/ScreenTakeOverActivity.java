package com.momenta_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Full-screen activity that pops up at user defined intervals,
 * prompting the user to log time
 */
public class ScreenTakeOverActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    // Firebase Instance
    private DatabaseReference databaseReference;

    private HelperPreferences helperPreferences;

    public Button goButton;
    private List<Task> taskList;

    TextView quoteSpace, quoteCreditSpace;
    public String[] quoteArray, quoteCreditArray;
    int quoteCreditNumber;
    String quoteCredit, displayQuote;
    //Random generator to randomly pick from a given list of quotes
    private static final Random rgen = new Random();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        //Hold all quotes and quoteCredits in the following arrays for local use
        quoteArray = res.getStringArray(R.array.quotes);
        quoteCreditArray = res.getStringArray(R.array.quoteCredits);

        //Choose a random quote from the Strings.xml file
        displayQuote = quoteArray[rgen.nextInt(quoteArray.length)];

        //Subtract one because of indexing, or we can just start the indexing at 0 in the string.xml file
        quoteCreditNumber = Integer.parseInt(displayQuote.substring(0,1)) - 1;
        //Get the whole quote except the index number added at the front
        displayQuote = displayQuote.substring(1);
        //Quotes should have a number infront of them. Using this number is how we distinguish who said the particular quote
        quoteCredit = quoteCreditArray[quoteCreditNumber];

        helperPreferences = new HelperPreferences(this);

        setContentView(R.layout.activity_screen_take_over);

        //Set random quote and quoteCredit in the activity
        quoteSpace = (TextView)findViewById(R.id.quoteSpace);
        quoteSpace.setText(displayQuote);
        quoteCreditSpace = (TextView)findViewById(R.id.quoteCredit);
        quoteCreditSpace.setText(quoteCredit);

        mVisible = true;
        mContentView = findViewById(R.id.st_fullscreen_content);

        goButton = (Button)findViewById(R.id.dummy_button);
        goButton.setOnTouchListener(mDelayHideTouchListener);
        taskList = new ArrayList<>();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        FirebaseDatabase firebaseDatabase = FirebaseProvider.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child( FirebaseProvider.getUserPath() + "/goals").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot wholeData) {
                        for (DataSnapshot dataSnapshot: wholeData.getChildren()) {
                            Task task = new Task();
                            task.setId( (String)dataSnapshot.child("id").getValue() );
                            task.setName( (String)dataSnapshot.child("name").getValue() );
                            task.setGoal( dataSnapshot.child("goal").getValue(Integer.class) );
                            task.setDeadline( (Long)dataSnapshot.child("deadline").getValue() );
                            task.setDateCreated( (Long)dataSnapshot.child("dateCreated").getValue() );
                            task.setLastModified( (Long)dataSnapshot.child("lastModified").getValue() );
                            task.setTimeSpent( dataSnapshot.child("timeSpent").getValue(Integer.class) );
                            task.setPriority( (String)dataSnapshot.child("priority").getValue() );

                            taskList.add(task);
                        }

                        if(taskList.size() == 0){
                            goButton.setText(getString(R.string.dummy_button_add_new_task));
                            TextView noTasksAvailable = (TextView)findViewById(R.id.no_tasks_available_text);
                            noTasksAvailable.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );


        //Ring the device
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(this.getApplicationContext(), uri);
        ringtone.play();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void go_button_click(View view) {

        if (taskList.size() > 1) {
            Intent intent = new Intent(this, SelectTasksActivity.class);
            startActivity(intent);
        } else if (taskList.size() == 1) {
            HashMap<String, String> map = new HashMap<>();
            Task task = taskList.get(0);
            map.put(task.getId(), task.getName());

            Bundle extras = new Bundle();
            extras.putSerializable("HASH_MAP", map);
            Intent intent = new Intent(this, AddTaskTimeActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, AddNewTaskActivity.class);
            startActivity(intent);
        }
    }

    public void later_button_click(View view) {
        helperPreferences.savePreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_MINS,String.valueOf(Integer.parseInt(helperPreferences.getPreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_MINS,"0"))+Integer.parseInt(helperPreferences.getPreferences(Constants.SHPREF_INTERVAL_MINS,"0"))));
        helperPreferences.savePreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_HOURS,String.valueOf(Integer.parseInt(helperPreferences.getPreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_HOURS,"0"))+Integer.parseInt(helperPreferences.getPreferences(Constants.SHPREF_INTERVAL_HOURS,"0"))));
        Log.d("mins",helperPreferences.getPreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_HOURS,"0")+","+helperPreferences.getPreferences(Constants.SHPREF_INTERVAL_OVER_SNOOZE_MINS,"0"));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}