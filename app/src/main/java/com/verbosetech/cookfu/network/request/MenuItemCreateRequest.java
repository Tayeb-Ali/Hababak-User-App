package com.verbosetech.cookfu.network.request;

/**
 * Created by a_man on 14-03-2018.
 */

public class MenuItemCreateRequest {
    private String title, detail, specification, image_url;
    private Double price;
    private int is_available, is_non_veg, category_id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getIs_available() {
        return is_available;
    }

    public void setIs_available(int is_available) {
        this.is_available = is_available;
    }

    public int getIs_non_veg() {
        return is_non_veg;
    }

    public void setIs_non_veg(int is_non_veg) {
        this.is_non_veg = is_non_veg;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
