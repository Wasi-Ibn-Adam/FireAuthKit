package com.lassanit.authkit.interactions;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.lassanit.authkit.WaitingPopUp;
import com.lassanit.authkit.fragments.FragNavHandler;
import com.lassanit.authkit.interfaces.account.RegisterSocial;
import com.lassanit.authkit.interfaces.account.Result;

public abstract class AuthKitActivity extends AppCompatActivity implements RegisterSocial, Result {
    protected AuthKitOptions options;
    protected String TAG = "";
    public static FirebaseAuth auth;
    protected SignInClient oneTapClient;
    protected BeginSignInRequest request;
    protected ActivityResultLauncher<IntentSenderRequest> launcher;
    protected FragNavHandler topNav, btmNav;
    protected WaitingPopUp popUp;
    boolean backPress = true;
    public static final String POLICY_TERMS_CONDITIONS = "policy_and terms";
    protected boolean first = false;

}

