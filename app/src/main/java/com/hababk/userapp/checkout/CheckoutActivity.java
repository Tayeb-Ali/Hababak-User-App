package com.hababk.userapp.checkout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hababk.userapp.R;
import com.hababk.userapp.fragment.DetailsFragment;
import com.hababk.userapp.model.Store;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.request.CreateOrderRequest;
import com.hababk.userapp.network.response.Address;
import com.hababk.userapp.network.response.CouponResponse;
import com.hababk.userapp.network.response.CreateOrderResponse;
import com.hababk.userapp.network.response.MenuItem;
import com.hababk.userapp.util.Constants;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static String DATA_CART = "CartItems";
    private static String DATA_COUPON = "CouponResponse";

    private final String FRAG_TAG_ADDRESS = "Shipping Detail";
    private final String FRAG_TAG_PAYMENT_MODE = "Payment Detail";
    private final String FRAG_TAG_CONFIRM = "Confirm Order";

    private Handler mHandler;
    private int checkoutStage;
    private ArrayList<MenuItem> cartItems;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private Store savedStore;
    private CouponResponse couponResponse;

    private TextView checkoutActionText, checkoutStageHeading1, checkoutStageHeading2, checkoutStageHeading3;
    private Address defaultAddress;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        cartItems = getIntent().getParcelableArrayListExtra(DATA_CART);
        couponResponse = getIntent().getParcelableExtra(DATA_COUPON);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        savedStore = Helper.getCartStore(sharedPreferenceUtil);
        defaultAddress = Helper.getAddressDefault(sharedPreferenceUtil);
        initUi();

        loadFragment(FRAG_TAG_ADDRESS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(addressSelectedListener, new IntentFilter(Constants.BROADCAST_ADDRESS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(addressSelectedListener);
    }

    private BroadcastReceiver addressSelectedListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Address address = intent.getParcelableExtra("data");
            if (address != null) {
                if (defaultAddress != null && !defaultAddress.getId().equals(address.getId())) {
                    sharedPreferenceUtil.setBooleanPreference(Constants.KEY_RELOAD_CART, true);
                    finish();
                } else {
                    defaultAddress = address;
                    checkoutStage = 1;
                    setupViews();
                }
            }
        }
    };

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

        checkoutActionText = findViewById(R.id.checkoutActionText);
        checkoutStageHeading1 = findViewById(R.id.checkoutStageHeading1);
        checkoutStageHeading2 = findViewById(R.id.checkoutStageHeading2);
        checkoutStageHeading3 = findViewById(R.id.checkoutStageHeading3);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.checkoutAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (checkoutStage) {
                    case 0:
                        Toast.makeText(CheckoutActivity.this, "Select an address to continue", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        loadFragment(FRAG_TAG_PAYMENT_MODE);
                        break;
                    case 2:
                        loadFragment(FRAG_TAG_CONFIRM);
                        break;
                    case 3:
                        createOrder();
                        break;
                }
            }
        });
    }

    private void createOrder() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setAddress_id(defaultAddress.getId());
        createOrderRequest.setStore_id(savedStore.getId());
        createOrderRequest.setDelivery_fee(40d);
        createOrderRequest.setTaxes(45d);
        createOrderRequest.setDiscount(0d);
        createOrderRequest.setSpecial_instructions("You are awesome");
        createOrderRequest.setPayment_method_id(1);
        createOrderRequest.setItems(cartItems);
        if (couponResponse != null) createOrderRequest.setCoupon(couponResponse.getCode());

        progressBar.setVisibility(View.VISIBLE);
        ApiUtils.getClient().create(ChefService.class).createOrder(Helper.getApiToken(sharedPreferenceUtil), createOrderRequest).enqueue(new Callback<CreateOrderResponse>() {
            @Override
            public void onResponse(Call<CreateOrderResponse> call, Response<CreateOrderResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    done();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Unable to place order at the moment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateOrderResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(CheckoutActivity.this, "Something went wrong while placing order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void done() {
        Toast.makeText(CheckoutActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
        cartItems.clear();
        Helper.clearCart(sharedPreferenceUtil);
        startActivity(OrderPlacedActivity.newIntent(this, savedStore.getName()));
    }

    private void loadFragment(final String fragTag) {
        Fragment fragment = null;
        switch (fragTag) {
            case FRAG_TAG_ADDRESS:
                fragment = DetailsFragment.newInstance(false);
                if (defaultAddress != null) {
                    checkoutStage = 1;
                }
                break;
            case FRAG_TAG_PAYMENT_MODE:
                checkoutStage = 2;
                fragment = new CheckoutPaymentModeFragment();
                break;
            case FRAG_TAG_CONFIRM:
                checkoutStage = 3;
                fragment = CheckoutConfirmFragment.newInstance(cartItems, couponResponse, savedStore);
                break;
        }

        setupViews();

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        final Fragment finalFragment = fragment;
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.add(R.id.checkoutFrame, finalFragment, fragTag);
                fragmentTransaction.addToBackStack(fragTag);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mHandler == null)
            mHandler = new Handler();
        if (getSupportFragmentManager().findFragmentByTag(fragTag) == null)
            mHandler.post(mPendingRunnable);

    }

    private void setupViews() {
        getSupportActionBar().setTitle(checkoutStage == 1 ? FRAG_TAG_ADDRESS : checkoutStage == 2 ? FRAG_TAG_CONFIRM : FRAG_TAG_PAYMENT_MODE);
        checkoutActionText.setText(checkoutStage == 0 ? "Select Address" : checkoutStage == 1 ? "Proceed to Payment" : checkoutStage == 2 ? "Confirm Order" : "Confirm & Pay");
        checkoutStageHeading1.setTextColor(ContextCompat.getColor(this, checkoutStage >= 0 ? R.color.colorAccent : android.R.color.darker_gray));
        checkoutStageHeading2.setTextColor(ContextCompat.getColor(this, checkoutStage >= 2 ? R.color.colorAccent : android.R.color.darker_gray));
        checkoutStageHeading3.setTextColor(ContextCompat.getColor(this, checkoutStage >= 3 ? R.color.colorAccent : android.R.color.darker_gray));
        checkoutStageHeading1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ContextCompat.getDrawable(this, checkoutStage >= 0 ? R.drawable.ic_local_mall_accent_24dp : R.drawable.ic_local_mall_gray_24dp));
        checkoutStageHeading2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ContextCompat.getDrawable(this, checkoutStage >= 2 ? R.drawable.ic_credit_card_accent_24dp : R.drawable.ic_credit_card_gray_24dp));
        checkoutStageHeading3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ContextCompat.getDrawable(this, checkoutStage >= 3 ? R.drawable.ic_assignment_turned_in_accent_24dp : R.drawable.ic_assignment_turned_in_gray_24dp));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
            checkoutStage = getSupportFragmentManager().getBackStackEntryCount();
            setupViews();
        }
    }

    public static Intent newInstance(Context context, ArrayList<MenuItem> cartItems, CouponResponse coupon) {
        Intent intent = new Intent(context, CheckoutActivity.class);
        intent.putExtra(DATA_CART, cartItems);
        intent.putExtra(DATA_COUPON, coupon);
        return intent;
    }
}
