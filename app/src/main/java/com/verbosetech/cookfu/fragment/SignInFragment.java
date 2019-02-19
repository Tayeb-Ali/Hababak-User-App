package com.verbosetech.cookfu.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.interactor.AuthInnerInteractor;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.request.LoginRequest;
import com.verbosetech.cookfu.network.response.AuthResponse;
import com.verbosetech.cookfu.util.Constants;
import com.verbosetech.cookfu.util.Helper;
import com.verbosetech.cookfu.util.SharedPreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInFragment extends BaseFragment implements View.OnClickListener {
    TextView mForgotPassTv, mSignInBtn, mSignUpTv, errorText;
    EditText mPasswordEt, mEmailEt;
    private ProgressBar progressBar;

    private ChefService chefService;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private AuthInnerInteractor innerInteractor;


    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance(AuthInnerInteractor innerInteractor) {
        SignInFragment fragment = new SignInFragment();
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
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mForgotPassTv = view.findViewById(R.id.forgetPassword);
        mSignInBtn = view.findViewById(R.id.signIn);
        mSignUpTv = view.findViewById(R.id.switchSignUp);
        mPasswordEt = view.findViewById(R.id.password);
        mEmailEt = view.findViewById(R.id.email);
        errorText = view.findViewById(R.id.errorText);
        progressBar = view.findViewById(R.id.progressBar);

        view.findViewById(R.id.forgetPassword).setOnClickListener(this);
        view.findViewById(R.id.switchSignUp).setOnClickListener(this);
        view.findViewById(R.id.signIn).setOnClickListener(this);
        View guestContinueView = view.findViewById(R.id.guestContinue);
        guestContinueView.setOnClickListener(this);
        guestContinueView.setVisibility(getString(R.string.is_demo).equalsIgnoreCase("true") ? View.VISIBLE : View.GONE);
        return view;
    }

    public void onClickSignIn() {
        if (!Patterns.EMAIL_ADDRESS.matcher(mEmailEt.getText()).matches()) {
            toast("Enter valid email address", true);
            return;
        }
        if (TextUtils.isEmpty(mPasswordEt.getText())) {
            toast("Enter password", true);
            return;
        }

        setProgressLogin(true);
        chefService.login(new LoginRequest(mEmailEt.getText().toString(), mPasswordEt.getText().toString())).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setProgressLogin(false);
                if (response.isSuccessful()) {
                    sharedPreferenceUtil.setStringPreference(Constants.KEY_TOKEN, response.body().getToken());
                    Helper.setLoggedInUser(sharedPreferenceUtil, response.body().getUser());
                    if (innerInteractor != null) {
                        if (response.body().getUser().getMobile_verified() == 1) {
                            innerInteractor.switchToMain();
                        } else {
                            innerInteractor.switchToPhoneVerification(response.body().getUser().getMobile_number());
                        }
                    }
                } else {
                    if (errorText != null) {
                        errorText.setVisibility(View.VISIBLE);
                        errorText.setText("Unable to authorise with provided credentials");
                    }
                    //toast("Unable to authorise with provided credentials", true);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setProgressLogin(false);
                if (errorText != null) {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("Something went wrong");
                }
                //toast("Something went wrong", true);
            }
        });
    }

    private void setProgressLogin(boolean b) {
        if (progressBar != null) {
            progressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
            mForgotPassTv.setClickable(!b);
            mSignInBtn.setClickable(!b);
            mSignUpTv.setClickable(!b);
            if (b) errorText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgetPassword:
                innerInteractor.switchToForgetPassword();
                break;
            case R.id.switchSignUp:
                innerInteractor.switchToSignUp();
                break;
            case R.id.signIn:
                onClickSignIn();
                break;
            case R.id.guestContinue:
                mEmailEt.setText("guest@user.com");
                mPasswordEt.setText("12341234");
                onClickSignIn();
                break;
        }
    }
}
