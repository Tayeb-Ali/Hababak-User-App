package com.hababk.userapp.interactor;

/**
 * Created by a_man on 19-01-2018.
 */

public interface AuthInnerInteractor {
    void switchToSignIn();

    void switchToSignUp();

    void switchToForgetPassword();

    void switchToPhoneVerification(String mobile_number);

    void switchToMain();

    void popForgetPassword();
}
