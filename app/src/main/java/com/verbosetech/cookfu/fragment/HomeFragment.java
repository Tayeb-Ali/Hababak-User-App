package com.verbosetech.cookfu.fragment;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.activity.CartActivity;
import com.verbosetech.cookfu.activity.RefineActivity;
import com.verbosetech.cookfu.adapter.FoodCategoryAdapter;
import com.verbosetech.cookfu.adapter.RestaurantAdapter;
import com.verbosetech.cookfu.model.CategoryFood;
import com.verbosetech.cookfu.model.RefineSetting;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.model.User;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.response.BaseListModel;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private final String FRAG_TAG_SEARCH = "FragSearch";
    private RecyclerView recyclerRestaurants;
    private TextView cartNotificationCount, emptyViewText;
    private ImageView emptyViewImage;
    private View emptyView;
    private SwipeRefreshLayout swipeRefresh;
    private EditText searchBar;
    private ChefService service;
    private SharedPreferenceUtil sharedPreferences;
    private boolean isLoading2, allDone2;
    private RestaurantAdapter restaurantsAdapter;
    private int pageNo2;
    private Call<BaseListModel<Store>> call2;
    private Call<BaseListModel<CategoryFood>> call1;
    public Address lastAddress;
    private SearchFragment searchFragment;
    private RefineSetting refineSetting;
    private HomeFragmentInteractor mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (HomeFragmentInteractor) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HomeFragmentInteractor");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        if (call1 != null) call1.cancel();
        if (call2 != null) call2.cancel();
    }

    private Callback<BaseListModel<CategoryFood>> callBack1 = new Callback<BaseListModel<CategoryFood>>() {
        @Override
        public void onResponse(Call<BaseListModel<CategoryFood>> call, Response<BaseListModel<CategoryFood>> response) {
            if (response.isSuccessful() && mCallback != null) {
                restaurantsAdapter.setFoodCategories(response.body().getData());
                mCallback.setFoodCategories(response.body().getData());
                if (!stores.isEmpty() && stores.get(0).getId().equals(-1)) {
                    restaurantsAdapter.notifyItemChanged(0);
                } else {
                    stores.add(0, new Store(-1));
                    restaurantsAdapter.notifyItemInserted(0);
                    recyclerRestaurants.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerRestaurants.scrollToPosition(0);
                        }
                    }, 100);
                }
                done2(true);
            }
        }

        @Override
        public void onFailure(Call<BaseListModel<CategoryFood>> call, Throwable t) {
        }
    };

    private ArrayList<Store> stores;
    private Callback<BaseListModel<Store>> callBack2 = new Callback<BaseListModel<Store>>() {
        @Override
        public void onResponse(Call<BaseListModel<Store>> call, Response<BaseListModel<Store>> response) {
            if (mCallback != null) {
                if (response.isSuccessful()) {
                    allDone2 = response.body().getData().isEmpty();
                    stores.addAll(response.body().getData());
                    restaurantsAdapter.notifyDataSetChanged();
                    if (!stores.isEmpty() && !stores.get(0).getId().equals(-1)) {
                        call1 = service.getCategories(Helper.getApiToken(sharedPreferences), 1);
                        call1.enqueue(callBack1);
                    }
                }
                done2(response.isSuccessful());
            }
        }

        @Override
        public void onFailure(Call<BaseListModel<Store>> call, Throwable t) {
            if (mCallback != null) {
                emptyViewText.setText("No items found at the moment");
                done2(false);
            }
        }
    };

    private void done2(boolean successful) {
        isLoading2 = false;
        if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
        emptyView.setVisibility(stores.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerRestaurants.setVisibility(stores.isEmpty() ? View.GONE : View.VISIBLE);
        if (successful) {
            mCallback.setStores(stores);
            mCallback.setStorePageNo(pageNo2);
        }
    }


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        service = ApiUtils.getClient().create(ChefService.class);
        sharedPreferences = new SharedPreferenceUtil(getContext());
        refineSetting = Helper.getRefineSetting(sharedPreferences);
        User userMe = Helper.getLoggedInUser(sharedPreferences);
        lastAddress = Helper.getLastFetchedAddress(sharedPreferences, userMe != null && userMe.getEmail().equals("guest@user.com"));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        View cartActionView = menu.findItem(R.id.action_cart).getActionView();
        cartNotificationCount = ((TextView) cartActionView.findViewById(R.id.notification_count));
        setCartCount(Helper.getCart(sharedPreferences).size());
        cartActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CartActivity.class));
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setCartCount(int notificationCount) {
        if (cartNotificationCount != null) {
            if (notificationCount <= 0) {
                cartNotificationCount.setVisibility(View.GONE);
            } else {
                cartNotificationCount.setVisibility(View.VISIBLE);
                cartNotificationCount.setText(String.valueOf(notificationCount));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        searchBar = view.findViewById(R.id.searchBar);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerRestaurants = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        emptyViewImage = view.findViewById(R.id.emptyViewImage);
        emptyViewText = view.findViewById(R.id.emptyViewText);
        view.findViewById(R.id.refineSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RefineActivity.class));
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStores();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(getContext()).load(R.drawable.placeholder_restaurant).into(emptyViewImage);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (getActivity().getSupportFragmentManager().findFragmentByTag(FRAG_TAG_SEARCH) == null) {
                        searchFragment = SearchFragment.newInstance(searchBar.getText().toString(), null);
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        fragmentTransaction.add(R.id.homeFrame, searchFragment, FRAG_TAG_SEARCH);
                        fragmentTransaction.addToBackStack(FRAG_TAG_SEARCH);
                        fragmentTransaction.commit();
                    } else {
                        searchFragment.search(searchBar.getText().toString());
                    }
                    Helper.closeKeyboard(getContext(), searchBar);
                    searchBar.setText("");
                    return true;
                }
                return false;
            }
        });

        setupRecyclerRestaurants();
    }

    @Override
    public void onResume() {
        super.onResume();
        setCartCount(Helper.getCart(sharedPreferences).size());
        if (sharedPreferences.getBooleanPreference(Constants.KEY_RELOAD_STORES, false)) {
            this.refineSetting = Helper.getRefineSetting(sharedPreferences);
            refreshStores();
            sharedPreferences.setBooleanPreference(Constants.KEY_RELOAD_STORES, false);
        }

        String savedStore = sharedPreferences.getStringPreference(Constants.KEY_FAVORITE, null);
        if (savedStore != null && restaurantsAdapter != null && stores != null) {
            Store store = new Gson().fromJson(savedStore, new TypeToken<Store>() {
            }.getType());
            for (int i = 0; i < stores.size(); i++) {
                if (stores.get(i).getId().equals(store.getId())) {
                    stores.get(i).setFavourite(store.getFavourite());
                    restaurantsAdapter.notifyItemChanged(i);
                    break;
                }
            }
            sharedPreferences.removePreference(Constants.KEY_FAVORITE);
        }
    }

    private void setupRecyclerRestaurants() {
        recyclerRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRestaurants.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // init
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                RecyclerView.Adapter adapter = recyclerView.getAdapter();

                if (layoutManager.getChildCount() > 0) {
                    // Calculations..
                    int indexOfLastItemViewVisible = layoutManager.getChildCount() - 1;
                    View lastItemViewVisible = layoutManager.getChildAt(indexOfLastItemViewVisible);
                    int adapterPosition = layoutManager.getPosition(lastItemViewVisible);
                    boolean isLastItemVisible = (adapterPosition == adapter.getItemCount() - 1);
                    // check
                    if (isLastItemVisible && !isLoading2 && !allDone2) {
                        onRecyclerRestaurantsScrolled();
                    }
                }
            }
        });
        stores = new ArrayList<>();
        stores.addAll(mCallback.getStores());
        pageNo2 = mCallback.getStorePageNo();
        restaurantsAdapter = new RestaurantAdapter(getContext(), stores, new FoodCategoryAdapter.FoodCategoryClickListener() {
            @Override
            public void foodCategorySelected(CategoryFood categoryFood) {
                if (getActivity().getSupportFragmentManager().findFragmentByTag(FRAG_TAG_SEARCH) == null) {
                    searchFragment = SearchFragment.newInstance(null, categoryFood);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    fragmentTransaction.add(R.id.homeFrame, searchFragment, FRAG_TAG_SEARCH);
                    fragmentTransaction.addToBackStack(FRAG_TAG_SEARCH);
                    fragmentTransaction.commit();
                } else {
                    searchFragment.search(searchBar.getText().toString());
                }
            }
        });
        restaurantsAdapter.setFoodCategories(mCallback.getFoodCategories());
        recyclerRestaurants.setAdapter(restaurantsAdapter);

        recyclerRestaurants.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        if (stores.isEmpty()) refreshStores();
    }

    public void refreshStores() {
        SearchFragment searchFragment = (SearchFragment) getActivity().getSupportFragmentManager().findFragmentByTag(FRAG_TAG_SEARCH);
        if (searchFragment != null) {
            searchFragment.refresh();
        }
        if (isLoading2)
            return;
        swipeRefresh.setRefreshing(true);
        recyclerRestaurants.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        stores.clear();
        mCallback.clearStores();
        restaurantsAdapter.notifyDataSetChanged();

        if (lastAddress != null) {
            isLoading2 = true;
            pageNo2 = 1;
            call2 = service.searchStores(Helper.getApiToken(sharedPreferences), null, lastAddress.getLatitude(), lastAddress.getLongitude(), refineSetting.getCost_for_two_min(), refineSetting.getCost_for_two_max(), refineSetting.isVegOnly() ? 1 : 0, refineSetting.getCost_for_two_sort(), null, pageNo2);
            call2.enqueue(callBack2);
            emptyViewText.setText("No restaurants to show in selected location.");
        } else {
            emptyViewText.setText("Kindly choose a location first");
            done2(false);
        }
    }

    private void onRecyclerRestaurantsScrolled() {
        isLoading2 = true;
        pageNo2++;
        call2 = service.searchStores(Helper.getApiToken(sharedPreferences), null, lastAddress.getLatitude(), lastAddress.getLongitude(), refineSetting.getCost_for_two_min(), refineSetting.getCost_for_two_max(), refineSetting.isVegOnly() ? 1 : 0, refineSetting.getCost_for_two_sort(), null, pageNo2);
        call2.enqueue(callBack2);
    }

    public interface HomeFragmentInteractor {
        ArrayList<Store> getStores();

        void setStores(ArrayList<Store> stores);

        int getStorePageNo();

        void setStorePageNo(int pageNo2);

        void clearStores();

        void setFoodCategories(ArrayList<CategoryFood> data);

        ArrayList<CategoryFood> getFoodCategories();
    }
}
