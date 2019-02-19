package com.verbosetech.cookfu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.model.CategoryFood;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.network.response.StoreDetail;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by a_man on 31-01-2018.
 */

public class CuisineAdapter extends RecyclerView.Adapter<CuisineAdapter.MyViewHolder> {
    private StoreDetail storeDetail;
    private Context context;
    private ArrayList<CategoryFood> dataList;
    private Store savedStore;

    private CuisineListInteractor listInteractor;
    private ArrayList<MenuItem> cartItems;
    private SharedPreferenceUtil sharedPreferenceUtil;

    public CuisineAdapter(Context context, SharedPreferenceUtil sharedPreferenceUtil, StoreDetail storeDetail, CuisineListInteractor listInteractor) {
        this.context = context;
        this.listInteractor = listInteractor;
        this.storeDetail = storeDetail;
        this.dataList = new ArrayList<>();
        for (CategoryFood categoryFood : storeDetail.getStore().getCategories()) {
            ArrayList<MenuItem> menuItems = new ArrayList<>();
            for (MenuItem storeItem : storeDetail.getMenu_items()) {
                if (storeItem.getCategories().contains(categoryFood)) {
                    menuItems.add(storeItem);
                }
            }
            if (!menuItems.isEmpty()) {
                categoryFood.setMenuItems(menuItems);
                this.dataList.add(categoryFood);
            }
        }

        this.sharedPreferenceUtil = sharedPreferenceUtil;
        this.cartItems = Helper.getCart(this.sharedPreferenceUtil);
        this.savedStore = Helper.getCartStore(sharedPreferenceUtil);
        broadcastCart();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cuisine_cat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void refreshCartItems() {
        this.cartItems.clear();
        this.cartItems.addAll(Helper.getCart(this.sharedPreferenceUtil));
        broadcastCart();
        for (CategoryFood categoryFood : dataList) {
            categoryFood.setSelected(false);
            for (MenuItem menuItem : categoryFood.getMenuItems()) {
                menuItem.setAdded(false);
            }
        }
        notifyDataSetChanged();
    }

    private void broadcastCart() {
        Intent intent = new Intent(Constants.BROADCAST_CART);
        intent.putExtra("data", cartItems);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout rootLayout;
        private TextView categoryFood;
        private ImageView cuisineItemToggle;
        private RecyclerView cuisineItemList;

        MyViewHolder(View itemView) {
            super(itemView);
            cuisineItemList = itemView.findViewById(R.id.cuisineItemList);
            categoryFood = itemView.findViewById(R.id.categoryFood);
            cuisineItemToggle = itemView.findViewById(R.id.cuisineItemToggle);
            rootLayout = itemView.findViewById(R.id.rootLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    CategoryFood category = dataList.get(pos);
                    category.setSelected(!category.isSelected());
                    Collections.swap(dataList, 0, pos);
                    notifyItemMoved(category.isSelected() ? pos : 0, category.isSelected() ? 0 : pos);
                    listInteractor.OnListExpanded(category.isSelected());
                    notifyItemChanged(0);
                }
            });
        }

        public void setData(CategoryFood category) {
            categoryFood.setText(category.getTitle());
            cuisineItemToggle.setImageDrawable(ContextCompat.getDrawable(context, category.isSelected() ? R.drawable.ic_keyboard_arrow_up_accent_24dp : R.drawable.ic_keyboard_arrow_down_accent_24dp));

            cuisineItemList.setLayoutManager(new LinearLayoutManager(context));

            cuisineItemList.setAdapter(new RestaurantMenuAdapter(context, storeDetail.getStore(), savedStore, category.getMenuItems(), cartItems));
            cuisineItemList.setVisibility(category.isSelected() ? View.VISIBLE : View.GONE);

            ViewGroup.LayoutParams layoutParams = rootLayout.getLayoutParams();
            layoutParams.height = category.isSelected() ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
            rootLayout.setLayoutParams(layoutParams);
        }
    }

    public interface CuisineListInteractor {
        void OnListExpanded(boolean selected);
    }
}
