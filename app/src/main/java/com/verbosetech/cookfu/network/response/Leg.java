package com.verbosetech.cookfu.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Leg {
    @SerializedName("distance")
    @Expose
    private Value distance;
    @SerializedName("duration")
    @Expose
    private Value duration;

    public Value getDistance() {
        return distance;
    }

    public Value getDuration() {
        return duration;
    }
}
