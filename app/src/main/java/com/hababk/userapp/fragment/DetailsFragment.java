package com.hababk.userapp.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hababk.userapp.R;
import com.hababk.userapp.activity.AddEditAddressActivity;
import com.hababk.userapp.adapter.AddressAdapter;
import com.hababk.userapp.model.User;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.response.Address;
import com.hababk.userapp.util.Constants;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment {
    private Context context;
    private RecyclerView recyclerAddress;
    private ProgressBar progressBar;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private User userMe;
    private ChefService service;
    private AddressAdapter addressAdapter;
    private ArrayList<Address> addresses;
    private TextView empty_view_text;
    private boolean showPersonalDetails = true;

    public DetailsFragment() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceUtil = new SharedPreferenceUtil(getContext());
        userMe = Helper.getLoggedInUser(sharedPreferenceUtil);
        service = ApiUtils.getClient().create(ChefService.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ((EditText) view.findViewById(R.id.name)).setText(userMe.getName());
        ((EditText) view.findViewById(R.id.email)).setText(userMe.getEmail());
        ((EditText) view.findViewById(R.id.phone)).setText(userMe.getMobile_number());
        view.findViewById(R.id.personalDetailsContainer).setVisibility(showPersonalDetails ? View.VISIBLE : View.GONE);
        empty_view_text = view.findViewById(R.id.emptyViewText);
        recyclerAddress = view.findViewById(R.id.recyclerAddress);
        progressBar = view.findViewById(R.id.progressBar);
        view.findViewById(R.id.addAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddEditAddressActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        empty_view_text.setText(getText(R.string.no_address));
        setupAddressRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPreferenceUtil.getBooleanPreference(Constants.KEY_REFRESH_ADDRESSES, false)) {
            sharedPreferenceUtil.setBooleanPreference(Constants.KEY_REFRESH_ADDRESSES, false);
            empty_view_text.setVisibility(View.GONE);
            addresses.clear();
            addressAdapter.notifyDataSetChanged();
            loadAddresses();
        }
    }

    private void setupAddressRecycler() {
        recyclerAddress.setLayoutManager(new LinearLayoutManager(getContext()));
        addresses = Helper.getAddresses(sharedPreferenceUtil);
        addressAdapter = new AddressAdapter(getContext(), addresses, new AddressAdapter.AddressSelectedListener() {
            @Override
            public void onAddressSelected(Address address) {
                Intent addressIntent = new Intent(Constants.BROADCAST_ADDRESS);
                addressIntent.putExtra("data", address);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(addressIntent);
            }
        });
        recyclerAddress.setAdapter(addressAdapter);

        //addresses.clear();
        if (addresses.isEmpty()) {
            loadAddresses();
        }
    }

    private void loadAddresses() {
        progressBar.setVisibility(View.VISIBLE);
        service.getAddresses(Helper.getApiToken(sharedPreferenceUtil)).enqueue(new Callback<ArrayList<Address>>() {
            @Override
            public void onResponse(Call<ArrayList<Address>> call, Response<ArrayList<Address>> response) {
                if (context != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        addresses.addAll(response.body());
                        addressAdapter.notifyDataSetChanged();
                        Helper.setAddresses(sharedPreferenceUtil, addresses);

                        empty_view_text.setVisibility(addresses.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address>> call, Throwable t) {
                if (context != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public static DetailsFragment newInstance(boolean showPersonalDetails) {
        DetailsFragment fragment = new DetailsFragment();
        fragment.showPersonalDetails = showPersonalDetails;
        return fragment;
    }
}
