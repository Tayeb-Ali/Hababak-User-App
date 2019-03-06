package com.hababk.userapp.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hababk.userapp.R;
import com.hababk.userapp.model.User;
import com.hababk.userapp.network.ApiUtils;
import com.hababk.userapp.network.ChefService;
import com.hababk.userapp.network.request.SupportRequest;
import com.hababk.userapp.network.response.SupportResponse;
import com.hababk.userapp.util.Helper;
import com.hababk.userapp.util.SharedPreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportFragment extends Fragment {
    private EditText name, email, message;
    private ProgressBar progressBar;
    private TextView submitSupport, supportPhone, supportEmail;
    private View supportPhoneContainer, supportEmailContainer;
    private Context context;

    public SupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        supportEmail = view.findViewById(R.id.supportEmail);
        supportPhone = view.findViewById(R.id.supportPhone);
        name = view.findViewById(R.id.etName);
        email = view.findViewById(R.id.etEmail);
        message = view.findViewById(R.id.etMessage);
        progressBar = view.findViewById(R.id.progressBar);
        submitSupport = view.findViewById(R.id.submitSupport);
        supportEmailContainer = view.findViewById(R.id.supportEmailContainer);
        supportPhoneContainer = view.findViewById(R.id.supportPhoneContainer);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submitSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText())) {
                    Toast.makeText(getContext(), "Please provide your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    Toast.makeText(getContext(), "Enter valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(message.getText().toString().trim())) {
                    Toast.makeText(getContext(), "Please provide message for support", Toast.LENGTH_SHORT).show();
                    return;
                }
                Helper.closeKeyboard(getContext(), submitSupport);
                submitSupportRequest(new SupportRequest(name.getText().toString(), email.getText().toString(), message.getText().toString().trim()));
            }
        });

        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(getContext());
        User user = Helper.getLoggedInUser(sharedPreferenceUtil);
        if (user != null) {
            name.setText(user.getName());
            email.setText(user.getEmail());
        }
        final String emailSetting = Helper.getSetting(sharedPreferenceUtil, "support_email");
        if (!TextUtils.isEmpty(emailSetting)) {
            supportEmail.setText(emailSetting);
            supportEmail.setSelected(true);
            supportEmailContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", emailSetting, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(Intent.createChooser(emailIntent, "Send email"));
                }
            });
        }
        final String phoneSetting = Helper.getSetting(sharedPreferenceUtil, "support_phone");
        if (!TextUtils.isEmpty(phoneSetting)) {
            supportPhone.setText(phoneSetting);
            supportPhone.setSelected(true);
            supportPhoneContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneSetting, null)));
                }
            });
        }
    }

    private void submitSupportRequest(SupportRequest supportRequest) {
        setSubmitProgress(true);
        ChefService service = ApiUtils.getClient().create(ChefService.class);
        service.support(supportRequest).enqueue(new Callback<SupportResponse>() {
            @Override
            public void onResponse(Call<SupportResponse> call, Response<SupportResponse> response) {
                if (context != null) {
                    setSubmitProgress(false);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Request submitted", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportResponse> call, Throwable t) {
                if (context != null) {
                    setSubmitProgress(false);
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSubmitProgress(boolean b) {
        progressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        submitSupport.setClickable(!b);
    }

}
