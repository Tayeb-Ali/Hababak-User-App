package com.verbosetech.cookfu.network.request;

/**
 * Created by a_man on 14-03-2018.
 */

public class SupportRequest {
    private String name, email, message;

    public SupportRequest(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }
}
