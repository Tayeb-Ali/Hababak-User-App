package com.verbosetech.cookfu.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.verbosetech.cookfu.activity.AddEditAddressActivity;
import com.verbosetech.cookfu.network.response.Address;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.util.ArrayList;

/**
 * Created by a_man on 24-01-2018.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {
    private AddressSelectedListener addressSelectedListener;
    private Context context;
    private ArrayList<Address> dataList;
    private Address defaultAddress;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ArrayList<MenuItem> cartItems;

    public AddressAdapter(Context context, ArrayList<Address> addresses, AddressSelectedListener addressSelectedListener) {
        this.context = context;
        this.dataList = addresses;
        this.addressSelectedListener = addressSelectedListener;
        this.sharedPreferenceUtil = new SharedPreferenceUtil(context);
        this.defaultAddress = Helper.getAddressDefault(sharedPreferenceUtil);
        this.cartItems = Helper.getCart(this.sharedPreferenceUtil);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, addressText;
        private RadioButton selectedRadio;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.addressTitle);
            addressText = itemView.findViewById(R.id.addressText);
            selectedRadio = itemView.findViewById(R.id.addressSelected);

            itemView.findViewById(R.id.editAddress).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != -1) {
                        context.startActivity(AddEditAddressActivity.newInstance(context, dataList.get(pos)));
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != -1) {
                        final Address address = dataList.get(getAdapterPosition());

                        if (defaultAddress != null && !defaultAddress.getId().equals(address.getId()) && !cartItems.isEmpty()) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Changing default address!")
                                    .setMessage("Your cart contains " + cartItems.size() + " item(s). Changing default address will clear your cart item(s). Do you want to continue?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Helper.clearCart(sharedPreferenceUtil);
                                            cartItems.clear();
                                            defaultAddress = address;
                                            Helper.setAddressDefault(sharedPreferenceUtil, address);
                                            notifyDataSetChanged();
                                            addressSelectedListener.onAddressSelected(defaultAddress);
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();
                        } else {
                            defaultAddress = address;
                            Helper.setAddressDefault(sharedPreferenceUtil, address);
                            notifyDataSetChanged();
                            addressSelectedListener.onAddressSelected(defaultAddress);
                        }

                    }
                }
            });
        }

        public void setData(Address address) {
            title.setText(address.getTitle());
            addressText.setText(address.getAddress());

            if (defaultAddress != null)
                selectedRadio.setChecked(address.getId().equals(defaultAddress.getId()));
        }
    }

    public interface AddressSelectedListener {
        void onAddressSelected(Address address);
    }
}
