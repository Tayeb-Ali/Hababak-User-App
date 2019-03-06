package com.hababk.userapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hababk.userapp.R;
import com.hababk.userapp.model.Review;

import java.util.ArrayList;

/**
 * Created by a_man on 24-01-2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Review> dataList;

    public ReviewsAdapter(Context context, ArrayList<Review> reviews) {
        this.context = context;
        this.dataList = reviews;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_review, parent, false));
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
        private TextView name, location, reviewText, ratingText;
        private RatingBar ratingBar;

        MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.reviewName);
            location = itemView.findViewById(R.id.reviewLocation);
            reviewText = itemView.findViewById(R.id.reviewText);
            ratingText = itemView.findViewById(R.id.ratingText);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void setData(Review review) {
            name.setText(review.getStore() != null ? review.getStore().getName() : context.getString(R.string.loading));
            location.setText(review.getStore() != null ? review.getStore().getTagline() : context.getString(R.string.loading));
            reviewText.setText(review.getReview());
            ratingText.setText(String.valueOf(review.getRating()));
            ratingBar.setRating(review.getRating());
            name.setSelected(true);
            location.setSelected(true);
        }
    }
}
