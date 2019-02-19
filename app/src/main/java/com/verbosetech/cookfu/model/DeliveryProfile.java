package com.verbosetech.cookfu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryProfile implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_online")
    @Expose
    private Integer is_online;
    @SerializedName("user_id")
    @Expose
    private Integer user_id;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("user")
    @Expose
    private User user;

    protected DeliveryProfile(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            is_online = null;
        } else {
            is_online = in.readInt();
        }
        if (in.readByte() == 0) {
            user_id = null;
        } else {
            user_id = in.readInt();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<DeliveryProfile> CREATOR = new Creator<DeliveryProfile>() {
        @Override
        public DeliveryProfile createFromParcel(Parcel in) {
            return new DeliveryProfile(in);
        }

        @Override
        public DeliveryProfile[] newArray(int size) {
            return new DeliveryProfile[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public Integer getIs_online() {
        return is_online;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public User getUser() {
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (is_online == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(is_online);
        }
        if (user_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(user_id);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        dest.writeParcelable(user, flags);
    }
}
