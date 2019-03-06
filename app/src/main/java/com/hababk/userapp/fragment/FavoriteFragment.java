package com.hababk.userapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hababk.userapp.R;
import com.hababk.userapp.adapter.RestaurantAdapter;
import com.hababk.userapp.model.Store;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.response.BaseListModel;
import com.hababk.userapp.network.response.Favorite;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends BaseRecyclerFragment {
    private Context context;
    private ChefService service;
    private SharedPreferenceUtil sharedPreferences;
    private ArrayList<Store> stores;
    private RestaurantAdapter restaurantsAdapter;
    private int pageNo = 1;
    private Call<BaseListModel<Favorite>> call;
    private Callback<BaseListModel<Favorite>> callBack = new Callback<BaseListModel<Favorite>>() {
        @Override
        public void onResponse(Call<BaseListModel<Favorite>> call, Response<BaseListModel<Favorite>> response) {
            if (context != null) {
                if (response.isSuccessful()) {
                    for (Favorite favorite : response.body().getData())
                        favorite.getStore().setFavourite(1);
                    allDone = response.body().getData().isEmpty();
                    stores.addAll(retrieveStores(response.body().getData()));
                    restaurantsAdapter.notifyDataSetChanged();
                }
                done(response.isSuccessful());
            }
        }

        @Override
        public void onFailure(Call<BaseListModel<Favorite>> call, Throwable t) {
            if (context != null) done(false);
        }
    };

    private void done(boolean successful) {
        isLoading = false;
        if (swipeRefresh.isRefreshing())
            swipeRefresh.setRefreshing(false);
        emptyViewContainer.setVisibility(stores.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(stores.isEmpty() ? View.GONE : View.VISIBLE);
    }

    public FavoriteFragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = ApiUtils.getClient().create(ChefService.class);
        sharedPreferences = new SharedPreferenceUtil(getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stores = new ArrayList<>();
        restaurantsAdapter = new RestaurantAdapter(getContext(), stores, null);
        recyclerView.setAdapter(restaurantsAdapter);
        pageNo = 1;
        loadItems();
        swipeRefresh.setRefreshing(true);
        emptyViewText.setText("No favorites found at the moment");
        Glide.with(getContext()).load(R.drawable.placeholder_restaurant).into(emptyViewImage);
    }

    private void loadItems() {
        isLoading = true;
        call = service.getFavorite(Helper.getApiToken(sharedPreferences), pageNo);
        call.enqueue(callBack);
    }

    @Override
    public void onRecyclerViewScrolled() {
        pageNo++;
        loadItems();
    }

    @Override
    public void onSwipeRefresh() {
        pageNo = 1;
        stores.clear();
        restaurantsAdapter.notifyDataSetChanged();
        allDone = false;
        loadItems();
        emptyViewContainer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private ArrayList<Store> retrieveStores(ArrayList<Favorite> data) {
        ArrayList<Store> toReturn = new ArrayList<>();
        for (Favorite favorite : data)
            toReturn.add(favorite.getStore());
        return toReturn;
    }

}
