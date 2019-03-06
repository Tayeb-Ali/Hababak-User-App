package com.hababk.userapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hababk.userapp.network.response.MenuItem;

/**
 * Created by a_man on 26-03-2018.
 */

public class RequestItem {
    @SerializedName("order_id")
    @Expose
    private Integer order_id;
    @SerializedName("menu_item_id")
    @Expose
    private Integer menu_item_id;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("menuitem")
    @Expose
    private MenuItem menuitem;

    public Integer getOrder_id() {
        return order_id;
    }

    public Integer getMenu_item_id() {
        return menu_item_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getTotal() {
        return total;
    }

    public MenuItem getMenuitem() {
        return menuitem;
    }

    public RequestItem(Integer menu_item_id, Integer quantity, Double total) {
        this.menu_item_id = menu_item_id;
        this.quantity = quantity;
        this.total = total;
    }

    public RequestItem() {
    }

    public void setMenu_item_id(Integer menu_item_id) {
        this.menu_item_id = menu_item_id;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
