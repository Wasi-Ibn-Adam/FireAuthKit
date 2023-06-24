package com.lassanit.authkit.fragments.profile;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lassanit.authkit.interfaces.account.PolicyTermInter;

public class FragProfile extends Fragment {
    protected PolicyTermInter imp;

    protected boolean active;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        imp = (PolicyTermInter) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}
