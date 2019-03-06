package com.hababk.userapp.model;

import android.os.Build;

import java.util.Objects;

public class RefineSetting {
    private boolean vegOnly;
    private String cost_for_two_sort; //asc dsc
    private int cost_for_two_min, cost_for_two_max;

    public RefineSetting(boolean vegOnly, String cost_for_two_sort, int cost_for_two_min, int cost_for_two_max) {
        this.vegOnly = vegOnly;
        this.cost_for_two_sort = cost_for_two_sort;
        this.cost_for_two_min = cost_for_two_min;
        this.cost_for_two_max = cost_for_two_max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefineSetting)) return false;
        RefineSetting that = (RefineSetting) o;
        return isVegOnly() == that.isVegOnly() &&
                getCost_for_two_min() == that.getCost_for_two_min() &&
                getCost_for_two_max() == that.getCost_for_two_max() &&
                getCost_for_two_sort().equals(that.getCost_for_two_sort());
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(isVegOnly(), getCost_for_two_sort(), getCost_for_two_min(), getCost_for_two_max());
        } else {
            return (getCost_for_two_max() + getCost_for_two_min()) * ((isVegOnly() ? 1 : 2) * (getCost_for_two_sort().equals("asc") ? 3 : 4));
        }
    }

    public boolean isVegOnly() {
        return vegOnly;
    }

    public void setVegOnly(boolean vegOnly) {
        this.vegOnly = vegOnly;
    }

    public String getCost_for_two_sort() {
        return cost_for_two_sort;
    }

    public void setCost_for_two_sort(String cost_for_two_sort) {
        this.cost_for_two_sort = cost_for_two_sort;
    }

    public int getCost_for_two_min() {
        return cost_for_two_min;
    }

    public void setCost_for_two_min(int cost_for_two_min) {
        this.cost_for_two_min = cost_for_two_min;
    }

    public int getCost_for_two_max() {
        return cost_for_two_max;
    }

    public void setCost_for_two_max(int cost_for_two_max) {
        this.cost_for_two_max = cost_for_two_max;
    }

    public static RefineSetting getDefault() {
        return new RefineSetting(false, "asc", 1, 1000);
    }
}
