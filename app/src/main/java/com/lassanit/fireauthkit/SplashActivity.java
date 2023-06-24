package com.lassanit.fireauthkit;

import static com.lassanit.authkit.R.drawable.easy_logo;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.lassanit.authkit.interactions.AuthKitCompatActivity;
import com.lassanit.authkit.interactions.AuthKitOptions;
import com.lassanit.authkit.interactions.classes.AndroidApp;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AuthKitCompatActivity implements AuthKitOptions.CallBacks {
    @Override
    public AuthKitOptions setOptions() {
        return new AuthKitOptions.Builder(FirebaseAuth.getInstance(), new AndroidApp("App Name", easy_logo))
                .setCallBacks(this)
                .build();
    }

    @Override
    public void onSignIn() {

    }

    @Override
    public void onSignUp() {
        onSignIn();
    }

    @Override
    public void onEmailVerification(boolean sent) {

    }

    @Override
    public void onFailure(@Nullable Exception exception) {
        if (exception != null) exception.printStackTrace();
    }

    @Override
    public void onPolicyClick() {

    }

    @Override
    public void onTermsAndConditionsClick() {

    }
}
