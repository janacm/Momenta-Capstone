package com.momenta;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by Joe on 2016-01-31.
 * For Momenta
 */
public class ManagerFragmentPagerAdapter  extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Dashboard","Log", "Awards", "Trends" };
    private Context context;
    private DashboardFragment dashboardFragment;
    private LogFragment logFragment;
    private AwardsFragment awardsFragment;
    private StatsFragment statsFragment;

    public ManagerFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            //Change to Dashboradfragment
            if (dashboardFragment != null) {
                return dashboardFragment;
            } else {
                dashboardFragment = DashboardFragment.newInstance(position + 1);
                return dashboardFragment;
            }
        }else if(position == 1){
            //Change to Logfragment
            if (logFragment != null) {
                return logFragment;
            } else {
                logFragment = LogFragment.newInstance(position + 1);
                return logFragment;
            }
        }else if(position == 2){
            //Change to AwardsFragment
            if (awardsFragment != null) {
                return awardsFragment;
            } else {
                awardsFragment = AwardsFragment.newInstance(position + 1);
                return awardsFragment;
            }
        }else if(position == 3){
            //Change to StatsFragment
            if (statsFragment != null) {
                return statsFragment;
            } else {
                statsFragment = StatsFragment.newInstance(position + 1);
                return statsFragment;
            }
        }

        return LogFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }


}