package com.verbosetech.cookfu.checkout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by a_man on 25-03-2018.
 */

public class CheckoutSummaryAdapter extends RecyclerView.Adapter<CheckoutSummaryAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<MenuItem> dataList;
    private String currency;

    public CheckoutSummaryAdapter(Context context, ArrayList<MenuItem> cartItems) {
        this.context = context;
        this.dataList = cartItems;
        currency = Helper.getSetting(new SharedPreferenceUtil(context), "currency");
        if (TextUtils.isEmpty(currency)) {
            currency = "";
        } else {
            currency = " " + currency;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_checkout_summary, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemTotal, itemQuantity;
        private DecimalFormat decimalFormat;

        MyViewHolder(View itemView) {
            super(itemView);
            decimalFormat = new DecimalFormat("###.##");
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemTotal = itemView.findViewById(R.id.itemTotal);
        }

        public void setData(MenuItem menuItem) {
            itemName.setText(menuItem.getTitle());
            itemQuantity.setText(menuItem.getQuantity() + " x " + decimalFormat.format(menuItem.getPrice()));
            itemTotal.setText(decimalFormat.format(menuItem.getTotal()) + currency);
        }
    }
}
