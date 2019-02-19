package com.verbosetech.cookfu.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.verbosetech.cookfu.model.User;

/**
 * Created by a_man on 12-03-2018.
 */

public class AuthResponse {
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("user")
    @Expose
    private User user;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
