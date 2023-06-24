package com.lassanit.authkit.fragments.account;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.lassanit.authkit.R;
import com.lassanit.authkit.interfaces.account.PolicyTermInter;

public class FragPolicyTerms extends Frag {
    private PolicyTermInter imp;
    public FragPolicyTerms() {
        super(R.layout.frag_account_policy_terms);
    }

    public void setListener(PolicyTermInter imp) {
        this.imp = imp;
    }

    AppCompatButton btn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn = view.findViewById(R.id.btn_next);
        if (bres != -1)
            btn.setBackgroundResource(bres);
        view.findViewById(R.id.txt_policy).setOnClickListener(v -> {
            if (imp != null) imp.onPolicy();
        });
        view.findViewById(R.id.txt_terms).setOnClickListener(v -> {
            if (imp != null) imp.onTerms();
        });
        btn.setOnClickListener(view1 -> {
            if (imp != null) act.onPolicyTermsAccepted();
        });
    }
}
