package com.verbosetech.cookfu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by a_man on 24-01-2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<MenuItem> dataList;
    private CartTotalChangeListener totalChangeListener;
    private String currency;

    public CartAdapter(Context context, ArrayList<MenuItem> cartItems) {
        if (context instanceof CartTotalChangeListener) {
            this.totalChangeListener = (CartTotalChangeListener) context;
            this.context = context;
            this.dataList = cartItems;
            this.totalChangeListener.cartTotalChanged(getTotal());
            currency = Helper.getSetting(new SharedPreferenceUtil(context), "currency");
            if (TextUtils.isEmpty(currency)) {
                currency = "";
            } else {
                currency = " " + currency;
            }
        } else {
            throw new RuntimeException(context.toString() + " must implement CartTotalChangeListener");
        }

    }

    public Double getTotal() {
        Double total = 0d;
        for (MenuItem item : dataList)
            total += item.getTotal();
        return total;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, price, priceTotal, quantity;
        private ImageView itemImage;

        MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.itemPrice);
            priceTotal = itemView.findViewById(R.id.itemPriceTotal);
            quantity = itemView.findViewById(R.id.itemQuantity);
            itemImage = itemView.findViewById(R.id.itemImage);

            itemView.findViewById(R.id.itemQuantityMinus).setOnClickListener(this);
            itemView.findViewById(R.id.itemQuantityPlus).setOnClickListener(this);
            itemView.findViewById(R.id.itemDelete).setOnClickListener(this);
        }

        public void setData(MenuItem cartItem) {
            name.setText(cartItem.getTitle());
            price.setText("x " + new DecimalFormat("###.##").format(cartItem.getPrice()) + currency);
            priceTotal.setText(new DecimalFormat("###.##").format(cartItem.getTotal()) + currency);
            quantity.setText(String.valueOf(cartItem.getQuantity()));
            Glide.with(context).load(cartItem.getImage_url()).apply(new RequestOptions().placeholder(R.drawable.placeholder_food)).into(itemImage);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            MenuItem item = null;
            if (pos != -1) {
                item = dataList.get(pos);
            }
            if (item != null) {
                switch (v.getId()) {
                    case R.id.itemQuantityMinus:
                        if (item.getQuantity() > 1) {
                            item.setQuantity(item.getQuantity() - 1);
                            totalChangeListener.cartTotalChanged(getTotal());
                            notifyItemChanged(pos);
                        }
                        break;
                    case R.id.itemQuantityPlus:
                        item.setQuantity(item.getQuantity() + 1);
                        totalChangeListener.cartTotalChanged(getTotal());
                        notifyItemChanged(pos);
                        break;
                    case R.id.itemDelete:
                        dataList.remove(pos);
                        totalChangeListener.cartTotalChanged(getTotal());
                        notifyItemRemoved(pos);
                        break;
                }
            }
        }
    }

    public interface CartTotalChangeListener {
        void cartTotalChanged(Double total);
    }

}
