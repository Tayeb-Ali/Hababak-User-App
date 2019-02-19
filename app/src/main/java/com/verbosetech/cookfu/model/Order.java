package com.verbosetech.cookfu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.verbosetech.cookfu.network.response.Address;

import java.util.ArrayList;

/**
 * Created by a_man on 24-01-2018.
 */

public class Order implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("payment_method_id")
    @Expose
    private Integer payment_method_id;
    @SerializedName("address_id")
    @Expose
    private Integer address_id;
    @SerializedName("store_id")
    @Expose
    private Integer store_id;
    @SerializedName("user_id")
    @Expose
    private Integer user_id;
    @SerializedName("delivery_profile")
    @Expose
    private DeliveryProfile delivery_profile;
    @SerializedName("subtotal")
    @Expose
    private Double subtotal;
    @SerializedName("taxes")
    @Expose
    private Double taxes;
    @SerializedName("delivery_fee")
    @Expose
    private Double delivery_fee;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("discount")
    @Expose
    private Double discount;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("special_instructions")
    @Expose
    private String special_instructions;
    @SerializedName("payment_status")
    @Expose
    private String payment_status;
    @SerializedName("delivery_status")
    @Expose
    private String delivery_status;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("orderitems")
    @Expose
    private ArrayList<RequestItem> orderitems;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("store")
    @Expose
    private Store store;
    @SerializedName("address")
    @Expose
    private Address address;

    protected Order(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            payment_method_id = null;
        } else {
            payment_method_id = in.readInt();
        }
        if (in.readByte() == 0) {
            address_id = null;
        } else {
            address_id = in.readInt();
        }
        if (in.readByte() == 0) {
            store_id = null;
        } else {
            store_id = in.readInt();
        }
        if (in.readByte() == 0) {
            user_id = null;
        } else {
            user_id = in.readInt();
        }
        delivery_profile = in.readParcelable(DeliveryProfile.class.getClassLoader());
        if (in.readByte() == 0) {
            subtotal = null;
        } else {
            subtotal = in.readDouble();
        }
        if (in.readByte() == 0) {
            taxes = null;
        } else {
            taxes = in.readDouble();
        }
        if (in.readByte() == 0) {
            delivery_fee = null;
        } else {
            delivery_fee = in.readDouble();
        }
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readDouble();
        }
        if (in.readByte() == 0) {
            discount = null;
        } else {
            discount = in.readDouble();
        }
        created_at = in.readString();
        updated_at = in.readString();
        special_instructions = in.readString();
        payment_status = in.readString();
        delivery_status = in.readString();
        status = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        store = in.readParcelable(Store.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public User getUser() {
        return user;
    }

    public Store getStore() {
        return store;
    }

    public Address getAddress() {
        return address;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPayment_method_id() {
        return payment_method_id;
    }

    public Integer getAddress_id() {
        return address_id;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public DeliveryProfile getDelivery_profile() {
        return delivery_profile;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public Double getTaxes() {
        return taxes;
    }

    public Double getDelivery_fee() {
        return delivery_fee;
    }

    public Double getTotal() {
        if (total == null || total < 0)
            total = 0d;
        return total;
    }

    public Double getDiscount() {
        return discount == null ? 0d : discount;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getSpecial_instructions() {
        return special_instructions;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<RequestItem> getOrderitems() {
        return orderitems;
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
        if (payment_method_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(payment_method_id);
        }
        if (address_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(address_id);
        }
        if (store_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(store_id);
        }
        if (user_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(user_id);
        }
        dest.writeParcelable(delivery_profile, flags);
        if (subtotal == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(subtotal);
        }
        if (taxes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(taxes);
        }
        if (delivery_fee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(delivery_fee);
        }
        if (total == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(total);
        }
        if (discount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(discount);
        }
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(special_instructions);
        dest.writeString(payment_status);
        dest.writeString(delivery_status);
        dest.writeString(status);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(store, flags);
        dest.writeParcelable(address, flags);
    }
}
