package com.lassanit.authkit.interactions;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.lassanit.authkit.R;
import com.lassanit.authkit.WaitingPopUp;
import com.lassanit.authkit.fragments.FragNavHandler;
import com.lassanit.authkit.fragments.account.Frag;
import com.lassanit.authkit.fragments.account.FragIntro;
import com.lassanit.authkit.fragments.account.FragLogin;
import com.lassanit.authkit.fragments.account.FragPhone;
import com.lassanit.authkit.fragments.account.FragPolicyTerms;
import com.lassanit.authkit.fragments.account.FragRegister;
import com.lassanit.authkit.fragments.account.FragReset;
import com.lassanit.authkit.fragments.account.FragSocial;
import com.lassanit.authkit.fragments.account.FragSplash;
import com.lassanit.authkit.interfaces.account.Actions;
import com.lassanit.authkit.interfaces.account.PolicyTermInter;

import java.util.Objects;

public abstract class AuthKitCompatActivity extends AuthKitActivity implements Actions {


    public abstract AuthKitOptions setOptions();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sso);
        options = setOptions();
        try {
            if (options == null) {
                finishAndRemoveTask();
                return;
            }
            setBg();
            auth = options.getAuth();
            //loadFrags
            topNav = new FragNavHandler(AuthKitCompatActivity.this, getSupportFragmentManager(), R.id.top_frame, 0, options.getApp());
            topNav.addFragmentChangeListener((name, attached) -> {
                Log.d("sso", "change " + name + "attached: " + attached);

                if (name.equalsIgnoreCase("FragReset") || name.equalsIgnoreCase("FragPhone")) {
                    Log.d("sso", "hide me " + name);
                    btmNav.hide(FragSocial.class);
                } else {
                    Log.d("sso", "show me " + name);
                    btmNav.show(FragSocial.class);
                }
            });
            topNav.push(new FragSplash());

            btmNav = new FragNavHandler(this, getSupportFragmentManager(), R.id.btm_frame, 1, options.getApp());
            btmNav.setAnim(R.anim.show, R.anim.hide);
            btmNav.setHideAnime(R.anim.show, R.anim.hide);

            setGoogle();
            popUp = new WaitingPopUp(AuthKitCompatActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSplashLoad() {
        FragSplash prev = (FragSplash) topNav.getFragment(FragSplash.class);
        if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(POLICY_TERMS_CONDITIONS, false)) {
            loadLogin(prev, options.getSplashDelay());
        } else {
            loadPolicyTerms(prev, options.getSplashDelay());
        }
    }

    @Override
    public void onRegisterClick() {
        FragLogin login = (FragLogin) topNav.getFragment(FragLogin.class);
        FragRegister reg = new FragRegister();
        if (options != null) reg.setDesign(options.getDesign());
        if (login != null) {
            topNav.push(reg, login.getDefaultLinker());
        } else topNav.push(reg);
    }

    @Override
    public void onRegisterComplete() {
        topNav.pop();
        if (options != null && options.getCallBacks() != null)
            options.getCallBacks().onSignUp();
    }

    @Override
    public void stopBackPresses() {
        backPress = false;
    }

    @Override
    public void onFragViewCreated(String fragName) {
    }

    @Override
    public void allowBackPresses() {
        backPress = true;
    }

    @Override
    public void onBackPressed() {
        if (backPress && !topNav.handleBackPress()) {
            finishAndRemoveTask();
        }
    }

    private void setGoogle() {
        oneTapClient = Identity.getSignInClient(this);
        request = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true).setFilterByAuthorizedAccounts(false)
                .setServerClientId(options.getWebClientId()).build()).build();
        launcher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if (result.getResultCode() != RESULT_OK) {
                onCompletionCancel(null);
                return;
            }
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                AuthCredential authCredential = GoogleAuthProvider.getCredential(credential.getGoogleIdToken(), null);
                auth.signInWithCredential(authCredential).addOnSuccessListener(authResult -> properCheck(Objects.requireNonNull(authResult.getUser()))).addOnFailureListener(this::onCompletionCancel);
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED:
                        Log.d(TAG, "One-tap dialog was closed.");
                        // Don't re-prompt the user.
                        break;
                    case CommonStatusCodes.NETWORK_ERROR:
                        Log.d(TAG, "One-tap encountered a network error.");
                        // Try again or just ignore.
                        break;
                    default:
                        Log.d(TAG, "Couldn't get credential from result." + e.getLocalizedMessage());
                        break;
                }
            } catch (Exception e) {
                Log.d(TAG, "One-tap error: " + e.getLocalizedMessage());
                onCompletionCancel(e);
            }

        });
    }

    private void properCheck(@NonNull FirebaseUser user) {
        if (!user.isEmailVerified() && options != null && options.shouldVerifyEmail()) {
            Log.d(TAG, "User Email Verification.");
            verifyEmail(user);
        } else {
            onCompletion();
        }
    }

    @Override
    public void loginWithProvider(@NonNull OAuthProvider oAuth) {
        auth.startActivityForSignInWithProvider(AuthKitCompatActivity.this, oAuth)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "loginWithProvider " + oAuth.getProviderId() + " startActivityForSignInWithProvider: COMPLETE");
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        Log.d(TAG, "loginWithProvider " + oAuth.getProviderId() + " startActivityForSignInWithProvider: SUCCESS");
                        checkUser(task.getResult().getAdditionalUserInfo());
                    }
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "This email is Registered with different Sign-In Provider." +
                                " Sign in using a provider associated with this email address", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "loginWithProvider " + oAuth.getProviderId() + " startActivityForSignInWithProvider: ERROR: " + e.getMessage());
                        onCompletionCancel(e);
                    }
                });
    }

    @Override
    public void showLoadingWindow() {
        popUp.show();
    }

    @Override
    public void dismissLoadingWindow() {
        popUp.dismiss();
    }

    @Override
    public void checkUser(AdditionalUserInfo res) {
        try {
            assert res != null;
            first = res.isNewUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPhoneSignInDone() {
        topNav.pop();
        FirebaseUser user = AuthKitActivity.auth.getCurrentUser();
        if (user != null) {
            properCheck(user);
        } else {
            Toast.makeText(this, "Unknown error found, Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyEmail(@NonNull FirebaseUser user) {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder().setUrl("https://sso.com/uid=" + user.getUid()).setHandleCodeInApp(true).build();
        AuthKitActivity.auth.sendSignInLinkToEmail(Objects.requireNonNull(user.getEmail()), actionCodeSettings).addOnCompleteListener(task -> {
            Log.d(TAG, "USER updateName: " + (task.isSuccessful() ? "SUCCESS." : "ERROR. "));
            if (options != null && options.getCallBacks() != null)
                options.getCallBacks().onEmailVerification(task.isSuccessful());
        });
    }

    @Override
    public void onCompletionInComplete() {
        // FAILED

    }

    // ACTIONS
    @Override
    public void onCompletion() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        if (options.getCallBacks() != null) {
            if (first) options.getCallBacks().onSignUp();
            else options.getCallBacks().onSignIn();
        }
    }

    @Override
    public void onCompletionCancel(@Nullable Exception e) {
        AuthKitActivity.auth.signOut();
        if (options.getCallBacks() != null) {
            options.getCallBacks().onFailure(e);
        }
        if (e != null) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPolicyTermsAccepted() {
        btmNav.pop();
        topNav.popPrev();
        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putBoolean(POLICY_TERMS_CONDITIONS, true).apply();
        FragIntro prev = (FragIntro) topNav.getFragment(FragIntro.class);
        loadLogin(prev, 0);
    }

    @Override
    public void onLoginComplete() {
        if (options.getCallBacks() != null)
            options.getCallBacks().onSignIn();
    }

    @Override
    public void onForgetClick() {
        FragLogin frag = (FragLogin) topNav.getFragment(FragLogin.class);
        FragReset reset = new FragReset();
        if (options != null)
            reset.setDesign(options.getDesign());
        if (frag != null) {
            topNav.push(reset, frag.getDefaultLinker());
        } else topNav.push(reset);
    }

    @Override
    public void onResetLinkSent() {
        Toast.makeText(AuthKitCompatActivity.this, "Password Reset link is sent to your email.", Toast.LENGTH_SHORT).show();
        topNav.pop();
    }

    void setBg() {
        try {
            View v = findViewById(R.id.include);
            Drawable dr = null;
            if (options.getDesign() != null && options.getDesign().getBackgroundRes() != -1) {
                View v1 = LayoutInflater.from(this).inflate(options.getDesign().getBackgroundRes(), findViewById(R.id.layout));
                v1.setDrawingCacheEnabled(true);
                v1.buildDrawingCache();
                Bitmap bm = v1.getDrawingCache();
                if (bm != null)
                    dr = new BitmapDrawable(getResources(), bm);
            }
            if (dr != null)
                v.setBackground(dr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFacebookClick() {

    }

    @Override
    public void onGoogleClick() {
        oneTapClient.beginSignIn(request).addOnSuccessListener(beginSignInResult -> {
                    try {
                        Log.d(TAG, "One Tap beginSignIn.");
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender()).build();
                        launcher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        onCompletionCancel(e);
                    }

                })
                .addOnFailureListener(e1 -> {
                    Log.d(TAG, "One Tap beginSignIn: ERROR: " + e1.getMessage());
                    if (e1.getLocalizedMessage() != null && e1.getLocalizedMessage().equalsIgnoreCase("16: Caller has been temporarily blocked due to too many canceled sign-in prompts.")) {
                        Toast.makeText(this, "Too many Wrong Attempts, try again later.", Toast.LENGTH_SHORT).show();
                    } else {
                        onCompletionCancel(e1);
                    }
                });
    }

    @Override
    public void onPhoneClick() {
        FragLogin login = (FragLogin) topNav.getFragment(FragLogin.class);
        FragPhone phone = new FragPhone();
        if (options != null) {
            phone.setDesign(options.getDesign());
        }

        if (login != null) {
            topNav.push(phone, login.getDefaultLinker());
        } else
            topNav.push(phone);
    }

    protected void loadLogin(Frag prev, long delay) {
        FragLogin login = new FragLogin();
        if (options != null) {
            login.setDesign(options.getDesign());
        }
        if (prev != null) {
            if (delay == 0)
                topNav.push(login, prev.getDefaultLinker());
            else
                topNav.push(login, prev.getDefaultLinker(), delay);
        } else {
            if (delay == 0)
                topNav.push(login);
            else
                topNav.push(login, delay);
        }
        FragSocial social = new FragSocial(options.getCompany(), options.getMethods());
        btmNav.push(social, delay);
    }

    protected void loadPolicyTerms(Frag prev, long delay) {
        if (prev != null) {
            topNav.push(new FragIntro(), prev.getDefaultLinker(), delay);
        } else
            topNav.push(new FragIntro(), delay);
        FragPolicyTerms frag = new FragPolicyTerms();
        if (options != null) {
            frag.setDesign(options.getDesign());
        }
        frag.setListener(new PolicyTermInter() {
            @Override
            public void onPolicy() {
                if (options != null && options.getCallBacks() != null) {
                    options.getCallBacks().onPolicyClick();
                }
            }

            @Override
            public void onTerms() {
                if (options != null && options.getCallBacks() != null) {
                    options.getCallBacks().onTermsAndConditionsClick();
                }
            }
        });
        btmNav.push(frag, delay);
    }
}
