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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.lassanit.authkit.R;
import com.lassanit.authkit.interactions.AuthKitActivity;

import java.util.Objects;

public class FragRegister extends Frag {
    private EditText txtEmail, txtPass, txtConPass;
    private String e = "", p = "", cp = "";


    public FragRegister() {
        super(R.layout.frag_account_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtEmail = view.findViewById(R.id.txt_email);
        txtPass = view.findViewById(R.id.txt_pass);
        txtConPass = view.findViewById(R.id.txt_pass_con);
        AppCompatButton btn = view.findViewById(R.id.btn_signup);


        if (ires != -1) {
            txtEmail.setBackgroundResource(ires);
            txtPass.setBackgroundResource(ires);
            txtConPass.setBackgroundResource(ires);
        }

        if (bres != -1)
            btn.setBackgroundResource(bres);
        btn.setOnClickListener(v -> {
            String email = txtEmail.getText().toString();
            String pass = txtPass.getText().toString();
            String cpass = txtConPass.getText().toString();
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
            if (cpass.isEmpty()) {
                txtConPass.setError("Missing");
                return;
            } else if (!cpass.equals(pass)) {
                txtConPass.setError("Not same as Above");
                return;
            }
            action(email, pass);
        });

        txtEmail.addTextChangedListener(watcher(txtEmail, "Email"));
        txtPass.addTextChangedListener(watcher(txtPass, "Password"));
        txtConPass.addTextChangedListener(watcher(txtConPass, "Confirm Password"));
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
        if (txtConPass != null) {
            cp = txtConPass.getText().toString();
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
        if (txtConPass != null) {
            txtConPass.setText(cp);
        }

    }

    private void action(@NonNull String email, @NonNull String pass) {
        act.showLoadingWindow();
        AuthKitActivity.auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                    Log.d(TAG, "createUserWithEmailAndPassword: SUCCESS.");
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            act.onRegisterComplete();
                        }
                    }
                    act.dismissLoadingWindow();
                })
                .addOnFailureListener(e -> {
                    try {
                        throw Objects.requireNonNull(e);
                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                        Log.d(TAG, "createUserWithEmailAndPassword: ERROR: FirebaseAuthWeakPasswordException");
                        txtPass.setError("Weak Password.");
                    } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                        Log.d(TAG, "createUserWithEmailAndPassword: ERROR: FirebaseAuthInvalidCredentialsException");
                        txtEmail.setError("Mail-formed Email.");
                    } catch (FirebaseAuthUserCollisionException existEmail) {
                        Log.d(TAG, "createUserWithEmailAndPassword: ERROR: FirebaseAuthUserCollisionException");
                        txtEmail.setError("Email already Exist.");
                    } catch (Exception e1) {
                        txtEmail.setError("You Cant Register with this Email.");
                        Log.d(TAG, "createUserWithEmailAndPassword: ERROR: " + e1.getMessage());
                    }

                });
    }

}
