package com.hababk.userapp.network.request;

public class FcmTokenUpdateRequest {
    private String fcm_registration_id;

    public FcmTokenUpdateRequest(String fcm_registration_id) {
        this.fcm_registration_id = fcm_registration_id;
    }
}
