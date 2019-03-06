package com.hababk.userapp.model;

/**
 * Created by a_man on 20-01-2018.
 */

public class NavItem {
    String mTitle;
    String mSubtitle;
    int mIcon;

    public NavItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmSubtitle() {
        return mSubtitle;
    }

    public int getmIcon() {
        return mIcon;
    }
}
