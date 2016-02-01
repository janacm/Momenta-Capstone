package com.momenta;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Joe on 2016-01-31.
 * For Momenta
 */
public class ManagerFragmentPagerAdapter  extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Dashboard","Log", "Awards", "Trends" };
    private Context context;

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
            return DashboardFragment.newInstance(position + 1);
        }else if(position == 1){
            //Change to Logfragment
            return LogFragment.newInstance(position + 1);
        }else if(position == 2){
            //Change to AwardsFragment
            return AwardsFragment.newInstance(position + 1);
        }else if(position == 3){
            //Change to StatsFragment
            return StatsFragment.newInstance(position + 1);
        }

        return LogFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}