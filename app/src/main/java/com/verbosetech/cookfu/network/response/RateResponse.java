package com.verbosetech.cookfu.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RateResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("store_id")
    @Expose
    private Integer store_id;
    @SerializedName("user_id")
    @Expose
    private Integer user_id;
    @SerializedName("review")
    @Expose
    private String review;
}
