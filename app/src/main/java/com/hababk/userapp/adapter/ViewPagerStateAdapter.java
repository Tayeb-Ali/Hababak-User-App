package com.hababk.userapp.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a_man on 24-01-2018.
 */

public class ViewPagerStateAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragmentsList = new ArrayList<>();
    private final List<String> fragmentsTitleList = new ArrayList<>();

    public ViewPagerStateAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentsTitleList.get(position);
    }

    public void addFrag(@NonNull Fragment fragment, @NonNull String title) {
        fragmentsList.add(fragment);
        fragmentsTitleList.add(title);
    }
}
