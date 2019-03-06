package com.hababk.userapp.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hababk.userapp.R;
import com.hababk.userapp.model.Order;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.response.GoogleMapsRoute;
import com.hababk.userapp.network.response.LocationUpdateResponse;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Order order;
    private GoogleMap googleMap;
    private TextView deliveryProfileName, deliveryProfilePhone, paymentMode, storeAddress, personNameTv, priceTv, paymentMethodTv, orderDateTv, orderNumberTv;
    private Marker markerDelivery;
    private DatabaseReference deliveryLocationReference;
    private ValueEventListener locationValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                LocationUpdateResponse locationUpdateResponse = dataSnapshot.getValue(LocationUpdateResponse.class);
                if (locationUpdateResponse != null && locationUpdateResponse.lat != null && locationUpdateResponse.lang != null) {
                    updateDeliveryLocation(new LatLng(locationUpdateResponse.lat, locationUpdateResponse.lang));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        order = getIntent().getParcelableExtra("order");
        initUi();
        registerLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deliveryLocationReference.removeEventListener(locationValueEventListener);
    }

    private void registerLocationUpdates() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        deliveryLocationReference = firebaseDatabase.getReference().child("cookfu").child(order.getDelivery_profile().getId().toString());
        deliveryLocationReference.addValueEventListener(locationValueEventListener);
    }

    private void initUi() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

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

        deliveryProfileName = findViewById(R.id.deliveryProfileName);
        deliveryProfilePhone = findViewById(R.id.deliveryProfilePhone);
        paymentMode = findViewById(R.id.paymentMode);
        storeAddress = findViewById(R.id.storeAddress);
        personNameTv = findViewById(R.id.new_order_person_name_tv);
        priceTv = findViewById(R.id.new_order_price_tv);
        paymentMethodTv = findViewById(R.id.new_order_payment_method_tv);
        orderNumberTv = findViewById(R.id.new_order_order_text_tv);
        orderDateTv = findViewById(R.id.new_order_dispatch_text_tv);
        deliveryProfileName.setText(order.getDelivery_profile().getUser().getName());
        deliveryProfilePhone.setText(order.getDelivery_profile().getUser().getMobile_number());
        paymentMode.setText("Cod");
        storeAddress.setText(order.getStore().getAddress());
        personNameTv.setText(order.getUser().getName());

        String currency = Helper.getSetting(new SharedPreferenceUtil(this), "currency");
        String PRICE_UNIT = TextUtils.isEmpty(currency) ? "" : " " + currency;
        priceTv.setText(new DecimalFormat("###.##").format(order.getSubtotal()) + PRICE_UNIT);
        paymentMethodTv.setText("Cod");
        orderNumberTv.setText("Order #" + order.getId());
        orderDateTv.setText("Ordered on " + Helper.getReadableDateTime(order.getCreated_at()));

        findViewById(R.id.deliveryProfilePhoneContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", order.getDelivery_profile().getUser().getMobile_number(), null)));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap mp) {
        this.googleMap = mp;
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng latLngStore = new LatLng(order.getStore().getLatitude(), order.getStore().getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLngStore).title("Restaurant").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                LatLng latLngUser = new LatLng(order.getAddress().getLatitude(), order.getAddress().getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLngUser).title("Home").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                ArrayList<LatLng> latLngs = new ArrayList<>();
                latLngs.add(latLngStore);
                latLngs.add(latLngUser);
                zoomLatLngs(latLngs);

                ApiUtils.getClient().create(ChefService.class).getRoute(getString(R.string.directions_api_key),latLngStore.latitude + "," + latLngStore.longitude, latLngUser.latitude + "," + latLngUser.longitude).enqueue(new Callback<GoogleMapsRoute>() {
                    @Override
                    public void onResponse(Call<GoogleMapsRoute> call, Response<GoogleMapsRoute> response) {
                        if (response.isSuccessful() && !response.body().getRoutes().isEmpty() && !response.body().getRoutes().get(0).getLegs().isEmpty()) {
                            if (response.body().getRoutes().get(0).getOverview_polyline() != null)
                                plotPolyline(response.body().getRoutes().get(0).getOverview_polyline().decodePoly());
                        }
                    }

                    @Override
                    public void onFailure(Call<GoogleMapsRoute> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void updateDeliveryLocation(LatLng latLng) {
        if (googleMap != null) {
            if (markerDelivery == null) {
                markerDelivery = googleMap.addMarker(new MarkerOptions().position(latLng).title("Food").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            } else {
                markerDelivery.setPosition(latLng);
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }

    private void plotPolyline(ArrayList<LatLng> latLngs) {
        googleMap.addPolyline(new PolylineOptions().addAll(latLngs).color(ContextCompat.getColor(this, R.color.colorDarkText3)));
        zoomLatLngs(latLngs);
    }

    private void zoomLatLngs(ArrayList<LatLng> toPlot) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : toPlot)
            builder.include(latLng);
        LatLngBounds bounds = builder.build();
        if (areBoundsTooSmall(bounds, 300)) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 17));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        }
    }

    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    public static Intent newInstance(Context context, Order order) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("order", order);
        return intent;
    }
}
