package com.verbosetech.cookfu.fragment;


import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.adapter.RestaurantAdapter;
import com.verbosetech.cookfu.model.CategoryFood;
import com.verbosetech.cookfu.model.RefineSetting;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.model.User;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.response.BaseListModel;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private ChefService service;
    private SharedPreferenceUtil sharedPreferences;
    private String query;
    private CategoryFood category;

    private Call<BaseListModel<Store>> call2;
    private int pageNo2;
    private RecyclerView recyclerRestaurants;
    private ProgressBar progressBar2;
    private boolean isLoading2, allDone2;
    private TextView emptyViewText, searchHeading;
    private View emptyView;
    private ImageView emptyViewImage;
    private Address lastAddress;
    private RefineSetting refineSetting;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;

    private ArrayList<Store> stores;
    private Callback<BaseListModel<Store>> callBack2 = new Callback<BaseListModel<Store>>() {
        @Override
        public void onResponse(Call<BaseListModel<Store>> call, Response<BaseListModel<Store>> response) {
            if (context != null) {
                if (response.isSuccessful()) {
                    allDone2 = response.body().getData().isEmpty();
                    stores.addAll(response.body().getData());
                    recyclerRestaurants.getAdapter().notifyDataSetChanged();
                }
                done2(response.isSuccessful());
            }
        }

        @Override
        public void onFailure(Call<BaseListModel<Store>> call, Throwable t) {
            if (context != null) {
                emptyViewText.setText("No items found at the moment");
                done2(false);
            }
        }
    };

    public SearchFragment() {
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = ApiUtils.getClient().create(ChefService.class);
        sharedPreferences = new SharedPreferenceUtil(context);
        User userMe = Helper.getLoggedInUser(sharedPreferences);
        lastAddress = Helper.getLastFetchedAddress(sharedPreferences, userMe != null && userMe.getEmail().equals("guest@user.com"));
        refineSetting = Helper.getRefineSetting(sharedPreferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        RefineSetting newRefineSetting = Helper.getRefineSetting(sharedPreferences);
        if (refineSetting != null && !newRefineSetting.equals(refineSetting)) {
            this.refineSetting = newRefineSetting;
            search(query);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        recyclerRestaurants = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        emptyViewImage = view.findViewById(R.id.emptyViewImage);
        emptyViewText = view.findViewById(R.id.emptyViewText);
        progressBar2 = view.findViewById(R.id.progressBar2);
        searchHeading = view.findViewById(R.id.searchHeading);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(context).load(R.drawable.placeholder_search).into(emptyViewImage);
        recyclerRestaurants.setLayoutManager(new LinearLayoutManager(context));
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
        recyclerRestaurants.setAdapter(new RestaurantAdapter(context, stores, null));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        search(query);
    }

    public void refresh() {
        User userMe = Helper.getLoggedInUser(sharedPreferences);
        lastAddress = Helper.getLastFetchedAddress(sharedPreferences, userMe != null && userMe.getEmail().equals("guest@user.com"));
        stores.clear();
        recyclerRestaurants.getAdapter().notifyDataSetChanged();
        pageNo2 = 1;
        search(query);
        swipeRefreshLayout.setRefreshing(true);
    }


    private void onRecyclerRestaurantsScrolled() {
        isLoading2 = true;
        pageNo2++;
        call2 = service.searchStores(Helper.getApiToken(sharedPreferences), query, lastAddress.getLatitude(), lastAddress.getLongitude(), refineSetting.getCost_for_two_min(), refineSetting.getCost_for_two_max(), refineSetting.isVegOnly() ? 1 : 0, refineSetting.getCost_for_two_sort(), category != null ? category.getId() : null, pageNo2);
        call2.enqueue(callBack2);
    }

    private void done2(boolean successful) {
        isLoading2 = false;
        progressBar2.setVisibility(View.GONE);
        emptyView.setVisibility(stores.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerRestaurants.setVisibility(stores.isEmpty() ? View.GONE : View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void search(String s) {
        this.query = s;
        searchHeading.setText(String.format(getString(R.string.search_header), (s != null ? s : "") + (category != null ? "(" + category.getTitle() + ")" : "")));
        searchHeading.setSelected(true);
        if (!stores.isEmpty()) {
            stores.clear();
            recyclerRestaurants.getAdapter().notifyDataSetChanged();
        }

        progressBar2.setVisibility(View.VISIBLE);
        recyclerRestaurants.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        if (lastAddress != null) {
            isLoading2 = true;
            pageNo2 = 1;
            call2 = service.searchStores(Helper.getApiToken(sharedPreferences), s, lastAddress.getLatitude(), lastAddress.getLongitude(), refineSetting.getCost_for_two_min(), refineSetting.getCost_for_two_max(), refineSetting.isVegOnly() ? 1 : 0, refineSetting.getCost_for_two_sort(), category != null ? category.getId() : null, pageNo2);
            call2.enqueue(callBack2);
        } else {
            emptyViewText.setText("Kindly choose a location first");
            done2(false);
        }
    }

    public static SearchFragment newInstance(String query, CategoryFood category) {
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.query = query;
        searchFragment.category = category;
        return searchFragment;
    }
}
