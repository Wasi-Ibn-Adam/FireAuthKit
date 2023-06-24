package com.lassanit.authkit.fragments.account;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lassanit.authkit.R;

public class FragSplash extends Frag {

    public FragSplash() {
        super(R.layout.frag_account_splash);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act.onSplashLoad();
    }

}
