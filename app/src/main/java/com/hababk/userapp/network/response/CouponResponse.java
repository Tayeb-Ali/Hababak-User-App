package com.hababk.userapp.network.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CouponResponse implements Parcelable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("reward")
    @Expose
    private Double reward;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("expires_at")
    @Expose
    private String expires_at;
    @SerializedName("created_at")
    @Expose
    private String created_at;

    protected CouponResponse(Parcel in) {
        id = in.readInt();
        code = in.readString();
        if (in.readByte() == 0) {
            reward = null;
        } else {
            reward = in.readDouble();
        }
        type = in.readString();
        expires_at = in.readString();
        created_at = in.readString();
    }

    public static final Creator<CouponResponse> CREATOR = new Creator<CouponResponse>() {
        @Override
        public CouponResponse createFromParcel(Parcel in) {
            return new CouponResponse(in);
        }

        @Override
        public CouponResponse[] newArray(int size) {
            return new CouponResponse[size];
        }
    };

    public boolean isExpired() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return new Date().getTime() > simpleDateFormat.parse(getExpires_at()).getTime();
        } catch (ParseException pe) {
            pe.printStackTrace();
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Double getReward() {
        return reward == null ? 0 : reward;
    }

    public String getType() {
        return type;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
        if (reward == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(reward);
        }
        dest.writeString(type);
        dest.writeString(expires_at);
        dest.writeString(created_at);
    }
}