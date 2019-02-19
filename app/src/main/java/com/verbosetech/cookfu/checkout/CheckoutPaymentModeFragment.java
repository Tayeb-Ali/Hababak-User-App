package com.verbosetech.cookfu.checkout;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.verbosetech.cookfu.R;

public class CheckoutPaymentModeFragment extends Fragment {
    private RadioButton radioCod, radioDebit, radioCredit;
    private View creditDetail, debitDetail;

    public CheckoutPaymentModeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkour_payment_mode, container, false);
        radioCod = view.findViewById(R.id.codSelected);
        radioDebit = view.findViewById(R.id.debitSelected);
        radioCredit = view.findViewById(R.id.creditSelected);
        creditDetail = view.findViewById(R.id.creditDetailContainer);
        debitDetail = view.findViewById(R.id.debitDetailContainer);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioCod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    radioCredit.setChecked(false);
                    radioDebit.setChecked(false);
                }
            }
        });
        radioCredit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    radioCod.setChecked(false);
                    radioDebit.setChecked(false);
                }
                creditDetail.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });
        radioDebit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    radioCredit.setChecked(false);
                    radioCod.setChecked(false);
                }
                debitDetail.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });
    }
}
