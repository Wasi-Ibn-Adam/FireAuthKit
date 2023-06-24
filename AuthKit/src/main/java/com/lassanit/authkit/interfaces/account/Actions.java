package com.lassanit.authkit.interfaces.account;

import com.google.firebase.auth.AdditionalUserInfo;

public interface Actions {
    void onSplashLoad();

    void onPolicyTermsAccepted();

    void onForgetClick();

    void onResetLinkSent();

    void onRegisterClick();

    void onRegisterComplete();

    void onLoginComplete();

    void onPhoneSignInDone();

    void showLoadingWindow();

    void dismissLoadingWindow();

    void checkUser(AdditionalUserInfo res);

    void allowBackPresses();

    void stopBackPresses();

    void onFragViewCreated(String fragName);
}
