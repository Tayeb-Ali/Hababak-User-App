package com.hababk.userapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hababk.userapp.R;
import com.hababk.userapp.model.Store;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.request.RateRequest;
import com.hababk.userapp.network.response.RateResponse;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostReviewActivity extends AppCompatActivity {
    private TextView rateTitle;
    private AppCompatRatingBar restRating;
    private EditText restReview;
    private ProgressBar progressBar;
    private Button rateButton;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);
        store = getIntent().getParcelableExtra("store");
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        if (store == null)
            finish();
        else
            initUi();
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

        rateTitle = findViewById(R.id.rateTitle);
        restRating = findViewById(R.id.restRating);
        restReview = findViewById(R.id.restReview);
        progressBar = findViewById(R.id.progressBar);
        rateButton = findViewById(R.id.rateButton);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(restReview.getText())) {
                    Toast.makeText(PostReviewActivity.this, "Kindly say something", Toast.LENGTH_SHORT).show();
                    return;
                }
                Helper.closeKeyboard(PostReviewActivity.this, rateButton);
                postReview(new RateRequest(((int) restRating.getRating()), restReview.getText().toString()));
            }
        });

        rateTitle.setText(String.format("Rate and review %s", store.getName()));
    }

    private void postReview(RateRequest rateRequest) {
        progressBar.setVisibility(View.VISIBLE);
        rateButton.setClickable(false);
        ApiUtils.getClient().create(ChefService.class).rateStore(Helper.getApiToken(sharedPreferenceUtil), store.getId(), rateRequest).enqueue(new Callback<RateResponse>() {
            @Override
            public void onResponse(Call<RateResponse> call, Response<RateResponse> response) {
                rateButton.setClickable(true);
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    Toast.makeText(PostReviewActivity.this, "Review submitted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RateResponse> call, Throwable t) {
                rateButton.setClickable(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public static Intent newInstance(Context context, Store store) {
        Intent intent = new Intent(context, PostReviewActivity.class);
        intent.putExtra("store", store);
        return intent;
    }
}
