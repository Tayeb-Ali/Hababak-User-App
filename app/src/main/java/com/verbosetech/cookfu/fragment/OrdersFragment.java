package com.verbosetech.cookfu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.adapter.OrdersAdapter;
import com.verbosetech.cookfu.model.Order;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.response.BaseListModel;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends BaseRecyclerFragment {
    private Context context;
    private ChefService service;
    private SharedPreferenceUtil sharedPreferences;
    private ArrayList<Order> orders;
    private OrdersAdapter ordersAdapter;

    private int pageNo = 1;
    private Call<BaseListModel<Order>> call;
    private Callback<BaseListModel<Order>> callBack = new Callback<BaseListModel<Order>>() {
        @Override
        public void onResponse(Call<BaseListModel<Order>> call, Response<BaseListModel<Order>> response) {
            if (context != null) {
                if (response.isSuccessful()) {
                    allDone = response.body().getData().isEmpty();
                    orders.addAll(response.body().getData());
                    ordersAdapter.notifyDataSetChanged();
                }
                done(response.isSuccessful());
            }
        }

        @Override
        public void onFailure(Call<BaseListModel<Order>> call, Throwable t) {
            if (context != null) {
                done(false);
            }
        }
    };

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

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orders = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(getContext(), orders, new OrdersAdapter.DetailToggleListener() {
            @Override
            public void onDetailVisible(int pos) {
                //recyclerOrders.scrollToPosition(pos);
            }
        });
        recyclerView.setAdapter(ordersAdapter);
        pageNo = 1;
        loadItems();
        swipeRefresh.setRefreshing(true);
        emptyViewText.setText("No orders found");
        Glide.with(getContext()).load(R.drawable.placeholder_restaurant).into(emptyViewImage);
    }

    @Override
    public void onRecyclerViewScrolled() {
        pageNo++;
        loadItems();
    }

    @Override
    public void onSwipeRefresh() {
        pageNo = 1;
        orders.clear();
        ordersAdapter.notifyDataSetChanged();
        allDone = false;
        loadItems();
        emptyViewContainer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void loadItems() {
        isLoading = true;
        call = service.getOrders(Helper.getApiToken(sharedPreferences), pageNo);
        call.enqueue(callBack);
    }

    private void done(boolean successful) {
        isLoading = false;
        if (swipeRefresh.isRefreshing())
            swipeRefresh.setRefreshing(false);
        emptyViewContainer.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(orders.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
