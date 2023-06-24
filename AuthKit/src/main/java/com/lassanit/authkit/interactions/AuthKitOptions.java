package com.lassanit.authkit.interactions;

import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.lassanit.authkit.interactions.classes.AndroidApp;
import com.lassanit.authkit.interactions.classes.Company;
import com.lassanit.authkit.interactions.classes.Design;

import org.jetbrains.annotations.NotNull;

public class AuthKitOptions {
    private FirebaseAuth auth;
    private AndroidApp app;
    private Company company = null;
    private Design design = null;
    private Method[] methods = null;
    private String wid = null, tag = "FireAuthKit";
    private CallBacks callBacks = null;
    private boolean emailVerify = false;
    private long splashDelay = 1500;

    public static class Builder{
        AuthKitOptions options;
        public Builder(@NotNull FirebaseAuth auth,@NotNull AndroidApp app) {
            options.app = app;
            options.auth = auth;
        }

        public Builder(@NotNull FirebaseApp fapp,@NotNull AndroidApp app) {
            options.app = app;
            options.auth = FirebaseAuth.getInstance(fapp);
        }
        /**
         * @implNote set debug TAG for debugging Log.d(TAG,"msg");
         * <br>
         * default  value is 'FireAuthKit'
         */
        public Builder setCustomTag(String tag) {
            options.tag = tag;
            return this;
        }

        /**
         * @implNote set company name and logo which can be shown in main page right after splash time
         */
        public Builder setCompany(Company company) {
            options.company = company;
            return this;
        }

        /**
         * @implNote set custom background resource designs for Buttons, Input, Activity
         */
        public Builder setCustomDesign(Design des) {
            options.design = des;
            return this;
        }

        /**
         * @implNote Email/Password is mandatory Method which will always be primary
         * in this function pass extra methods to SIGN-IN
         */
        public Builder setSignInMethods(Method[] methods) {
            options.methods = methods;
            return this;
        }

        /**
         * @implSpec to setup GOOGLE authentication you need to register you project in
         * url="<a href="https://console.cloud.google.com/"> Google Cloud</a>"
         * and from OAuth 2.0 Client IDs get *'WEB CLIENT ID'*
         * if you dont have any OAuth 2.0 Client IDs you can create from above given button 'Create Credentials' in that console
         * @implNote make sure it is web client id and not android client id
         */
        public Builder setGoogleWebClientId(String wid) {
            options.wid = wid;
            return this;
        }

        /**
         * @implNote show Splash Screen for how long
         * <br>
         * default time is 1500L = 1.5sec
         */
        public Builder setSplashDelay(long millis) {
            options.splashDelay = millis;
            return this;
        }

        /**
         * @implSpec either allow app to verify email before going home page <br>
         * if true it will always stuck until email is verified by clicking link in the email sent
         */
        public Builder verifyEmail(boolean ver) {
            options.emailVerify = ver;
            return this;
        }

        public Builder setCallBacks(CallBacks callBacks) {
            options.callBacks = callBacks;
            return this;
        }

        public AuthKitOptions build(){
            return options;
        }

    }


    public FirebaseAuth getAuth() {
        return auth;
    }

    public AndroidApp getApp() {
        return app;
    }

    public Company getCompany() {
        return company;
    }

    public Design getDesign() {
        return design;
    }

    public Method[] getMethods() {
        return methods;
    }

    public String getWebClientId() {
        return wid;
    }

    public String getTag() {
        return tag;
    }

    public CallBacks getCallBacks() {
        return callBacks;
    }

    public boolean shouldVerifyEmail() {
        return emailVerify;
    }

    public long getSplashDelay() {
        return splashDelay;
    }

    public enum Method {
        PHONE, GOOGLE, FACEBOOK, TWITTER, GITHUB, YAHOO, MICROSOFT
    }
    public interface CallBacks {
        /**
         * @implNote when user log-in second time or simply and old user logged in
         * <br>
         * if profile info is set it will be a callback just to inform that user is logged-in and
         * now heading towards profile info setup
         * @see #onSignUp()
         */
        void onSignIn();

        /**
         * @implNote when a new user log-in
         * <br>
         * if profile info is set it will be a callback just to inform that user is logged-in and
         * now heading towards profile info setup
         * @see #onSignIn()
         */
        void onSignUp();

        /**
         * @param sent either verification email was sent successful or not
         * @implSpec perform action when email sent to user for Verification
         */
        void onEmailVerification(boolean sent);

        /**
         * @implNote if any error occur while performing action by SSO module
         */
        void onFailure(@Nullable Exception exception);

        void onPolicyClick();
        void onTermsAndConditionsClick();
    }
}
