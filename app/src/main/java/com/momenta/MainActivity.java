package com.momenta;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ManagerFragmentPagerAdapter fragementManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragementManager = new ManagerFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);
        viewPager.setAdapter(fragementManager);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
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

        return super.onOptionsItemSelected(item);
    }

    //TODO move this function to the settings section
    public void numberPicker(View v) {
        Log.d("MainActivity", "Button Pressed");
        Fragment fragment = fragementManager.getItem(0);
        if (fragment == null || fragment.isVisible() ) {
            Log.d("MainActivty", "Chai.");
        }
        Log.d("MainActivty", "Fragment Retrieved.");
        if (fragment != null && fragment.isVisible()) {
            if (fragment instanceof DashboardFragment) {
            }
        }
        Log.d("MainActivty", "Request deispatched");
    }

    /**
     * Call back method for the add activity button on the log fragement
     * This method call the addActivity() method in LogFragment.java
     * @param v The view off the button
     */
    public void addButton(View v) {
        Fragment fragment = fragementManager.getItem(1);
        if ( fragment instanceof  LogFragment ) {
            ( (LogFragment) fragment).addActivity();
        }
    }

}
