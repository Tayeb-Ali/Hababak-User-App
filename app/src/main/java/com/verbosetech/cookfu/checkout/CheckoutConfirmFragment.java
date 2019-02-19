package com.verbosetech.cookfu.checkout;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.network.response.CouponResponse;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CheckoutConfirmFragment extends Fragment {
    private ArrayList<MenuItem> cartItems;
    private Store store;
    private CouponResponse couponResponse;

    private String currency;
    private int deliveryFee = 0, serviceCharge = 0;

    private RecyclerView itemRecycler;
    private TextView total, subTotal, feeService, feeDelivery, couponPrice, couponText;
    private View couponContainer;

    public CheckoutConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(getContext());
        currency = Helper.getSetting(sharedPreferenceUtil, "currency");
        if (TextUtils.isEmpty(currency)) {
            currency = "";
        } else {
            currency = " " + currency;
        }
        String delivery_fee = Helper.getSetting(sharedPreferenceUtil, "delivery_fee");
        String admin_fee_for_order_in_percent = Helper.getSetting(sharedPreferenceUtil, "admin_fee_for_order_in_percent");
        if (!TextUtils.isEmpty(delivery_fee)) {
            try {
                deliveryFee = Integer.parseInt(delivery_fee);
            } catch (NumberFormatException ex) {
                deliveryFee = 0;
            }
        }
        if (!TextUtils.isEmpty(admin_fee_for_order_in_percent)) {
            try {
                serviceCharge = Integer.parseInt(admin_fee_for_order_in_percent);
            } catch (NumberFormatException ex) {
                serviceCharge = 0;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout_confirm, container, false);
        ((TextView) view.findViewById(R.id.restName)).setText(store.getName());
        ((TextView) view.findViewById(R.id.restAddress)).setText(store.getAddress());
        feeService = view.findViewById(R.id.feeService);
        feeDelivery = view.findViewById(R.id.feeDelivery);
        itemRecycler = view.findViewById(R.id.itemRecycler);
        total = view.findViewById(R.id.total);
        subTotal = view.findViewById(R.id.subTotal);
        couponContainer = view.findViewById(R.id.couponContainer);
        couponText = view.findViewById(R.id.couponText);
        couponPrice = view.findViewById(R.id.couponPrice);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feeDelivery.setText(String.valueOf(deliveryFee));

        itemRecycler.setNestedScrollingEnabled(false);
        itemRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        itemRecycler.setAdapter(new CheckoutSummaryAdapter(getContext(), cartItems));

        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        if (couponResponse != null) {
            couponText.setText(String.format("Coupon (%s) applied", couponResponse.getCode()));
            couponPrice.setText(decimalFormat.format(couponResponse.getReward()) + currency);
            couponContainer.setVisibility(View.VISIBLE);
        } else {
            couponContainer.setVisibility(View.GONE);
        }

        Double totalAmount = 0d;
        for (MenuItem menuItem : cartItems)
            totalAmount += menuItem.getTotal();

        if (couponResponse != null) {
            totalAmount = totalAmount - (couponResponse.getType().equals("fixed") ? couponResponse.getReward() : ((totalAmount * couponResponse.getReward()) / 100));
            if (totalAmount < 0) totalAmount = 0d;
        }
        subTotal.setText(decimalFormat.format(totalAmount) + currency);
        double sc = (totalAmount * serviceCharge / 100);
        feeService.setText(decimalFormat.format(sc) + currency);
        total.setText(decimalFormat.format(totalAmount + sc + deliveryFee) + currency);
    }

    public static CheckoutConfirmFragment newInstance(ArrayList<MenuItem> cartItems, CouponResponse couponResponse, Store store) {
        CheckoutConfirmFragment fragment = new CheckoutConfirmFragment();
        fragment.cartItems = cartItems;
        fragment.couponResponse = couponResponse;
        fragment.store = store;
        return fragment;
    }
}
