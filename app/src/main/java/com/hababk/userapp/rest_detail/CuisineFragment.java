package com.hababk.userapp.rest_detail;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hababk.userapp.network.response.StoreDetail;
import com.hababk.userapp.util.MyLinearLayoutManager;
import com.hababk.userapp.R;
import com.hababk.userapp.adapter.CuisineAdapter;
import com.hababk.userapp.util.SharedPreferenceUtil;

public class CuisineFragment extends Fragment {
    private RecyclerView cuisineRecycler;
    private MyLinearLayoutManager linearLayoutManager;

    private StoreDetail restaurantDetail;
    private CuisineAdapter cuisineAdapter;
    private SharedPreferenceUtil sharedPreferenceUtil;

    public CuisineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceUtil = new SharedPreferenceUtil(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuisine, container, false);
        cuisineRecycler = view.findViewById(R.id.cuisineRecycler);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupCuisineRecycler();
    }

    public void refreshCart() {
        if (cuisineAdapter != null) cuisineAdapter.refreshCartItems();
    }

    private void setupCuisineRecycler() {
        linearLayoutManager = new MyLinearLayoutManager(getContext());
        cuisineRecycler.setLayoutManager(linearLayoutManager);

        cuisineAdapter = new CuisineAdapter(getContext(), sharedPreferenceUtil, restaurantDetail, new CuisineAdapter.CuisineListInteractor() {
            @Override
            public void OnListExpanded(final boolean selected) {
                if (selected) {
                    cuisineRecycler.scrollToPosition(0);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutManager.setScrollEnabled(!selected);
                    }
                }, 500);
            }
        });
        cuisineRecycler.setAdapter(cuisineAdapter);
    }

    public static CuisineFragment newInstance(StoreDetail restaurantDetail) {
        CuisineFragment fragment = new CuisineFragment();
        fragment.restaurantDetail = restaurantDetail;
        return fragment;
    }
}
