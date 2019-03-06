package com.hababk.userapp.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Route {
    @SerializedName("legs")
    @Expose
    private ArrayList<Leg> legs;
    @SerializedName("overview_polyline")
    @Expose
    private PolylineOverview overview_polyline;

    public PolylineOverview getOverview_polyline() {
        return overview_polyline;
    }

    public ArrayList<Leg> getLegs() {
        return legs;
    }
}
