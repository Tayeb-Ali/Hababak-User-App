package com.hababk.userapp.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a_man on 25-03-2018.
 */

public class CreateOrderResponse {
    @SerializedName("subtotal")
    @Expose
    private Double subtotal;
    @SerializedName("taxes")
    @Expose
    private Double taxes;
    @SerializedName("delivery_fee")
    @Expose
    private Double delivery_fee;
    @SerializedName("discount")
    @Expose
    private Double discount;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("address_id")
    @Expose
    private Integer address_id;
    @SerializedName("store_id")
    @Expose
    private Integer store_id;
    @SerializedName("payment_method_id")
    @Expose
    private Integer payment_method_id;
    @SerializedName("user_id")
    @Expose
    private Integer user_id;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("special_instructions")
    @Expose
    private String special_instructions;
    @SerializedName("created_at")
    @Expose
    private String created_at;

    public Double getSubtotal() {
        return subtotal;
    }

    public Double getTaxes() {
        return taxes;
    }

    public Double getDelivery_fee() {
        return delivery_fee;
    }

    public Double getDiscount() {
        return discount;
    }

    public Double getTotal() {
        return total;
    }

    public Integer getAddress_id() {
        return address_id;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public Integer getPayment_method_id() {
        return payment_method_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Integer getId() {
        return id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getSpecial_instructions() {
        return special_instructions;
    }

    public String getCreated_at() {
        return created_at;
    }
}
