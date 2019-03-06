package com.hababk.userapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hababk.userapp.R;
import com.hababk.userapp.model.CategoryFood;

import java.util.ArrayList;

/**
 * Created by a_man on 20-01-2018.
 */

public class FoodCategoryAdapter extends RecyclerView.Adapter<FoodCategoryAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CategoryFood> dataList;
    private FoodCategoryClickListener foodCategoryClickListener;

    FoodCategoryAdapter(Context context, ArrayList<CategoryFood> categoryFoods, FoodCategoryClickListener foodCategoryClickListener) {
        this.context = context;
        this.dataList = categoryFoods;
        this.foodCategoryClickListener = foodCategoryClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_food_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, count;
        private ImageView image;

        MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_food_category_name);
            count = itemView.findViewById(R.id.item_food_category_count);
            image = itemView.findViewById(R.id.item_food_category_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (foodCategoryClickListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != -1) {
                            foodCategoryClickListener.foodCategorySelected(dataList.get(pos));
                        }
                    }
                }
            });
        }

        public void setData(CategoryFood categoryFood) {
            name.setText(categoryFood.getTitle());
            count.setText(categoryFood.getStores_count() + " restaurants");
            Glide.with(context).load(categoryFood.getImage_url()).apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(16)).placeholder(R.drawable.placeholder_restaurant)).into(image);
        }
    }

    public interface FoodCategoryClickListener {
        void foodCategorySelected(CategoryFood categoryFood);
    }
}
