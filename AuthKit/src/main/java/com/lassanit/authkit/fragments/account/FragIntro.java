package com.lassanit.authkit.fragments.account;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.lassanit.authkit.R;

public class FragIntro extends Frag {

    AppCompatTextView txt;

    public FragIntro() {
        super(R.layout.frag_account_intro);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt = view.findViewById(R.id.txt_welcome);
        if (app == null || app.getName() == null || app.getName().isEmpty()) {
            txt.setVisibility(View.INVISIBLE);
        } else {
            txt.setVisibility(View.VISIBLE);
            txt.setText("Welcome to " + app.getName());
        }
    }
}
