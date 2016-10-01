package com.momenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.github.fabtransitionactivity.SheetLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener{
    private static final int REQUEST_CODE = 1;
//    private static boolean persistenceEnabled = false;

    private SheetLayout mSheetLayout;
    private FloatingActionButton fab;
    private SessionManager sm;
    private DatabaseReference mFirebaseDatabaseReference;
    private String awardsDirectory = null;

    private helperPreferences helperPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = SessionManager.getInstance(this);
        // Initialize Firebase Auth
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            awardsDirectory = mFirebaseUser.getUid() + "/awards";
        }

        mFirebaseDatabaseReference = FirebaseProvider.getInstance().getReference();

        helperPreferences = new helperPreferences(this);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ManagerFragmentPagerAdapter fragmentManager = new ManagerFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);
        viewPager.setAdapter(fragmentManager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    default:
                        fab.show();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mSheetLayout = (SheetLayout)findViewById(R.id.bottom_sheet);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSheetLayout.expandFab();
            }
        });

        mSheetLayout.setFab(fab);
        mSheetLayout.setFabAnimationEndListener(this);

        //Check if awards table is empty before. if so, fill it in
        mFirebaseDatabaseReference.child(awardsDirectory).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){
                            fillAwards();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.getGoogleApiClient().connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_signout){
            sm.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(this, AddNewTaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mSheetLayout.contractFab();
        }
    }

    @Override
    protected void onStop()
    {
        sm.getGoogleApiClient().disconnect();

        super.onStop();
    }

    private void fillAwards(){
        Map<String,Award> awardsList = new HashMap<>();
        Award commitedAward = new Award("award_commited_title", "award_commited_desc_1","", new ArrayList<>(Collections.singletonList(1)));
        commitedAward.setCurrentLevel(0);
        commitedAward.setCurrentProgress(0);
        awardsList.put(Constants.SHARE_COMMITTED_AWARD_ID,commitedAward);

        Award neophyteAward = new Award("award_neophyte_title", "award_neophyte_desc_1", "", new ArrayList<>(Collections.singletonList(1)));
        neophyteAward.setCurrentLevel(0);
        neophyteAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_NEOPHYTE_AWARD_ID,neophyteAward);

        Award trendSetterAward = new Award("award_trend_setter_title", "award_trend_setter_desc_1", "", new ArrayList<>(Collections.singletonList(1)));
        trendSetterAward.setCurrentLevel(0);
        trendSetterAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_TREND_SETTER_AWARD_ID,trendSetterAward);

        Award multiTaskerAward = new Award("award_multi_tasker_title", "award_multi_tasker_desc_1", "award_multi_tasker_desc_2", new ArrayList<>(Arrays.asList(5, 10, 25, 100, 200)));
        multiTaskerAward.setCurrentLevel(0);
        multiTaskerAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_MULTI_TASKER_AWARD_ID,multiTaskerAward);

        Award productiveAward = new Award("award_productive_title", "award_productive_desc_1", "award_productive_desc_2", new ArrayList<>(Arrays.asList(10, 50, 100, 500, 2000)));
        productiveAward.setCurrentLevel(0);
        productiveAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_PRODUCTIVE_AWARD_ID,productiveAward);

        Award perfectionistAward = new Award("award_perfectionnist_title", "award_perfectionnist_desc_1", "award_perfectionnist_desc_2", new ArrayList<>(Arrays.asList(10, 20, 50, 200, 500)));
        perfectionistAward.setCurrentLevel(0);
        perfectionistAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_PERFECTIONIST_AWARD_ID,perfectionistAward);

        Award punctualAward = new Award("award_ponctual_title", "award_ponctual_desc_1", "award_ponctual_desc_2", new ArrayList<>(Arrays.asList(5, 10, 25, 100, 200)));
        punctualAward.setCurrentLevel(0);
        punctualAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_PUNCTUAL_AWARD_ID,punctualAward);

        for (Map.Entry<String,Award> award: awardsList.entrySet()) {
            String key = mFirebaseDatabaseReference.child(awardsDirectory).push().getKey();
            award.getValue().setId(key);
            helperPreferences.savePreferences(award.getKey(),key);
            mFirebaseDatabaseReference.child(awardsDirectory + "/" + award.getValue().getId()).setValue(award.getValue());
        }

    }
}
