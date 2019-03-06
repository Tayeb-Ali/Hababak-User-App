package com.hababk.userapp.model;

/**
 * Created by a_man on 22-01-2018.
 */

public class Restaurant {
    private String name, description;
    private int imageRes;

    public Restaurant(String name, String description, int imageRes) {
        this.name = name;
        this.description = description;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageRes() {
        return imageRes;
    }
}
