package com.hababk.userapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by a_man on 23-03-2018.
 */

public class Store implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("owner_id")
    @Expose
    private Integer owner_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("tagline")
    @Expose
    private String tagline;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("delivery_time")
    @Expose
    private String delivery_time;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("minimum_order")
    @Expose
    private Double minimum_order;
    @SerializedName("delivery_fee")
    @Expose
    private Double delivery_fee;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("preorder")
    @Expose
    private int preorder;
    @SerializedName("favourite")
    @Expose
    private int favourite;
    @SerializedName("ratings")
    @Expose
    private String ratings;
    @SerializedName("categories")
    @Expose
    private ArrayList<CategoryFood> categories;

    public Store(int id) {
        this.id = id;
    }

    protected Store(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            owner_id = null;
        } else {
            owner_id = in.readInt();
        }
        name = in.readString();
        tagline = in.readString();
        image_url = in.readString();
        delivery_time = in.readString();
        details = in.readString();
        area = in.readString();
        address = in.readString();
        if (in.readByte() == 0) {
            minimum_order = null;
        } else {
            minimum_order = in.readDouble();
        }
        if (in.readByte() == 0) {
            delivery_fee = null;
        } else {
            delivery_fee = in.readDouble();
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
        preorder = in.readInt();
        favourite = in.readInt();
        ratings = in.readString();
        categories = in.createTypedArrayList(CategoryFood.CREATOR);
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    public String getName() {
        return name;
    }

    public String getTagline() {
        return tagline;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getDelivery_time() {
        return delivery_time + " mins";
    }

    public String getDetails() {
        return details;
    }

    public String getArea() {
        return area;
    }

    public String getAddress() {
        return address;
    }

    public Double getMinimum_order() {
        return minimum_order;
    }

    public Double getDelivery_fee() {
        return delivery_fee;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public int getPreorder() {
        return preorder;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    public String getRatings() {
        if (ratings == null)
            ratings = "0";
        return ratings;
    }

    public ArrayList<CategoryFood> getCategories() {
        if (categories == null)
            categories = new ArrayList<>();
        return categories;
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
        if (owner_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(owner_id);
        }
        dest.writeString(name);
        dest.writeString(tagline);
        dest.writeString(image_url);
        dest.writeString(delivery_time);
        dest.writeString(details);
        dest.writeString(area);
        dest.writeString(address);
        if (minimum_order == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(minimum_order);
        }
        if (delivery_fee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(delivery_fee);
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
        dest.writeInt(preorder);
        dest.writeInt(favourite);
        dest.writeString(ratings);
        dest.writeTypedList(categories);
    }
}
