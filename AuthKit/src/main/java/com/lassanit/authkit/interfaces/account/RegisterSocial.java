package com.lassanit.authkit.interfaces.account;

import androidx.annotation.NonNull;

import com.google.firebase.auth.OAuthProvider;

public interface RegisterSocial {
    void loginWithProvider(@NonNull OAuthProvider provider);

    void onFacebookClick();

    void onGoogleClick();

    void onPhoneClick();
}
