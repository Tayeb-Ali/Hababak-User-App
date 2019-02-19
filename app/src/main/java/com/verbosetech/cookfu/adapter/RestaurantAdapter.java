package com.verbosetech.cookfu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.model.CategoryFood;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.response.FavoriteResponse;
import com.verbosetech.cookfu.rest_detail.RestaurantDetailActivity;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by a_man on 22-01-2018.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {
    private FoodCategoryAdapter.FoodCategoryClickListener foodCategoryClickListener;
    private Context context;
    private ArrayList<Store> dataList;
    private ArrayList<CategoryFood> categoryFoods;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ChefService service;
    private String currency;

    public RestaurantAdapter(Context context, ArrayList<Store> stores, FoodCategoryAdapter.FoodCategoryClickListener foodCategoryClickListener) {
        this.context = context;
        this.dataList = stores;
        this.foodCategoryClickListener = foodCategoryClickListener;
        this.categoryFoods = new ArrayList<>();
        this.sharedPreferenceUtil = new SharedPreferenceUtil(context);
        this.service = ApiUtils.getClient().create(ChefService.class);

        currency = Helper.getSetting(sharedPreferenceUtil, "currency");
        if (TextUtils.isEmpty(currency)) {
            currency = "";
        } else {
            currency = " " + currency;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getId() == -1 ? 0 : 1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new RestaurantViewHolder(LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false));
        else
            return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_food_category_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setFoodCategories(ArrayList<CategoryFood> categoryFoods) {
        this.categoryFoods.clear();
        this.categoryFoods.addAll(categoryFoods);
    }

    public class CategoryViewHolder extends MyViewHolder {
        private RecyclerView recyclerFood;

        CategoryViewHolder(View itemView) {
            super(itemView);
            recyclerFood = itemView.findViewById(R.id.recyclerFood);
        }

        @Override
        public void setData(Store restaurant) {
            recyclerFood.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerFood.setAdapter(new FoodCategoryAdapter(context, categoryFoods, foodCategoryClickListener));
        }
    }

    public class RestaurantViewHolder extends MyViewHolder {
        private TextView name, description, restMinOrderPrice, restDeliveryTime, ratingText, restDeliveryFee;
        private ImageView imageView, itemFavorite;
        private DecimalFormat decimalFormat;

        RestaurantViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.restName);
            description = itemView.findViewById(R.id.restDesc);
            restMinOrderPrice = itemView.findViewById(R.id.restMinOrderPrice);
            imageView = itemView.findViewById(R.id.restRes);
            restDeliveryTime = itemView.findViewById(R.id.restDeliveryTime);
            itemFavorite = itemView.findViewById(R.id.itemFavorite);
            ratingText = itemView.findViewById(R.id.ratingText);
            restDeliveryFee = itemView.findViewById(R.id.restDeliveryFee);
            decimalFormat = new DecimalFormat("###.##");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != -1) {
                        Store store = dataList.get(pos);
                        context.startActivity(RestaurantDetailActivity.newIntent(context, store));
                    }
                }
            });
            itemFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != -1) {
                        Store store = dataList.get(pos);
                        store.setFavourite(store.getFavourite() == 1 ? 0 : 1);
                        notifyItemChanged(pos);
                        markFavorite(store);
                    }
                }
            });
        }

        @Override
        public void setData(Store restaurant) {
            name.setText(restaurant.getName());
            name.setSelected(true);
            description.setText(restaurant.getDetails());
            restDeliveryTime.setText(restaurant.getDelivery_time());
            restDeliveryFee.setText(decimalFormat.format(restaurant.getDelivery_fee()) + currency);
            restMinOrderPrice.setText(decimalFormat.format(restaurant.getMinimum_order()) + currency);
            Glide.with(context).load(restaurant.getImage_url()).apply(new RequestOptions().placeholder(R.drawable.placeholder_food)).into(imageView);
            itemFavorite.setImageDrawable(ContextCompat.getDrawable(context, restaurant.getFavourite() == 1 ? R.drawable.ic_favorite_accent_24dp : R.drawable.ic_favorite_white_24dp));
            ratingText.setText(String.valueOf(restaurant.getRatings()));
        }

        private void markFavorite(Store store) {
            service.markFavorite(Helper.getApiToken(sharedPreferenceUtil), store.getId()).enqueue(new Callback<FavoriteResponse>() {
                @Override
                public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                    response.isSuccessful();
                }

                @Override
                public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                    t.getMessage();
                }
            });
        }
    }

    public abstract class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void setData(Store restaurant);
    }
}
