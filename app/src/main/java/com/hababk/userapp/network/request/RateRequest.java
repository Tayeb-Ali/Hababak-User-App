package com.hababk.userapp.network.request;

public class RateRequest {
    private int rating;
    private String review;

    public RateRequest(int rating, String review) {
        this.rating = rating;
        this.review = review;
    }
}
