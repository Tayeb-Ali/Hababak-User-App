package com.verbosetech.cookfu.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GoogleMapsRoute {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("routes")
    @Expose
    private ArrayList<Route> routes;

    public String getStatus() {
        return status;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }
}
