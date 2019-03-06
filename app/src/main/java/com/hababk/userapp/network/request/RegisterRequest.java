package com.hababk.userapp.network.request;

/**
 * Created by a_man on 12-03-2018.
 */

public class RegisterRequest {
    private String name, email, password, mobile_number, role = "customer";

    public RegisterRequest(String name, String email, String password, String mobile_number) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile_number = mobile_number;
    }

    public String getMobile_number() {
        return mobile_number;
    }
}
