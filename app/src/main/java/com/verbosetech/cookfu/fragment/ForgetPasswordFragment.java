package com.verbosetech.cookfu.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.interactor.AuthInnerInteractor;
import com.verbosetech.cookfu.network.ApiUtils;
import com.verbosetech.cookfu.network.ChefService;
import com.verbosetech.cookfu.network.request.ResetPasswordRequest;
import com.verbosetech.cookfu.util.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordFragment extends Fragment {
    private AuthInnerInteractor innerInteractor;
    private ProgressBar progressBar;
    private Button sendButton;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDetach() {
        super.onDetach();
        innerInteractor = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        sendButton = view.findViewById(R.id.send);
        final EditText etEmail = view.findViewById(R.id.etEmail);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()) {
                    Helper.closeKeyboard(getContext(), sendButton);
                    requestReset(new ResetPasswordRequest(etEmail.getText().toString()));
                } else {
                    Toast.makeText(getContext(), "Enter valid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.forgot_pass_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerInteractor.popForgetPassword();
            }
        });
        view.findViewById(R.id.forgot_pass_login_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerInteractor.popForgetPassword();
            }
        });
        return view;
    }

    private void requestReset(ResetPasswordRequest request) {
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setClickable(false);
        ApiUtils.getClient().create(ChefService.class).forgetPassword(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (innerInteractor != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    sendButton.setClickable(true);
                    Toast.makeText(getContext(), response.isSuccessful() ? "Reset instructions sent" : "Unable to send email at the moment", Toast.LENGTH_SHORT).show();
                    innerInteractor.popForgetPassword();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (innerInteractor != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    sendButton.setClickable(true);
                    Toast.makeText(getContext(), "Unfortunately request failed", Toast.LENGTH_SHORT).show();
                    innerInteractor.popForgetPassword();
                }
            }
        });
    }

    public static ForgetPasswordFragment newInstance(AuthInnerInteractor innerInteractor) {
        ForgetPasswordFragment fragment = new ForgetPasswordFragment();
        fragment.innerInteractor = innerInteractor;
        return fragment;
    }
}
