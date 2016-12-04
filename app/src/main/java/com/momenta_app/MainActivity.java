package com.momenta_app;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.fabtransitionactivity.SheetLayout;
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

    //Fragment fields
    private ViewPager viewPager;
    private ManagerFragmentPagerAdapter fragmentManager;

    private SheetLayout mSheetLayout;
    private FloatingActionButton fab;
    private SessionManager sm;
    private DatabaseReference ref;
    private User user;
    private String awardsDirectory = null;

    private helperPreferences helperPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = SessionManager.getInstance(this);
        user = FirebaseProvider.getUser();
        awardsDirectory = user.getPath() + "/awards";

        ref = FirebaseProvider.getInstance().getReference();

        helperPreferences = new helperPreferences(this);
        if (user.getPath()!=null) {
            helperPreferences.savePreferences(Constants.ACCOUNT_NAME,
                    user.getPath().replace(",","."));
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentManager = new ManagerFragmentPagerAdapter(getSupportFragmentManager());
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

        //Array of tab icons
        int tabIcons[] = {R.drawable.ic_bulletin_board_black_24dp, R.drawable.ic_checkbox_multiple_marked_circle_outline_black_24dp,
                R.drawable.ic_trophy_variant_black_24dp, R.drawable.ic_chart_line_black_24dp};


        //Setting tab icons
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
            int tabIconColor = ContextCompat.getColor(viewPager.getContext(), R.color.colorNotSelected);
            tabLayout.getTabAt(i).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        }

        //Setting colour for Dashboard Tab Colour initially
        int selectedTabColour = ContextCompat.getColor(viewPager.getContext(), R.color.colorAccent);
        tabLayout.getTabAt(0).getIcon().setColorFilter(selectedTabColour, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(0).select();

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);

                        //Colour when tab is selected - use the accent colour
                        int tabIconColor = ContextCompat.getColor(viewPager.getContext(), R.color.colorAccent);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);

                        //Colour when tab is not selected
                        int tabIconColor = ContextCompat.getColor(viewPager.getContext(), R.color.colorNotSelected);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );

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

        ref.child(awardsDirectory).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){
                            fillAwards();
                        }else{//To make sure the DB and share prefs are in sync
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                String awardId = (String) postSnapshot.child("id").getValue();
                                String name = (String) postSnapshot.child("name").getValue();
                                switch (name) {
                                    case "award_neophyte_title":
                                        helperPreferences.savePreferences(Constants.SHPREF_NEOPHYTE_AWARD_ID, awardId);
                                        break;
                                    case "award_perfectionnist_title":
                                        helperPreferences.savePreferences(Constants.SHPREF_PERFECTIONIST_AWARD_ID, awardId);
                                        break;
                                    case "award_productive_title":
                                        helperPreferences.savePreferences(Constants.SHPREF_PRODUCTIVE_AWARD_ID, awardId);
                                        break;
                                    case "award_trend_setter_title":
                                        helperPreferences.savePreferences(Constants.SHPREF_TREND_SETTER_AWARD_ID, awardId);
                                        break;
                                    case "award_commited_title":
                                        helperPreferences.savePreferences(Constants.SHARE_COMMITTED_AWARD_ID, awardId);
                                        break;
                                    case "award_ponctual_title":
                                        helperPreferences.savePreferences(Constants.SHPREF_PUNCTUAL_AWARD_ID, awardId);
                                        break;
                                    case "award_multi_tasker_title":
                                        helperPreferences.savePreferences(Constants.SHPREF_MULTI_TASKER_AWARD_ID, awardId);
                                        break;
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        ref.child("users/" + user.getPath()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ( !dataSnapshot.exists() ) {
                            ref.child("users/" + user.getPath()).setValue(user);
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

    /**
     * Returns the viewPager of MainActivity
     * @return Viewpager
     */
    public ViewPager getViewerPager() {
        return viewPager;
    }

    private void fillAwards(){
        Map<String,Award> awardsList = new HashMap<>();
        Award commitedAward = new Award("award_commited_title", "award_commited_desc_1","", R.drawable.committed_badge, new ArrayList<>(Collections.singletonList(1)), new ArrayList<>(Collections.singletonList("")));
        commitedAward.setCurrentLevel(0);
        commitedAward.setCurrentProgress(0);
        awardsList.put(Constants.SHARE_COMMITTED_AWARD_ID,commitedAward);

        Award neophyteAward = new Award("award_neophyte_title", "award_neophyte_desc_1", "", R.drawable.neophyte_badge,  new ArrayList<>(Collections.singletonList(1)), new ArrayList<>(Collections.singletonList("")));
        neophyteAward.setCurrentLevel(0);
        neophyteAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_NEOPHYTE_AWARD_ID,neophyteAward);

        Award trendSetterAward = new Award("award_trend_setter_title", "award_trend_setter_desc_1", "", R.drawable.trend_setter_badge, new ArrayList<>(Collections.singletonList(5)), new ArrayList<>(Collections.singletonList("")));
        trendSetterAward.setCurrentLevel(0);
        trendSetterAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_TREND_SETTER_AWARD_ID,trendSetterAward);

        Award multiTaskerAward = new Award("award_multi_tasker_title", "award_multi_tasker_desc_1", "award_multi_tasker_desc_2", R.drawable.multi_tasker_badge, new ArrayList<>(Arrays.asList(5, 10, 25, 100, 200)), new ArrayList<>(Collections.singletonList("")));
        multiTaskerAward.setCurrentLevel(0);
        multiTaskerAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_MULTI_TASKER_AWARD_ID,multiTaskerAward);

        Award productiveAward = new Award("award_productive_title", "award_productive_desc_1", "award_productive_desc_2", R.drawable.productive_badge, new ArrayList<>(Arrays.asList(10, 50, 100, 500, 2000)), new ArrayList<>(Collections.singletonList("")));
        productiveAward.setCurrentLevel(0);
        productiveAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_PRODUCTIVE_AWARD_ID,productiveAward);

        Award perfectionistAward = new Award("award_perfectionnist_title", "award_perfectionnist_desc_1", "award_perfectionnist_desc_2", R.drawable.perfectionist_badge, new ArrayList<>(Arrays.asList(10, 20, 50, 200, 500)), new ArrayList<>(Collections.singletonList("")));
        perfectionistAward.setCurrentLevel(0);
        perfectionistAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_PERFECTIONIST_AWARD_ID,perfectionistAward);

        Award punctualAward = new Award("award_ponctual_title", "award_ponctual_desc_1", "award_ponctual_desc_2", R.drawable.punctual_badge, new ArrayList<>(Arrays.asList(5, 10, 25, 100, 200)), new ArrayList<>(Collections.singletonList("")));
        punctualAward.setCurrentLevel(0);
        punctualAward.setCurrentProgress(0);
        awardsList.put(Constants.SHPREF_PUNCTUAL_AWARD_ID,punctualAward);

        for (Map.Entry<String,Award> award: awardsList.entrySet()) {
            String key = ref.child(awardsDirectory).push().getKey();
            award.getValue().setId(key);
            helperPreferences.savePreferences(award.getKey(),key);
            ref.child(awardsDirectory + "/" + award.getValue().getId()).setValue(award.getValue());
        }

    }
}
