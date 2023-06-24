package com.lassanit.authkit.fragments.account;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.lassanit.authkit.R;
import com.lassanit.authkit.interactions.AuthKitActivity;

import java.util.HashMap;

public class FragLogin extends Frag {
    private EditText txtEmail, txtPass;
    private AppCompatButton btn;

    private String e = "", p = "";


    public FragLogin() {
        super(R.layout.frag_account_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtEmail = view.findViewById(R.id.txt_email);
        txtPass = view.findViewById(R.id.txt_pass);
        btn = view.findViewById(R.id.btn_signin);

        if (bres != -1)
            btn.setBackgroundResource(bres);

        if (ires != -1) {
            txtEmail.setBackgroundResource(ires);
            txtPass.setBackgroundResource(ires);
        }

        txtEmail.addTextChangedListener(watcher(txtEmail, "Email"));
        txtPass.addTextChangedListener(watcher(txtPass, "Password"));

        btn.setOnClickListener(v -> {
            String email = txtEmail.getText().toString();
            String pass = txtPass.getText().toString();
            if (email.isEmpty()) {
                txtEmail.setError("Missing");
                return;
            } else if (!email.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                txtEmail.setError("Invalid");
                return;
            }
            if (pass.isEmpty()) {
                txtPass.setError("Missing");
                return;
            } else if (pass.length() < 8) {
                txtPass.setError("Invalid");
                return;
            }
            action(email, pass);
        });
        view.findViewById(R.id.txt_forget).setOnClickListener(v -> {
            if (act != null) act.onForgetClick();
        });
        view.findViewById(R.id.txt_reg).setOnClickListener(v -> {
            if (act != null) act.onRegisterClick();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (txtEmail != null) {
            e = txtEmail.getText().toString();
        }
        if (txtPass != null) {
            p = txtPass.getText().toString();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (txtEmail != null) {
            txtEmail.setText(e);
        }
        if (txtPass != null) {
            txtPass.setText(p);
        }

    }


    private void action(@NonNull String email, @NonNull String pass) {
        act.showLoadingWindow();
        AuthKitActivity.auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            act.onLoginComplete();
                        }
                    }
                    act.dismissLoadingWindow();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.d(TAG, "FirebaseAuthInvalidCredentialsException: " + e.getMessage());
                        txtPass.setError("Invalid!");
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        Log.d(TAG, "FirebaseAuthInvalidUserException: " + e.getMessage());
                        String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();
                        if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                            txtEmail.setError("Email not Registered.");
                        } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                            txtEmail.setError("User-Account has been disabled. For more information email us.");
                        }
                    }
                    e.printStackTrace();
                });
    }

    @Override
    public HashMap<String, View> getDefaultLinker() {
        HashMap<String, View> map = super.getDefaultLinker();
        map.put("email", txtEmail);
        map.put("pass", txtPass);
        map.put("btn", btn);
        return map;
    }
}
