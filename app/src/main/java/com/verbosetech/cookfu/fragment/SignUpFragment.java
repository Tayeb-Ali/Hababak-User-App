package com.verbosetech.cookfu.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.interactor.AuthInnerInteractor;
import com.verbosetech.cookfu.model.Country;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.request.RegisterRequest;
import com.verbosetech.cookfu.network.response.AuthResponse;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFragment extends BaseFragment {
    private EditText name, email, phone, password1, password2;
    private AppCompatSpinner countryCode;
    private TextView mRegisterBtn, mSignInTv, errorText;
    private ProgressBar progressBar;
    private AuthInnerInteractor innerInteractor;

    private ChefService chefService;
    private SharedPreferenceUtil sharedPreferenceUtil;


    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(AuthInnerInteractor innerInteractor) {
        SignUpFragment fragment = new SignUpFragment();
        fragment.innerInteractor = innerInteractor;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chefService = ApiUtils.getClient().create(ChefService.class);
        sharedPreferenceUtil = new SharedPreferenceUtil(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mRegisterBtn = view.findViewById(R.id.signUp);
        mSignInTv = view.findViewById(R.id.switchSignIn);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        countryCode = view.findViewById(R.id.countryCode);
        errorText = view.findViewById(R.id.errorText);
        password1 = view.findViewById(R.id.password);
        password2 = view.findViewById(R.id.passwordConfirm);
        progressBar = view.findViewById(R.id.progressBar);
        setupCountryCodes();
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText())) {
                    toast(getString(R.string.name_error), true);
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    toast(getString(R.string.email_error), true);
                    return;
                }
                if (phone.getText().toString().length() != 10 && !Patterns.PHONE.matcher(phone.getText()).matches()) {
                    toast(getString(R.string.phone_error), true);
                    return;
                }
                if (password1.getText().toString().length() < 6) {
                    toast("Provide at least 6 character password", true);
                    return;
                }
                if (TextUtils.isEmpty(password1.getText()) || TextUtils.isEmpty(password2.getText()) || !password1.getText().toString().equals(password2.getText().toString())) {
                    toast("Please enter valid password, Twice.", true);
                    return;
                }
                setProgressRegister(true);
                onClickRegister(new RegisterRequest(name.getText().toString(), email.getText().toString(), password1.getText().toString(), (((Country) countryCode.getSelectedItem()).getDialCode() + phone.getText().toString()).replaceAll("\\s+", "")));
            }
        });
        mSignInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerInteractor.switchToSignIn();
            }
        });
        return view;
    }

    private void setupCountryCodes() {
        ArrayList<Country> countries = getCountries();
        if (countries != null) {
            ArrayAdapter<Country> myAdapter = new ArrayAdapter<Country>(getContext(), R.layout.item_country_spinner, countries);
            countryCode.setAdapter(myAdapter);
        }
    }

    private ArrayList<Country> getCountries() {
        ArrayList<Country> toReturn = new ArrayList<>();
//        toReturn.add(new Country("RU", "Russia", "+7"));
//        toReturn.add(new Country("TJ", "Tajikistan", "+992"));
//        toReturn.add(new Country("US", "United States", "+1"));
//        return toReturn;

        try {
            JSONArray countrArray = new JSONArray(readEncodedJsonString(getContext()));
            toReturn = new ArrayList<>();
            for (int i = 0; i < countrArray.length(); i++) {
                JSONObject jsonObject = countrArray.getJSONObject(i);
                String countryName = jsonObject.getString("name");
                String countryDialCode = jsonObject.getString("dial_code");
                String countryCode = jsonObject.getString("code");
                Country country = new Country(countryCode, countryName, countryDialCode);
                toReturn.add(country);
            }
            Collections.sort(toReturn, new Comparator<Country>() {
                @Override
                public int compare(Country lhs, Country rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    private String readEncodedJsonString(Context context) throws java.io.IOException {
        String base64 = context.getResources().getString(R.string.countries_code);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

    public void onClickRegister(final RegisterRequest registerRequest) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(registerRequest.getMobile_number());
//        alertDialog.setMessage("Are your sure to use this number? As this will be used for phone number verification.\nKindly verify!");
        alertDialog.setMessage("هل انت متاكد من انك ادخلة بيانات صحيحة.\n!");
        alertDialog.setPositiveButton("نعم متاكد", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                register(registerRequest);
            }
        });
        alertDialog.setNegativeButton("لا, احتاج الي التعديل", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                phone.requestFocus();
            }
        });
        alertDialog.show();
    }

    private void register(RegisterRequest registerRequest) {
        chefService.register(registerRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setProgressRegister(false);
                if (response.isSuccessful()) {
                    sharedPreferenceUtil.setStringPreference(Constants.KEY_TOKEN, response.body().getToken());
                    Helper.setLoggedInUser(sharedPreferenceUtil, response.body().getUser());
                    innerInteractor.switchToMain();
//                    innerInteractor.switchToPhoneVerification(response.body().getUser().getMobile_number());
                } else {
                    if (errorText != null) {
                        errorText.setVisibility(View.VISIBLE);
                        try {
                            JSONObject errorObject = new JSONObject(response.errorBody().string());
                            if (errorObject.has("errors")) {
                                JSONObject errors = errorObject.getJSONObject("errors");
                                notifyError(errors, "email");
                                notifyError(errors, "password");
                                notifyError(errors, "mobile_number");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorText.setText(R.string.signup_error);
                        } catch (IOException e) {
                            errorText.setText(R.string.signup_error);
                            e.printStackTrace();
                        }
                    }
                    //toast("Unable to register with provided credentails", true);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setProgressRegister(false);
                if (errorText != null) {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("عذراً, حصل خطاء ما!");
                }
                //toast("Something went wrong", true);
            }
        });
    }

    private void notifyError(JSONObject errors, String field) throws JSONException {
        if (errors != null && errors.has(field)) {
            JSONArray fieldArr = errors.getJSONArray(field);
            if (fieldArr.length() > 0) {
                errorText.setText(fieldArr.get(0).toString());
                //toast(fieldArr.get(0).toString(), true);
            }
        }
    }

    private void setProgressRegister(boolean b) {
        if (progressBar != null) {
            progressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
            mRegisterBtn.setClickable(!b);
            mSignInTv.setClickable(!b);
            if (b) errorText.setVisibility(View.GONE);
        }
    }

}
