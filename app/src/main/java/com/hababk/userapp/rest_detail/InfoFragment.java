package com.hababk.userapp.rest_detail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hababk.userapp.R;
import com.hababk.userapp.model.Store;

import java.text.DecimalFormat;

public class InfoFragment extends Fragment {
    private Store store;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ((TextView) view.findViewById(R.id.area)).setText(store.getArea());
        ((TextView) view.findViewById(R.id.deliveryTime)).setText(store.getDelivery_time());
        ((TextView) view.findViewById(R.id.minOrder)).setText(new DecimalFormat("###.##").format(store.getMinimum_order()));
        ((TextView) view.findViewById(R.id.deliveryFee)).setText(new DecimalFormat("###.##").format(store.getDelivery_fee()));
        ((TextView) view.findViewById(R.id.preorder)).setText(store.getPreorder() == 1 ? "Yes" : "No");
        return view;
    }

    public static InfoFragment newInstance(Store store) {
        InfoFragment fragment = new InfoFragment();
        fragment.store = store;
        return fragment;
    }
}
