package com.verbosetech.cookfu.rest_detail;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.adapter.OrdersAdapter;
import com.verbosetech.cookfu.adapter.ReviewsAdapter;
import com.verbosetech.cookfu.fragment.BaseRecyclerFragment;
import com.verbosetech.cookfu.model.Review;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.response.BaseListModel;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewFragment extends BaseRecyclerFragment {
    private Context context;
    private Store store;
    private ChefService service;
    private SharedPreferenceUtil sharedPreferences;
    private ArrayList<Review> reviews;
    private ReviewsAdapter reviewAdapter;

    private int pageNo = 1;
    private Call<BaseListModel<Review>> call;
    private Callback<BaseListModel<Review>> callback = new Callback<BaseListModel<Review>>() {
        @Override
        public void onResponse(Call<BaseListModel<Review>> call, Response<BaseListModel<Review>> response) {
            if (context != null) {
                if (response.isSuccessful()) {
                    allDone = response.body().getData().isEmpty();
                    reviews.addAll(response.body().getData());
                    reviewAdapter.notifyDataSetChanged();
                }
                done(response.isSuccessful());
            }
        }

        @Override
        public void onFailure(Call<BaseListModel<Review>> call, Throwable t) {
            if (context != null) {
                done(false);
            }
        }
    };

    private void done(boolean successful) {
        isLoading = false;
        if (swipeRefresh.isRefreshing())
            swipeRefresh.setRefreshing(false);
        emptyViewContainer.setVisibility(reviews.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(reviews.isEmpty() ? View.GONE : View.VISIBLE);
    }

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = ApiUtils.getClient().create(ChefService.class);
        sharedPreferences = new SharedPreferenceUtil(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reviews = new ArrayList<>();
        reviewAdapter = new ReviewsAdapter(getContext(), reviews);
        recyclerView.setAdapter(reviewAdapter);
        pageNo = 1;
        loadReviews();
        swipeRefresh.setRefreshing(true);
        emptyViewText.setText("No reviews found");
        Glide.with(getContext()).load(R.drawable.placeholder_review).into(emptyViewImage);
    }

    @Override
    public void onRecyclerViewScrolled() {
        pageNo++;
        loadReviews();
    }

    @Override
    public void onSwipeRefresh() {
        pageNo = 1;
        reviews.clear();
        reviewAdapter.notifyDataSetChanged();
        allDone = false;
        loadReviews();
        emptyViewContainer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void loadReviews() {
        isLoading = true;
        call = service.getReviews(Helper.getApiToken(sharedPreferences), store.getId(), pageNo);
        call.enqueue(callback);
    }

    public static ReviewFragment newInstance(Store store) {
        ReviewFragment fragment = new ReviewFragment();
        fragment.store = store;
        return fragment;
    }
}
