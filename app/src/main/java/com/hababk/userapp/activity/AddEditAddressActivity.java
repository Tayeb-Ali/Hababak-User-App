package com.hababk.userapp.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hababk.userapp.R;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.response.Address;
import com.hababk.userapp.util.Constants;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditAddressActivity extends BaseLocationActivity {
    private static String DATA_ADDRESS = "Address";
    private TextView title, address;
    private ProgressBar progressBar;
    private View submitAddress;
    private Double latitude = 0.0d, longitude = 0.0d;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private Address addressToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        addressToEdit = getIntent().getParcelableExtra(DATA_ADDRESS);
        initUi();
    }

    @Override
    protected void startedLocationFetching() {

    }

    @Override
    protected void locationCoordinatesCaptured(Location lastLocation, Double lastLatitude, Double lastLongitude) {
    }

    @Override
    protected void locationAddressCaptured(android.location.Address fetchedAddress, String addressString) {
        this.latitude = fetchedAddress.getLatitude();
        this.longitude = fetchedAddress.getLongitude();
        Toast.makeText(this, "Location captured", Toast.LENGTH_SHORT).show();
        address.setText(addressString);
    }

    private void initUi() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        title = findViewById(R.id.title);
        address = findViewById(R.id.address);
        progressBar = findViewById(R.id.progressBar);
        submitAddress = findViewById(R.id.submitAddress);
        findViewById(R.id.getLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPlace();
            }
        });

        submitAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText())) {
                    Toast.makeText(AddEditAddressActivity.this, "Give a title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(address.getText())) {
                    Toast.makeText(AddEditAddressActivity.this, "Fill in address manually", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (latitude == null || latitude == 0.0d || longitude == null || longitude == 0.0d) {
                    Toast.makeText(AddEditAddressActivity.this, "Point location on map", Toast.LENGTH_SHORT).show();
                    return;
                }
                Helper.closeKeyboard(AddEditAddressActivity.this, submitAddress);
                submitAddress.setClickable(false);
                createUpdateAddress(new Address(title.getText().toString(), address.getText().toString(), latitude, longitude));
            }
        });

        if (addressToEdit != null) {
            title.setText(addressToEdit.getTitle());
            address.setText(addressToEdit.getAddress());
            latitude = addressToEdit.getLatitude();
            longitude = addressToEdit.getLongitude();
        }
    }

    private void createUpdateAddress(Address address) {
        progressBar.setVisibility(View.VISIBLE);
        address.setLatitude(latitude);
        address.setLongitude(longitude);

        ChefService service = ApiUtils.getClient().create(ChefService.class);
        if (addressToEdit != null) {
            service.updateAddress(Helper.getApiToken(sharedPreferenceUtil), address, addressToEdit.getId()).enqueue(new Callback<Address>() {
                @Override
                public void onResponse(Call<Address> call, Response<Address> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    submitAddress.setClickable(true);
                    if (response.isSuccessful()) {
                        sharedPreferenceUtil.setBooleanPreference(Constants.KEY_REFRESH_ADDRESSES, true);
                        Toast.makeText(AddEditAddressActivity.this, "Address updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditAddressActivity.this, "Address update failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Address> call, Throwable t) {
                    submitAddress.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddEditAddressActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            service.addAddress(Helper.getApiToken(sharedPreferenceUtil), address).enqueue(new Callback<Address>() {
                @Override
                public void onResponse(Call<Address> call, Response<Address> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    submitAddress.setClickable(true);
                    if (response.isSuccessful()) {
                        sharedPreferenceUtil.setBooleanPreference(Constants.KEY_REFRESH_ADDRESSES, true);
                        Toast.makeText(AddEditAddressActivity.this, "Address added", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditAddressActivity.this, "Address creation failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Address> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    submitAddress.setClickable(true);
                    Toast.makeText(AddEditAddressActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static Intent newInstance(Context context, Address address) {
        Intent intent = new Intent(context, AddEditAddressActivity.class);
        intent.putExtra(DATA_ADDRESS, address);
        return intent;
    }
}
