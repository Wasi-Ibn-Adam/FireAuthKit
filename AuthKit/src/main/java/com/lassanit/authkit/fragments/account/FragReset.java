package com.lassanit.authkit.fragments.account;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.lassanit.authkit.R;
import com.lassanit.authkit.interactions.AuthKitActivity;

public class FragReset extends Frag {
    private EditText txtEmail;
    private String e = "";
    public FragReset() {
        super(R.layout.frag_account_reset);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtEmail = view.findViewById(R.id.txt_email);
        AppCompatImageView btnReset = view.findViewById(R.id.btn_reset);

        if (bres != -1)
            btnReset.setBackgroundResource(bres);
        if (ires != -1) {
            txtEmail.setBackgroundResource(ires);
        }
        btnReset.setOnClickListener(v -> {
            String email = txtEmail.getText().toString();
            if (email.isEmpty()) {
                txtEmail.setError("Missing");
                return;
            } else if (!email.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                txtEmail.setError("Invalid");
                return;
            }
            action(email);
        });
        txtEmail.addTextChangedListener(watcher(txtEmail, "Email"));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (txtEmail != null) {
            e = txtEmail.getText().toString();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (txtEmail != null) {
            txtEmail.setText(e);
        }
    }

    void action(String email) {
        act.showLoadingWindow();
        AuthKitActivity.auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "sendPasswordResetEmail: SUCCESS.");
                act.onResetLinkSent();
            }
            act.dismissLoadingWindow();
        }).addOnFailureListener(e -> {
            Log.d(TAG, "sendPasswordResetEmail: ERROR: " + e.getMessage());
            if (e.getLocalizedMessage() != null && e.getLocalizedMessage().equalsIgnoreCase("There is no user record corresponding to this identifier. The user may have been deleted."))
                txtEmail.setError("Email not registered.");
            e.printStackTrace();
        });
    }


}
