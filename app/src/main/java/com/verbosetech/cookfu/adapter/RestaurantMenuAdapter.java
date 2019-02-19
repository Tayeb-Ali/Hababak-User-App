package com.verbosetech.cookfu.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.activity.FoodDetailActivity;
import com.verbosetech.cookfu.activity.PostReviewActivity;
import com.verbosetech.cookfu.model.Store;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by a_man on 24-01-2018.
 */

public class RestaurantMenuAdapter extends RecyclerView.Adapter<RestaurantMenuAdapter.MyViewHolder> {
    private Store store, savedStore;
    private Context context;
    private ArrayList<MenuItem> dataList, cartItems;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private String currency;

    RestaurantMenuAdapter(Context context, Store store, Store storeSaved, ArrayList<MenuItem> menuItems, ArrayList<MenuItem> cartItems) {
        this.context = context;
        this.dataList = menuItems;
        this.cartItems = cartItems;
        this.store = store;
        this.savedStore = storeSaved;
        sharedPreferenceUtil = new SharedPreferenceUtil(context);
        currency = Helper.getSetting(sharedPreferenceUtil, "currency");
        if (TextUtils.isEmpty(currency)) {
            currency = "";
        } else {
            currency = " " + currency;
        }
        for (MenuItem cartItem : cartItems) {
            int index = dataList.indexOf(cartItem);
            if (index != -1) {
                dataList.get(index).setAdded(true);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_restaurant_menu, parent, false));
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
        private TextView itemName, itemDescription, itemPrice;
        private ImageView itemImage, menuItemAction;

        MyViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.menuItemName);
            itemDescription = itemView.findViewById(R.id.menuItemDescription);
            itemPrice = itemView.findViewById(R.id.menuItemPrice);
            itemImage = itemView.findViewById(R.id.menuItemImage);
            menuItemAction = itemView.findViewById(R.id.menuItemAction);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != -1)
                        context.startActivity(FoodDetailActivity.newIntent(context, store, savedStore, dataList.get(pos), cartItems));
                }
            });

            menuItemAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (savedStore == null || savedStore.getId().equals(store.getId())) {
                        int pos = getAdapterPosition();
                        MenuItem item = dataList.get(pos);
                        item.setAdded(!dataList.get(pos).isAdded());

                        if (item.isAdded()) {
                            if (!cartItems.contains(item)) {
                                cartItems.add(item);
                                broadcastCart();
                            }
                        } else {
                            cartItems.remove(item);
                            broadcastCart();
                        }

                        Helper.setCart(sharedPreferenceUtil, cartItems);
                        if (savedStore == null) {
                            savedStore = store;
                            Helper.setCartStore(sharedPreferenceUtil, store);
                        }

                        notifyItemChanged(pos);
                    } else if (savedStore != null && !savedStore.getId().equals(store.getId())) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("Cart conflict");
                        dialog.setMessage("Your cart consists items from another restaurant, do you want to clear your cart to add items from this restaurant?");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                savedStore = null;
                                cartItems.clear();
                                Helper.clearCart(sharedPreferenceUtil);
                                broadcastCart();
                                Toast.makeText(context, "Cart Cleared, try adding item again", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });
        }

        public void setData(MenuItem restaurantMenu) {
            Glide.with(context).load(restaurantMenu.getImage_url()).apply(new RequestOptions().placeholder(R.drawable.placeholder_restaurant)).into(itemImage);
            itemPrice.setText(new DecimalFormat("###.##").format(restaurantMenu.getPrice()) + currency);
            itemDescription.setText(restaurantMenu.getDetail());
            itemName.setText(restaurantMenu.getTitle());
            menuItemAction.setImageDrawable(ContextCompat.getDrawable(context, restaurantMenu.isAdded() ? R.drawable.ic_done_primary_24dp : R.drawable.ic_add_circle_primary_24dp));
        }
    }

    private void broadcastCart() {
        Intent intent = new Intent(Constants.BROADCAST_CART);
        intent.putExtra("data", cartItems);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
