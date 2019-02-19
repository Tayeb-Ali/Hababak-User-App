package com.verbosetech.cookfu.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by a_man on 14-03-2018.
 */

public class BaseListModel<T> {
    @SerializedName("current_page")
    @Expose
    private int current_page;

    @SerializedName("last_page")
    @Expose
    private int last_page;

    @SerializedName("per_page")
    @Expose
    private int per_page;

    @SerializedName("data")
    @Expose
    private ArrayList<T> data;

    public int getCurrent_page() {
        return current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public int getPer_page() {
        return per_page;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }
}
