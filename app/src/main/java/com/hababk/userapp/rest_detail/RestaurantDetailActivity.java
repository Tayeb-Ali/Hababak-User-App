package com.hababk.userapp.rest_detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hababk.userapp.R;
import com.hababk.userapp.activity.CartActivity;
import com.hababk.userapp.adapter.ViewPagerAdapter;
import com.hababk.userapp.model.Store;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.response.FavoriteResponse;
import com.hababk.userapp.network.response.MenuItem;
import com.hababk.userapp.network.response.StoreDetail;
import com.hababk.userapp.util.Constants;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity {
    private static String EXTRA_REST = "restaurant";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView restName, restDesc, restMinOrderPrice, restDeliveryTime, restDeliveryFee;
    private ImageView restImage;
    private TextView cartItemsTotal, cartItemsCount;
    private View cartSummary;
    private ProgressBar progressBarDetail;
    private AppCompatRatingBar restRating;
    private CoordinatorLayout coordinator;

    private Store restaurant;
    private String currency;
    private DecimalFormat decimalFormat;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private boolean cartHasItems;
    private Menu menu;

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(cartEventReceiver, new IntentFilter(Constants.BROADCAST_CART));
        if (adapter != null && adapter.getCount() > 0 && adapter.getItem(0) != null) {
            CuisineFragment cuisineFragment = (CuisineFragment) adapter.getItem(0);
            cuisineFragment.refreshCart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cartEventReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        restaurant = getIntent().getParcelableExtra(EXTRA_REST);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        currency = Helper.getSetting(sharedPreferenceUtil, "currency");
        if (TextUtils.isEmpty(currency)) {
            currency = "";
        } else {
            currency = " " + currency;
        }
        decimalFormat = new DecimalFormat("###.##");
        initUi();
        setHeaderDetails();
        initViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rest_detail, menu);
        if (restaurant != null)
            menu.getItem(0).setIcon(restaurant.getFavourite() == 1 ? R.drawable.ic_favorite_accent_24dp : R.drawable.ic_favorite_white_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionFavorite:
                if (restaurant != null) {
                    restaurant.setFavourite(restaurant.getFavourite() == 1 ? 0 : 1);
                    item.setIcon(restaurant.getFavourite() == 1 ? R.drawable.ic_favorite_accent_24dp : R.drawable.ic_favorite_white_24dp);
                    sharedPreferenceUtil.setStringPreference(Constants.KEY_FAVORITE, new Gson().toJson(restaurant, new TypeToken<Store>() {
                    }.getType()));

                    ApiUtils.getClient().create(ChefService.class).markFavorite(Helper.getApiToken(sharedPreferenceUtil), restaurant.getId()).enqueue(new Callback<FavoriteResponse>() {
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setHeaderDetails() {
        restName.setText(restaurant.getName());
        restDesc.setText(restaurant.getTagline());
        restRating.setRating(Float.parseFloat(restaurant.getRatings()));
        restDeliveryTime.setText(restaurant.getDelivery_time());
        restMinOrderPrice.setText(decimalFormat.format(restaurant.getMinimum_order()) + currency);
        restDeliveryFee.setText(decimalFormat.format(restaurant.getDelivery_fee()) + currency);
        Glide.with(this).load(restaurant.getImage_url()).apply(new RequestOptions().placeholder(R.drawable.placeholder_food)).into(restImage);
    }

    private void initViewPager() {
        ChefService service = ApiUtils.getClient().create(ChefService.class);
        service.getStoreById(Helper.getApiToken(new SharedPreferenceUtil(this)), restaurant.getId()).enqueue(new Callback<StoreDetail>() {
            @Override
            public void onResponse(Call<StoreDetail> call, Response<StoreDetail> response) {
                if (response.isSuccessful()) {
                    progressBarDetail.setVisibility(View.GONE);
                    if (tabLayout != null && viewPager != null && response.body() != null && response.body().getStore() != null)
                        setupViewPager(response.body());
                }
            }

            @Override
            public void onFailure(Call<StoreDetail> call, Throwable t) {

            }
        });
    }

    private void setupViewPager(StoreDetail storeDetail) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(CuisineFragment.newInstance(storeDetail), "Cuisine");
        adapter.addFragment(ReviewFragment.newInstance(storeDetail.getStore()), "Review");
        adapter.addFragment(InfoFragment.newInstance(storeDetail.getStore()), "Info");
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                cartSummary.setVisibility(cartHasItems && position == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
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

        restName = findViewById(R.id.restName);
        progressBarDetail = findViewById(R.id.progressBarDetail);
        restImage = findViewById(R.id.restImage);
        restDesc = findViewById(R.id.restDesc);
        restDeliveryTime = findViewById(R.id.restDeliveryTime);
        restMinOrderPrice = findViewById(R.id.restMinOrderPrice);
        restDeliveryFee = findViewById(R.id.restDeliveryFee);
        restRating = findViewById(R.id.restRating);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        coordinator = findViewById(R.id.coordinator);
        cartSummary = findViewById(R.id.cartSummary);
        cartItemsCount = findViewById(R.id.cartItemsCount);
        cartItemsTotal = findViewById(R.id.cartItemsTotal);

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
                    collapsingToolbarLayout.setTitle(restaurant.getName());
                    isShow = true;
                } else if (isShow) {
                    //userDetailsSummaryContainer.setVisibility(View.VISIBLE);
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        cartSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantDetailActivity.this, CartActivity.class));
            }
        });
    }

    private BroadcastReceiver cartEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<MenuItem> cartItems = intent.getParcelableArrayListExtra("data");
            setTotal(cartItems);
        }
    };

    private void setTotal(ArrayList<MenuItem> cartItems) {
        cartHasItems = cartItems != null && !cartItems.isEmpty();
        cartSummary.setVisibility(cartHasItems ? View.VISIBLE : View.GONE);
        if (cartHasItems) {
            Double total = 0d;
            for (MenuItem item : cartItems)
                total += item.getTotal();
            cartItemsCount.setText(String.valueOf(cartItems.size()));
            cartItemsTotal.setText(decimalFormat.format(total) + currency);
        }
    }

    public static Intent newIntent(Context context, Store restaurant) {
        Intent intent = new Intent(context, RestaurantDetailActivity.class);
        intent.putExtra(EXTRA_REST, restaurant);
        return intent;
    }
}
