package com.verbosetech.cookfu.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 15-03-2018.
 */

public class RestaurantProfile {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile_number")
    @Expose
    private String mobile_number;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("address")
    @Expose
    private String address;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getAddress() {
        return address;
    }
}
