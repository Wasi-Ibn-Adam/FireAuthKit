package com.lassanit.authkit.fragments.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lassanit.authkit.R;
import com.lassanit.authkit.interactions.AuthKitActivity;
import com.lassanit.authkit.phone.PhoneEditText;
import com.raycoarana.codeinputview.CodeInputView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class FragPhone extends Frag {
    private PhoneEditText txtPhone;
    private TextView txtTimer, txtResend;
    private CodeInputView txtCode;
    private AppCompatButton btnPhone;

    private long time = 60L;
    private Timer timer;
    private TimerTask task;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String phoneNumber, verificationId;

    public FragPhone() {
        super(R.layout.frag_account_phone);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtPhone = view.findViewById(R.id.txt_phone);
        btnPhone = view.findViewById(R.id.btn_phone);

        txtCode = view.findViewById(R.id.txt_code);
        txtResend = view.findViewById(R.id.txt_resend);
        txtTimer = view.findViewById(R.id.txt_timer);

        if (bres != -1)
            btnPhone.setBackgroundResource(bres);
        if (ires != -1) {
            txtPhone.setBackgroundResource(ires);
            txtCode.setBackgroundResource(ires);
        }
        txtCode.setVisibility(View.GONE);
        txtResend.setVisibility(View.GONE);
        txtTimer.setVisibility(View.GONE);

        btnPhone.setOnClickListener(v -> {
            if (!txtPhone.isValid()) {
                txtPhone.setError("Enter Valid Phone Number");
                return;
            }
            String num = txtPhone.getPhoneNumber();
            action(num);
            hideSoftKeyboard(requireActivity());
        });
        txtCode.addOnCompleteListener(code -> {
            onPhoneCodeEntered(code);
            txtTimer.setVisibility(View.GONE);
            txtResend.setVisibility(View.GONE);
        });
        txtResend.setOnClickListener(view1 -> {
            onPhoneCodeResent();
        });
    }

    void action(String num) {
        act.showLoadingWindow();
        act.stopBackPresses();
        phoneNumber = num;
        timer = new Timer();
        task = new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtTimer.setText(time + " sec");
                    }
                });

                time--;
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(AuthKitActivity.auth)
                        .setPhoneNumber(num)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public static void hideSoftKeyboard(@NonNull Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }


    private void onPhoneCodeResent() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(AuthKitActivity.auth)
                        .setForceResendingToken(resendToken)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void onPhoneCodeEntered(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "Phone onVerificationCompleted.");
            txtCode.setCode(phoneAuthCredential.getSmsCode());
            signInWithPhoneAuthCredential(phoneAuthCredential);
            act.allowBackPresses();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            act.dismissLoadingWindow();
            act.allowBackPresses();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Log.d(TAG, "Phone onVerificationFailed: FirebaseAuthInvalidCredentialsException.");
                // Invalid request
                txtPhone.setError("Invalid Phone number.");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.d(TAG, "Phone onVerificationFailed: FirebaseTooManyRequestsException.");
                // The SMS quota for the project has been exceeded
                txtPhone.setError("We have blocked all requests from this device due to unusual activity. Try again later.");
            } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                Log.d(TAG, "Phone onVerificationFailed: FirebaseAuthMissingActivityForRecaptchaException.");
                txtPhone.setError("Try again.");
            } else {
                Log.d(TAG, "Phone onVerificationFailed: ERROR: " + e.getMessage());
            }
        }

        @Override
        public void onCodeSent(@NonNull String verifyId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "Phone onCodeSent.");
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            act.dismissLoadingWindow();
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later

            verificationId = verifyId;
            resendToken = token;
            txtPhone.setVisibility(View.GONE);
            btnPhone.setVisibility(View.GONE);

            txtCode.setVisibility(View.VISIBLE);
            txtTimer.setVisibility(View.VISIBLE);

            txtCode.requestFocus();
            txtCode.setEditable(true);
            timer.schedule(task, 0, 1000);

        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            Log.d(TAG, "Phone onCodeAutoRetrievalTimeOut.");
            txtTimer.setVisibility(View.GONE);
            txtResend.setVisibility(View.VISIBLE);
            act.allowBackPresses();
        }

    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        AuthKitActivity.auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "Phone signInWithCredential: SUCCESS.");
                    timer.cancel();
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        Log.d(TAG, "Phone signInWithCredential: SUCCESS USER.");
                        act.checkUser(authResult.getAdditionalUserInfo());

                    } else
                        Log.d(TAG, "Phone signInWithCredential: SUCCESS NULL.");
                    act.onPhoneSignInDone();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.d(TAG, "Phone signInWithCredential: FirebaseAuthInvalidCredentialsException.");
                        txtCode.setError("Invalid Code");
                        txtCode.setEditable(true);
                        txtCode.setVisibility(View.VISIBLE);
                        txtTimer.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(TAG, "Phone signInWithCredential: ERROR.");
                    }
                });
    }
}
