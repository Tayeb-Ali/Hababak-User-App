package com.verbosetech.cookfu.network.request;

import com.verbosetech.cookfu.model.RequestItem;
import com.verbosetech.cookfu.network.response.MenuItem;

import java.util.ArrayList;

/**
 * Created by a_man on 25-03-2018.
 */

public class CreateOrderRequest {
    private Double subtotal, delivery_fee, taxes, discount, total;
    private String special_instructions, coupon;
    private Integer address_id, store_id, payment_method_id;
    private ArrayList<RequestItem> items;

    public Double getDelivery_fee() {
        return delivery_fee;
    }

    public Double getTaxes() {
        return taxes;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public void setDelivery_fee(Double delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public void setTaxes(Double taxes) {
        this.taxes = taxes;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setSpecial_instructions(String special_instructions) {
        this.special_instructions = special_instructions;
    }

    public void setAddress_id(Integer address_id) {
        this.address_id = address_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public void setPayment_method_id(Integer payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public void setItems(ArrayList<MenuItem> items) {
        this.items = new ArrayList<>();
        Double totalAmount = 0d;
        for (MenuItem menuItem : items) {
            totalAmount += menuItem.getTotal();
            this.items.add(new RequestItem(menuItem.getId(), menuItem.getQuantity(), menuItem.getTotal()));
        }
        setSubtotal(totalAmount);
        setTotal(totalAmount + getDelivery_fee() + getTaxes() - getDiscount());
    }
}
