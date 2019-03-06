package com.hababk.userapp.checkout;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hababk.userapp.R;
import com.hababk.userapp.activity.MainActivity;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

public class OrderPlacedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        initUi();
    }

    private void initUi() {
        ImageView confirmImage = findViewById(R.id.confirmImage);
        Glide.with(this).load(R.drawable.confirm_order).into(confirmImage);

        TextView confirmTitle = findViewById(R.id.confirmTitle);
        TextView confirmMessage = findViewById(R.id.confirmMessage);

        confirmTitle.setText(String.format("Hey, %s", Helper.getLoggedInUser(new SharedPreferenceUtil(this)).getName()));
        confirmMessage.setText(String.format("Your order with %s is confirmed. You will be notified as order updates.", getIntent().getStringExtra("STORE_NAME")));

        findViewById(R.id.continueShopping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderPlacedActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.continueMyOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MainActivity.newIntent(OrderPlacedActivity.this, "order");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public static Intent newIntent(Context context, String storeName) {
        Intent intent = new Intent(context, OrderPlacedActivity.class);
        intent.putExtra("STORE_NAME", storeName);
        return intent;
    }
}
