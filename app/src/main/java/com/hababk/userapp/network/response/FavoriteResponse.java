package com.hababk.userapp.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 25-03-2018.
 */

public class FavoriteResponse {
    @SerializedName("favourite")
    @Expose
    private int favourite;

    public int getFavourite() {
        return favourite;
    }
}
