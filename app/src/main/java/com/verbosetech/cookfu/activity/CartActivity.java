package com.verbosetech.cookfu.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.adapter.CartAdapter;
import com.verbosetech.cookfu.checkout.CheckoutActivity;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.response.CouponResponse;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by a_man on 23-01-2018.
 */

public class CartActivity extends AppCompatActivity implements CartAdapter.CartTotalChangeListener {
    private RecyclerView cartRecycler;
    private LinearLayout totalContainer;
    private View emptyView, couponContainer, promoCodeView;
    private ImageView emptyViewImage;
    private TextView emptyViewText, subtotal, total, cartItemsCount, feeServicePercent, feeService, feeDelivery, couponText, couponPrice;
    private String currency;
    private int deliveryFee = 0, serviceCharge = 0;
    private ArrayList<MenuItem> cartItems;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private CouponResponse coupon;
    private CartAdapter cartAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initUi();

        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        cartItems = Helper.getCart(sharedPreferenceUtil);
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
        feeServicePercent.setText(String.format("Service fee (%d%%)", serviceCharge));
        feeDelivery.setText(String.valueOf(deliveryFee));
        if (cartItems.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            cartRecycler.setVisibility(View.GONE);
            totalContainer.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            cartRecycler.setVisibility(View.VISIBLE);
            totalContainer.setVisibility(View.VISIBLE);
            cartItemsCount.setText(String.valueOf(cartItems.size()));
            setupCartRecycler();
        }

        findViewById(R.id.promoCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPreferenceUtil.getBooleanPreference(Constants.KEY_RELOAD_CART, false)) {
            cartItems = Helper.getCart(sharedPreferenceUtil);
            if (cartItems.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                cartRecycler.setVisibility(View.GONE);
                totalContainer.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                cartRecycler.setVisibility(View.VISIBLE);
                totalContainer.setVisibility(View.VISIBLE);
                cartItemsCount.setText(String.valueOf(cartItems.size()));
            }
            if (cartRecycler.getAdapter() != null) cartRecycler.getAdapter().notifyDataSetChanged();
            sharedPreferenceUtil.setBooleanPreference(Constants.KEY_RELOAD_CART, false);
        }
    }

    private void promoDialog() {
        final Dialog dialog1 = new Dialog(this, R.style.dialog_full_90);
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setContentView(R.layout.coupon_dialog);
        dialog1.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        final EditText codeEdit = dialog1.findViewById(R.id.couponCode);
        InputFilter[] editFilters = codeEdit.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        codeEdit.setFilters(newFilters);
        final ProgressBar progressBar = dialog1.findViewById(R.id.progressBar);
        dialog1.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        dialog1.findViewById(R.id.couponApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.closeKeyboard(CartActivity.this, v);
                if (TextUtils.isEmpty(codeEdit.getText())) {
                    Toast.makeText(CartActivity.this, "Enter valid coupon code", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    ApiUtils.getClient().create(ChefService.class).checkCoupon(Helper.getApiToken(sharedPreferenceUtil), codeEdit.getText().toString()).enqueue(new Callback<CouponResponse>() {
                        @Override
                        public void onResponse(Call<CouponResponse> call, Response<CouponResponse> response) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                            if (response.isSuccessful() && !response.body().isExpired()) {
                                coupon = response.body();
                                setCouponData();
                            } else {
                                Toast.makeText(CartActivity.this, "Coupon code invalid or has expired", Toast.LENGTH_SHORT).show();
                            }
                            if (dialog1 != null)
                                dialog1.dismiss();
                        }

                        @Override
                        public void onFailure(Call<CouponResponse> call, Throwable t) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CartActivity.this, "Coupon validation check failed", Toast.LENGTH_SHORT).show();
                            }
                            if (dialog1 != null)
                                dialog1.dismiss();
                        }
                    });
                }
            }
        });
        dialog1.show();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart_clear:
                if (cartItems.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    confirmClearCart();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCouponData() {
        if (coupon != null) {
            couponText.setText(String.format("Coupon (%s) applied", coupon.getCode()));
            DecimalFormat decimalFormat = new DecimalFormat("###.##");
            couponPrice.setText(decimalFormat.format(coupon.getReward()) + currency);
            cartTotalChanged(cartAdapter.getTotal());
            couponContainer.setVisibility(View.VISIBLE);
            promoCodeView.setVisibility(View.GONE);
        } else {
            couponContainer.setVisibility(View.GONE);
            promoCodeView.setVisibility(View.VISIBLE);
        }
    }

    private void confirmClearCart() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Clear cart");
        dialog.setMessage("Are you sure you want to clear cart?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cartItems.clear();
                Helper.clearCart(sharedPreferenceUtil);
                dialog.dismiss();
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initUi() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        emptyView = findViewById(R.id.emptyView);
        emptyViewImage = findViewById(R.id.emptyViewImage);
        emptyViewText = findViewById(R.id.emptyViewText);
        findViewById(R.id.emptyViewTextSwipe).setVisibility(View.GONE);
        cartItemsCount = findViewById(R.id.cartItemsCount);
        emptyViewText.setText("No items found in the cart");
        Glide.with(this).load(R.drawable.placeholder_food).into(emptyViewImage);
        cartRecycler = findViewById(R.id.cartRecycler);
        totalContainer = findViewById(R.id.applyFilter);
        subtotal = findViewById(R.id.subtotal);
        total = findViewById(R.id.total);
        feeServicePercent = findViewById(R.id.feeServicePercent);
        feeService = findViewById(R.id.feeService);
        feeDelivery = findViewById(R.id.feeDelivery);
        couponContainer = findViewById(R.id.couponContainer);
        couponText = findViewById(R.id.couponText);
        couponPrice = findViewById(R.id.couponPrice);
        promoCodeView = findViewById(R.id.promoCode);

        findViewById(R.id.checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(CheckoutActivity.newInstance(CartActivity.this, cartItems, coupon));
            }
        });
    }

    private void setupCartRecycler() {
        cartRecycler.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems);
        cartRecycler.setAdapter(cartAdapter);
    }

    @Override
    public void cartTotalChanged(Double totalAmount) {
        if (coupon != null) {
            totalAmount = totalAmount - (coupon.getType().equals("fixed") ? coupon.getReward() : ((totalAmount * coupon.getReward()) / 100));
            if (totalAmount < 0) totalAmount = 0d;
        }
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        subtotal.setText(decimalFormat.format(totalAmount) + currency);
        double sc = (totalAmount * serviceCharge / 100);
        feeService.setText(decimalFormat.format(sc) + currency);
        total.setText(decimalFormat.format(totalAmount + sc + deliveryFee) + currency);
        Helper.setCart(sharedPreferenceUtil, cartItems);
    }
}
