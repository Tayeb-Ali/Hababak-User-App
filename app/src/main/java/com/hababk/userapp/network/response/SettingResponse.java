package com.hababk.userapp.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("value")
    @Expose
    private String value;

    public SettingResponse(String key) {
        this.key = key;
    }

    public Integer getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingResponse)) return false;
        SettingResponse that = (SettingResponse) o;
        return getKey().equals(that.getKey());
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
