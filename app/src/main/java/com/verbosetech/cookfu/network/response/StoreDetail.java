package com.verbosetech.cookfu.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.verbosetech.cookfu.model.Store;

import java.util.ArrayList;

/**
 * Created by a_man on 24-03-2018.
 */

public class StoreDetail {
    @SerializedName("store")
    @Expose
    private Store store;
    @SerializedName("menu_items")
    @Expose
    private ArrayList<MenuItem> menu_items;

    private ArrayList<CatSubCat> catSubCats;

    public Store getStore() {
        return store;
    }

    public ArrayList<MenuItem> getMenu_items() {
        return menu_items;
    }

    private class CatSubCat {

    }
}
