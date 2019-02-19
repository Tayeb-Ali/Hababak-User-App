package com.verbosetech.cookfu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FoodDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static String EXTRA_REST_MENU = "restaurant_menu";
    private static String EXTRA_REST = "restaurant";
    private static String EXTRA_REST_SAVED = "restaurant_saved";
    private static String EXTRA_CART_SAVED = "cart_saved";

    private MenuItem restaurantMenu;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ArrayList<MenuItem> cartItems;
    private Store store, savedStore;
    private String currency;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView itemName, itemPrice, itemDetail, itemSpecification, itemQuantity;
    private ImageView itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        cartItems = getIntent().getParcelableArrayListExtra(EXTRA_CART_SAVED);
        restaurantMenu = getIntent().getParcelableExtra(EXTRA_REST_MENU);
        store = getIntent().getParcelableExtra(EXTRA_REST);
        savedStore = getIntent().getParcelableExtra(EXTRA_REST_SAVED);

        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        currency = Helper.getSetting(sharedPreferenceUtil, "currency");
        if (TextUtils.isEmpty(currency)) {
            currency = "";
        } else {
            currency = " " + currency;
        }
        initUi();
        setDetails();
    }

    private void setDetails() {
        itemName.setText(restaurantMenu.getTitle());
        itemPrice.setText(new DecimalFormat("###.##").format(restaurantMenu.getPrice()) + currency);
        itemDetail.setText(restaurantMenu.getDetail());
        itemSpecification.setText(restaurantMenu.getSpecification());
        Glide.with(this).load(restaurantMenu.getImage_url()).apply(new RequestOptions().placeholder(R.drawable.placeholder_food)).into(itemImage);

        int index = cartItems.indexOf(restaurantMenu);
        if (index != -1) {
            itemQuantity.setText(String.valueOf(restaurantMenu.getQuantity()));
        }
    }

    private void initUi() {
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemDetail = findViewById(R.id.itemDetail);
        itemSpecification = findViewById(R.id.itemSpecification);
        itemQuantity = findViewById(R.id.itemQuantity);
        Button addToOrder = findViewById(R.id.addToOrder);
        addToOrder.setOnClickListener(this);
        findViewById(R.id.itemQuantityMinus).setOnClickListener(this);
        findViewById(R.id.itemQuantityPlus).setOnClickListener(this);
        findViewById(R.id.goToCart).setOnClickListener(this);

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

        collapsingToolbarLayout.setTitle(" ");
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //userDetailsSummaryContainer.setVisibility(View.INVISIBLE);
                    collapsingToolbarLayout.setTitle(restaurantMenu.getTitle());
                    isShow = true;
                } else if (isShow) {
                    //userDetailsSummaryContainer.setVisibility(View.VISIBLE);
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itemQuantityMinus:
                if (restaurantMenu.getQuantity() > 1) {
                    restaurantMenu.setQuantity(restaurantMenu.getQuantity() - 1);
                    itemQuantity.setText(String.valueOf(restaurantMenu.getQuantity()));
                }
                break;
            case R.id.itemQuantityPlus:
                restaurantMenu.setQuantity(restaurantMenu.getQuantity() + 1);
                itemQuantity.setText(String.valueOf(restaurantMenu.getQuantity()));
                break;
            case R.id.addToOrder:
                if (savedStore == null || savedStore.getId().equals(store.getId())) {
                    if (restaurantMenu.getQuantity() == 0) {
                        restaurantMenu.setQuantity(1);
                        itemQuantity.setText(String.valueOf(restaurantMenu.getQuantity()));
                    }
                    int index = cartItems.indexOf(restaurantMenu);
                    if (index != -1) {
                        cartItems.set(index, restaurantMenu);
                    } else {
                        cartItems.add(restaurantMenu);
                    }
                    Helper.setCart(sharedPreferenceUtil, cartItems);
                    if (savedStore == null) {
                        savedStore = store;
                        Helper.setCartStore(sharedPreferenceUtil, store);
                    }
                    Toast.makeText(this, "Item added/updated in cart", Toast.LENGTH_SHORT).show();
                } else if (savedStore != null && !savedStore.getId().equals(store.getId())) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Cart conflict");
                    dialog.setMessage("Your cart consists items from another restaurant, do you want to clear your cart to add items from this restaurant?");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            savedStore = null;
                            cartItems.clear();
                            Helper.clearCart(sharedPreferenceUtil);
                            Toast.makeText(FoodDetailActivity.this, "Cart Cleared, try adding item again", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
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
                break;
            case R.id.goToCart:
                startActivity(new Intent(this, CartActivity.class));
                break;
        }
    }

    public static Intent newIntent(Context context, Store store, Store savedStore, MenuItem restaurantMenu, ArrayList<MenuItem> cartItems) {
        Intent intent = new Intent(context, FoodDetailActivity.class);
        intent.putExtra(EXTRA_REST_MENU, restaurantMenu);
        intent.putExtra(EXTRA_REST, store);
        intent.putExtra(EXTRA_REST_SAVED, savedStore);
        intent.putExtra(EXTRA_CART_SAVED, cartItems);
        return intent;
    }
}
