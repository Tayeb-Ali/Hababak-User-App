package com.hababk.userapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 24-01-2018.
 */

public class Review {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("review")
    @Expose
    private String review;
    @SerializedName("store")
    @Expose
    private Store store;
    @SerializedName("user_id")
    @Expose
    private Integer user_id;
    @SerializedName("rating")
    @Expose
    private int rating;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    public Integer getId() {
        return id;
    }


    public String getReview() {
        return review;
    }

    public Store getStore() {
        return store;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public int getRating() {
        return rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
