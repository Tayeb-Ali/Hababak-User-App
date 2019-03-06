package com.hababk.userapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hababk.userapp.network.response.MenuItem;

import java.util.ArrayList;

/**
 * Created by a_man on 20-01-2018.
 */

public class CategoryFood implements Parcelable {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("stores_count")
    @Expose
    private long stores_count;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("title")
    @Expose
    private String title;

    private ArrayList<MenuItem> menuItems;

    public ArrayList<MenuItem> getMenuItems() {
        if (menuItems == null) menuItems = new ArrayList<>();
        return menuItems;
    }

    public void setMenuItems(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    private boolean selected;

    protected CategoryFood(Parcel in) {
        id = in.readLong();
        stores_count = in.readLong();
        image_url = in.readString();
        title = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<CategoryFood> CREATOR = new Creator<CategoryFood>() {
        @Override
        public CategoryFood createFromParcel(Parcel in) {
            return new CategoryFood(in);
        }

        @Override
        public CategoryFood[] newArray(int size) {
            return new CategoryFood[size];
        }
    };

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getId() {
        return id;
    }

    public long getStores_count() {
        return stores_count;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(stores_count);
        dest.writeString(image_url);
        dest.writeString(title);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryFood)) return false;

        CategoryFood that = (CategoryFood) o;

        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }
}
