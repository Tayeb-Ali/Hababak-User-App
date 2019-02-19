package com.verbosetech.cookfu.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.model.RefineSetting;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import io.apptik.widget.MultiSlider;

public class RefineActivity extends AppCompatActivity {
    private RadioButton vegOnly1, vegOnly2, vegOnly3, sortAsc, sortDsc;
    private MultiSlider multiSlider;
    private TextView minText, maxText;
    private SharedPreferenceUtil sharedPreferenceUtils;
    private int minValue, maxValue;
    private RefineSetting refineSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refine);
        sharedPreferenceUtils = new SharedPreferenceUtil(this);
        refineSetting = Helper.getRefineSetting(sharedPreferenceUtils);
        initUi();
        setData();
    }

    private void setData() {
        vegOnly3.setChecked(true);
        vegOnly1.setChecked(refineSetting.isVegOnly());
        boolean asc = refineSetting.getCost_for_two_sort().equals("asc");
        sortAsc.setChecked(asc);
        sortDsc.setChecked(!asc);

        minValue = refineSetting.getCost_for_two_min();
        maxValue = refineSetting.getCost_for_two_max();
        minText.setText("Min: " + minValue);
        maxText.setText(maxValue == 1000 ? "1000+: Max" : maxValue + ": Max");

        multiSlider.getThumb(0).setValue(minValue);
        multiSlider.getThumb(1).setValue(maxValue);
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

        vegOnly1 = findViewById(R.id.vegOnly1);
        vegOnly2 = findViewById(R.id.vegOnly2);
        vegOnly3 = findViewById(R.id.vegOnly3);
        sortAsc = findViewById(R.id.sortAsc);
        sortDsc = findViewById(R.id.sortDsc);
        minText = findViewById(R.id.minText);
        maxText = findViewById(R.id.maxText);

        multiSlider = findViewById(R.id.multiSlider);
        multiSlider.setMin(1);
        multiSlider.setMax(1000);
        multiSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    minText.setText("Min: " + value);
                    minValue = value;
                } else {
                    maxText.setText(value == 1000 ? "1000+: Max" : value + ": Max");
                    maxValue = value;
                }
            }
        });

        findViewById(R.id.applyFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refineSetting.setVegOnly(vegOnly1.isChecked());
                refineSetting.setCost_for_two_sort(sortAsc.isChecked() ? "asc" : "dsc");
                refineSetting.setCost_for_two_min(multiSlider.getThumb(0).getValue());
                refineSetting.setCost_for_two_max(multiSlider.getThumb(1).getValue());
                Helper.setRefineSetting(sharedPreferenceUtils, refineSetting);
                sharedPreferenceUtils.setBooleanPreference(Constants.KEY_RELOAD_STORES, true);
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionResetRefine:
                refineSetting = RefineSetting.getDefault();
                setData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refine, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
