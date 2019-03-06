package com.hababk.userapp.network.request;

/**
 * Created by a_man on 14-03-2018.
 */

public class BankDetailRequest {
    private String name, bank_name, ifsc, account_number;

    public BankDetailRequest(String name, String bank_name, String ifsc, String account_number) {
        this.name = name;
        this.bank_name = bank_name;
        this.account_number = account_number;
        this.ifsc = ifsc;
    }
}
