package com.momenta;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.github.fabtransitionactivity.SheetLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener{
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";

    private ManagerFragmentPagerAdapter fragmentManager;
    private SheetLayout mSheetLayout;
    private FloatingActionButton fab;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = SessionManager.getInstance(this);
        // Initialize Firebase Auth
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch Sign in anonymously
            startActivity(new Intent(this, EmailPasswordActivity.class));
            finish();
            return;
        }

//        networkStateReceiver = new NetworkStateReceiver();
//        networkStateReceiver.addListener(this);
//        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

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

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    //<<<<<<< HEAD
    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(this, AddNewTaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mSheetLayout.contractFab();
        }
//    public void networkAvailable() {
//        Log.d(TAG, "Network Available");
//        //TODO: When network connectivity becomes available, upload all tasks to server. UNCOMMENT next line to do so
//        //HelperNetwork.uploadTasksToServer(this);
//    }

//    @Override
//    public void networkUnavailable() {
//        Log.d(TAG, "Network Unavailable");
//        //Show networ unavailable status
//    }
//    @Override
//    protected void onStop()
//    {
//        sm.getGoogleApiClient().disconnect();
//        try{
//            unregisterReceiver(networkStateReceiver);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        super.onStop();
    }
}
