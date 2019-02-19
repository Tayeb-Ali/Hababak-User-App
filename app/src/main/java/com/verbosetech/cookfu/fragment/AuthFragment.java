package com.verbosetech.cookfu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.verbosetech.cookfu.R;
import com.verbosetech.cookfu.adapter.ViewPagerAdapter;
import com.verbosetech.cookfu.interactor.AuthInnerInteractor;
import com.verbosetech.cookfu.interactor.AuthMainInteractor;

public class AuthFragment extends Fragment {
    private AuthMainInteractor mListener;
    private AuthInnerInteractor innerInteractor;
    private ViewPager viewPager;

    public AuthFragment() {
        // Required empty public constructor
    }


    public static AuthFragment newInstance(AuthMainInteractor mListener) {
        AuthFragment fragment = new AuthFragment();
        fragment.mListener = mListener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        innerInteractor = new AuthInnerInteractor() {
            @Override
            public void switchToSignIn() {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(0);
                    }
                }, 100);
            }

            @Override
            public void switchToSignUp() {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(1);
                    }
                }, 100);
            }

            @Override
            public void switchToForgetPassword() {
                getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.bottom_up, R.anim.bottom_down, R.anim.bottom_up, R.anim.bottom_down)
                        .add(R.id.authFrame, ForgetPasswordFragment.newInstance(innerInteractor), ForgetPasswordFragment.class.getName())
                        .addToBackStack(ForgetPasswordFragment.class.getName())
                        .commit();
            }

            @Override
            public void switchToPhoneVerification(String mobile_number) {
                getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.bottom_up, R.anim.bottom_down, R.anim.bottom_up, R.anim.bottom_down)
                        .replace(R.id.authFrame, VerificationCodeFragment.newInstance(innerInteractor, mobile_number), VerificationCodeFragment.class.getName())
                        .commit();
            }

            @Override
            public void switchToMain() {
                mListener.moveToMain();
            }

            @Override
            public void popForgetPassword() {
                getChildFragmentManager().popBackStackImmediate();
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(SignInFragment.newInstance(innerInteractor), "Sign in");
        adapter.addFragment(SignUpFragment.newInstance(innerInteractor), "Register");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
