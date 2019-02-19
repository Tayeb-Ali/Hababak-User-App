package com.verbosetech.cookfu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.verbosetech.cookfu.fragment.DetailsFragment;
import com.verbosetech.cookfu.fragment.FavoriteFragment;
import com.verbosetech.cookfu.fragment.HomeFragment;
import com.verbosetech.cookfu.fragment.MyReviewsFragment;
import com.verbosetech.cookfu.fragment.OrdersFragment;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.fragment.SupportFragment;
import com.verbosetech.cookfu.adapter.DrawerListAdapter;
import com.verbosetech.cookfu.model.CategoryFood;
import com.verbosetech.cookfu.model.NavItem;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.model.User;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.request.FcmTokenUpdateRequest;
import com.verbosetech.cookfu.network.response.BaseListModel;
import com.verbosetech.cookfu.network.response.Favorite;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseLocationActivity implements HomeFragment.HomeFragmentInteractor {
    ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private TextView userName, userNumber, cityText, addressText;

    ArrayList<NavItem> mNavItems;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private LinearLayout toolbarLayout;

    private Handler mHandler;
    private final String FRAG_TAG_HOME = "Cookfu";
    private final String FRAG_TAG_FAVORITE = "Favorites";
    private final String FRAG_TAG_REVIEWS = "My Reviews";
    private final String FRAG_TAG_SUPPORT = "Contact us";
    private final String FRAG_TAG_ORDERS = "My Orders";
    private final String FRAG_TAG_DETAILS = "My Details";
    private String fragTagCurrent = null;

    //home fragment temporary cache.
    private ArrayList<CategoryFood> categoriesCache;
    private ArrayList<Store> storesCache;
    private int storePageNo;
    private Address lastAddress;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private User userMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        userMe = Helper.getLoggedInUser(sharedPreferenceUtil);
        Helper.refreshSettings(sharedPreferenceUtil);
        initUi();
        categoriesCache = new ArrayList<>();
        storesCache = new ArrayList<>();
        storePageNo = 1;
        lastAddress = Helper.getLastFetchedAddress(sharedPreferenceUtil, userMe.getEmail().equals("guest@user.com"));
        setAddressData(lastAddress);
        if (lastAddress == null) {
            requestCurrentLocation();
        }
        loadFragment(getIntent().getStringExtra("order_id") != null ? FRAG_TAG_ORDERS : FRAG_TAG_HOME);
        updateFcmToken();
        notifyAboutNewYork();
    }

    private void notifyAboutNewYork() {
        if (getString(R.string.is_demo).equals("true") && sharedPreferenceUtil.getBooleanPreference(Constants.KEY_NY_NOTIFY, true)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Guest login");
            alertDialogBuilder.setMessage("For demo purposes currently your location has been set to New York, US. You can change your location by clicking above on New York to list Restaurants accordingly. In live app restaurants will appear according to user's current location by default.");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
            sharedPreferenceUtil.setBooleanPreference(Constants.KEY_NY_NOTIFY, false);
        }
    }

    private void initUi() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = findViewById(R.id.userName);
        userNumber = findViewById(R.id.userNumber);
        cityText = findViewById(R.id.cityText);
        addressText = findViewById(R.id.addressText);
        if (userMe != null) {
            userName.setText(String.format("%s %s", getString(R.string.hey), userMe.getName()));
            userNumber.setText(userMe.getMobile_number());
        }
        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        mDrawerList = (ListView) findViewById(R.id.navList);
        toolbarLayout = findViewById(R.id.locationContainer);

        ImageView navBackground = findViewById(R.id.navBackground);
        Glide.with(this).load(R.drawable.background).into(navBackground);
        ImageView navLogo = findViewById(R.id.navLogo);
        Glide.with(this).load(R.drawable.chef_logo).into(navLogo);

        mNavItems = new ArrayList<>();
        mNavItems.add(new NavItem(getString(R.string.nav_home), "List of restaurants", R.drawable.ic_store_white_24dp));
        mNavItems.add(new NavItem(getString(R.string.nav_orders), "Current and past orders", R.drawable.ic_restaurant_menu_white_24dp));
        mNavItems.add(new NavItem(getString(R.string.nav_details), "Profile, address, payment info", R.drawable.ic_person_pin_white_24dp));
        mNavItems.add(new NavItem(getString(R.string.nav_favo), "Favoured restaurants", R.drawable.ic_favorite_white_24dp));
        mNavItems.add(new NavItem(getString(R.string.nav_reviews), "List of reviews", R.drawable.ic_local_activity_white_24dp));
        mNavItems.add(new NavItem(getString(R.string.nav_support), "Have a chat with us", R.drawable.ic_chat_white_24dp));
        mNavItems.add(new NavItem(getString(R.string.nav_rate), "Rate us on playstore", R.drawable.ic_thumb_up_white_24dp));
        mNavItems.add(new NavItem(getString(R.string.nav_loout), "Sign out of app", R.drawable.ic_exit_to_app_white_24dp));
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragTagCurrent = null;
                switch (position) {
                    case 0:
                        fragTagCurrent = FRAG_TAG_HOME;
                        break;
                    case 1:
                        fragTagCurrent = FRAG_TAG_ORDERS;
                        break;
                    case 2:
                        fragTagCurrent = FRAG_TAG_DETAILS;
                        break;
                    case 3:
                        fragTagCurrent = FRAG_TAG_FAVORITE;
                        break;
                    case 4:
                        fragTagCurrent = FRAG_TAG_REVIEWS;
                        break;
                    case 5:
                        fragTagCurrent = FRAG_TAG_SUPPORT;
                        break;
                    case 6:
                        Helper.openPlayStoreIntent(MainActivity.this);
                        break;
                    case 7:
                        confirmLogout();
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                if (fragTagCurrent != null) {
                    loadFragment(fragTagCurrent);
                    fragTagCurrent = null;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        toolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPlace();
            }
        });
    }

    @Override
    protected void startedLocationFetching() {

    }

    @Override
    protected void locationCoordinatesCaptured(Location lastLocation, Double lastLatitude, Double lastLongitude) {

    }

    @Override
    protected void locationAddressCaptured(Address newAddress, String addressString) {
        Helper.setLastFetchedAddress(sharedPreferenceUtil, newAddress);
        getSupportActionBar().setDisplayShowTitleEnabled(newAddress == null);
        toolbarLayout.setVisibility(newAddress == null ? View.GONE : View.VISIBLE);
        setAddressData(newAddress);
        lastAddress = newAddress;
        if (getSupportFragmentManager().findFragmentByTag(FRAG_TAG_HOME) != null) {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_HOME);
            homeFragment.lastAddress = newAddress;
            homeFragment.refreshStores();
        }
    }

    private void setAddressData(Address newAddress) {
        if (newAddress != null) {
            String city = newAddress.getLocality();
            String state = newAddress.getAdminArea();
            String zip = newAddress.getPostalCode();
            String country = newAddress.getCountryName();

            if (zip == null)
                zip = "";

            cityText.setSelected(true);
            cityText.setText(city + " " + state);
            addressText.setSelected(true);
            addressText.setText(state + " " + zip + " " + country);
        }
    }

    private void confirmLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                logout();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void logout() {
        sharedPreferenceUtil.removePreference(Constants.KEY_USER);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("order_id") != null)
            loadFragment(FRAG_TAG_ORDERS);
    }

    private void updateFcmToken() {
        ApiUtils.getClient().create(ChefService.class).updateFcmToken(Helper.getApiToken(sharedPreferenceUtil), new FcmTokenUpdateRequest(FirebaseInstanceId.getInstance().getToken())).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e("FcmTokenUpdate", String.valueOf(response.isSuccessful()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("FcmTokenUpdate", t.getMessage());
            }
        });
    }

    private void loadFragment(final String fragTag) {
        Fragment fragment = null;
        toolbarLayout.setVisibility(View.GONE);
        switch (fragTag) {
            case FRAG_TAG_HOME:
                toolbarLayout.setVisibility(View.VISIBLE);
                fragment = new HomeFragment();
                break;
            case FRAG_TAG_FAVORITE:
                fragment = new FavoriteFragment();
                break;
            case FRAG_TAG_REVIEWS:
                fragment = new MyReviewsFragment();
                break;
            case FRAG_TAG_DETAILS:
                fragment = DetailsFragment.newInstance(true);
                break;
            case FRAG_TAG_ORDERS:
                fragment = new OrdersFragment();
                break;
            case FRAG_TAG_SUPPORT:
                fragment = new SupportFragment();
                break;
        }

        getSupportActionBar().setTitle(fragTag);
        getSupportActionBar().setDisplayShowTitleEnabled(!fragTag.equals(FRAG_TAG_HOME));

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
                fragmentTransaction.replace(R.id.mainFrame, finalFragment, fragTag);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mHandler == null)
            mHandler = new Handler();
        if (getSupportFragmentManager().findFragmentByTag(fragTag) == null)
            mHandler.post(mPendingRunnable);

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_TAG_HOME) == null)
            loadFragment(FRAG_TAG_HOME);
        else
            super.onBackPressed();
    }

    public static Intent newIntent(Context context, String order_id) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("order_id", order_id);
        return intent;
    }

    @Override
    public ArrayList<Store> getStores() {
        return storesCache;
    }

    @Override
    public void setStores(ArrayList<Store> stores) {
        storesCache.clear();
        storesCache.addAll(stores);
    }

    @Override
    public int getStorePageNo() {
        return storePageNo;
    }

    @Override
    public void setStorePageNo(int pageNo2) {
        storePageNo = pageNo2;
    }

    @Override
    public void clearStores() {
        storePageNo = 1;
        storesCache.clear();
        categoriesCache.clear();
    }

    @Override
    public void setFoodCategories(ArrayList<CategoryFood> data) {
        categoriesCache.clear();
        categoriesCache.addAll(data);
    }

    @Override
    public ArrayList<CategoryFood> getFoodCategories() {
        return categoriesCache;
    }
}
