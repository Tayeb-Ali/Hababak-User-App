package com.hababk.userapp.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 14-03-2018.
 */

public class BankDetailResponse {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int user_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bank_name")
    @Expose
    private String bank_name;
    @SerializedName("account_number")
    @Expose
    private String account_number;
    @SerializedName("ifsc")
    @Expose
    private String ifsc;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getBank_name() {
        return bank_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public String getIfsc() {
        return ifsc;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
