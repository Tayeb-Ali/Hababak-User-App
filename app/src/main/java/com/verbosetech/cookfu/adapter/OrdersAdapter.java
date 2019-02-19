package com.verbosetech.cookfu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.activity.OrderDetailActivity;
import com.verbosetech.cookfu.activity.PostReviewActivity;
import com.verbosetech.cookfu.checkout.CheckoutSummaryAdapter;
import com.verbosetech.cookfu.model.Order;
import com.verbosetech.cookfu.model.RequestItem;
import com.verbosetech.cookfu.network.response.MenuItem;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by a_man on 24-01-2018.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Order> dataList;
    private DetailToggleListener detailToggleListener;
    private String currency;

    public OrdersAdapter(Context context, ArrayList<Order> orders, DetailToggleListener detailToggleListener) {
        this.context = context;
        this.dataList = orders;
        this.detailToggleListener = detailToggleListener;
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
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order, parent, false));
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
        private View orderPreparingContainer;
        private TextView orderDate, orderPlaceName, orderPlaceAddress, orderReview, orderTotal, orderStatus, total, subTotal, paymentMode, feeService, feeDelivery, discountTv;
        private RecyclerView itemRecycler;
        private ImageView[] statusIcons = new ImageView[5];
        private DecimalFormat decimalFormat;

        MyViewHolder(View itemView) {
            super(itemView);
            decimalFormat = new DecimalFormat("###.##");
            orderPreparingContainer = itemView.findViewById(R.id.orderPreparingContainer);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderPlaceName = itemView.findViewById(R.id.orderPlaceName);
            orderPlaceAddress = itemView.findViewById(R.id.orderPlaceAddress);
            orderReview = itemView.findViewById(R.id.orderReview);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            paymentMode = itemView.findViewById(R.id.paymentMode);
            discountTv = itemView.findViewById(R.id.discount);

            statusIcons[0] = itemView.findViewById(R.id.statusIcon1);
            statusIcons[1] = itemView.findViewById(R.id.statusIcon2);
            statusIcons[2] = itemView.findViewById(R.id.statusIcon3);
            statusIcons[3] = itemView.findViewById(R.id.statusIcon4);
            statusIcons[4] = itemView.findViewById(R.id.statusIcon5);

            itemRecycler = itemView.findViewById(R.id.itemRecycler);
            total = itemView.findViewById(R.id.total);
            subTotal = itemView.findViewById(R.id.subTotal);
            feeDelivery = itemView.findViewById(R.id.feeDelivery);
            feeService = itemView.findViewById(R.id.feeService);

            itemView.findViewById(R.id.detailContainerToggle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderPreparingContainer.setVisibility(orderPreparingContainer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    if (orderPreparingContainer.getVisibility() == View.VISIBLE) {
                        detailToggleListener.onDetailVisible(getAdapterPosition());
                    }
                }
            });
            itemView.findViewById(R.id.trackingContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != -1 && dataList.get(pos).getDelivery_profile() != null && dataList.get(pos).getDelivery_status().equals("started")) {
                        context.startActivity(OrderDetailActivity.newInstance(context, dataList.get(pos)));
                    } else {
                        Toast.makeText(context, "Tracking data not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            orderReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != -1 && dataList.get(pos).getStore() != null) {
                        context.startActivity(PostReviewActivity.newInstance(context, dataList.get(pos).getStore()));
                    }
                }
            });
        }

        public void setData(Order order) {
            String dateArr[] = Helper.timeString(order.getCreated_at()).split(" ");
            orderDate.setText(dateArr[0] + "\n" + dateArr[1]);
            orderPlaceAddress.setText(order.getStore().getAddress());
            orderPlaceAddress.setSelected(true);
            orderPlaceName.setText(order.getStore().getName());
            orderPlaceName.setSelected(true);
            orderTotal.setText(decimalFormat.format(order.getTotal() + order.getDelivery_fee()) + currency);
            orderStatus.setText(order.getStatus().substring(0, 1).toUpperCase() + order.getStatus().substring(1));
            orderStatus.setBackground(ContextCompat.getDrawable(context, order.getStatus().equals("complete") ? R.drawable.round_circle_accent : R.drawable.round_circle_primary));

            itemRecycler.setNestedScrollingEnabled(false);
            itemRecycler.setLayoutManager(new LinearLayoutManager(context));
            ArrayList<MenuItem> menuItems = new ArrayList<>();
            for (RequestItem requestItem : order.getOrderitems()) {
                requestItem.getMenuitem().setQuantity(requestItem.getQuantity());
                menuItems.add(requestItem.getMenuitem());
            }
            itemRecycler.setAdapter(new CheckoutSummaryAdapter(context, menuItems));

            subTotal.setText(decimalFormat.format(order.getSubtotal()));
            feeService.setText(decimalFormat.format(order.getSubtotal() * order.getTaxes() / 100));
            feeDelivery.setText(decimalFormat.format(order.getDelivery_fee()));
            discountTv.setText(decimalFormat.format(order.getDiscount()));
            total.setText(decimalFormat.format(order.getTotal() + order.getDelivery_fee()) + currency);
            paymentMode.setText(order.getPayment_method_id() == 1 ? "Cash payable on delivery:" : "Paid amount:");

            orderReview.setVisibility(order.getStatus().equals("complete") ? View.VISIBLE : View.GONE);
            orderPlaceAddress.setVisibility(order.getStatus().equals("complete") ? View.GONE : View.VISIBLE);

            int posTill = -1;
            switch (order.getStatus()) {
                case "complete":
                    posTill = 4;
                    break;
//                case "intransit":
//                    posTill = 3;
//                    break;
                case "dispatched":
                    posTill = 3;
                    break;
                case "accepted":
                    posTill = 1;
                    break;
                case "new":
                    posTill = 0;
                    break;
            }
            for (int i = 0; i < statusIcons.length; i++) {
                statusIcons[i].setBackground(ContextCompat.getDrawable(context, i <= posTill ? R.drawable.round_circle_primary : R.drawable.round_circle_gray));
            }
        }
    }

    public interface DetailToggleListener {
        void onDetailVisible(int pos);
    }
}
