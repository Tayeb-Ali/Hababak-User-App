package com.verbosetech.cookfu.model;

/**
 * Created by a_man on 31-01-2018.
 */

public class CuisineCategory {
    private String title;
    private boolean selected;

    public CuisineCategory(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
