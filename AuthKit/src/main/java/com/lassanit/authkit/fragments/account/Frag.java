package com.lassanit.authkit.fragments.account;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.lassanit.authkit.R;
import com.lassanit.authkit.interactions.classes.AndroidApp;
import com.lassanit.authkit.interactions.classes.Design;
import com.lassanit.authkit.interfaces.account.Actions;

import java.util.HashMap;

public abstract class Frag extends Fragment {
    public static final String TAG = "sso";
    protected Actions act;
    protected int bres = -1, ires = -1;
    protected AndroidApp app;

    int res;
    AppCompatImageView imgCompany;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        act = (Actions) context;
    }

    public AppCompatImageView getImageView() {
        return imgCompany;
    }


    TextWatcher watcher(EditText txt, String hint) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() != 0) {
                    txt.setHint("");
                } else {
                    txt.setHint(hint);
                }
            }
        };
    }

    public Frag(int res) {
        this.res = res;
    }

    public static Transition getTransition(Context context, int rid) {
        return TransitionInflater.from(context).inflateTransition(rid);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set the shared element enter transition
        setSharedElementEnterTransition(getTransition(requireContext(), android.R.transition.move));
        setSharedElementReturnTransition(getTransition(requireContext(), android.R.transition.move));
        setEnterTransition(getTransition(requireContext(), android.R.transition.move));
        setExitTransition(getTransition(requireContext(), android.R.transition.fade));
        return inflater.inflate(res, container, false);
    }

    public HashMap<String, View> getDefaultLinker() {
        HashMap<String, View> map = new HashMap<>();
        map.put("logo", getImageView());
        return map;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            imgCompany = view.findViewById(R.id.img_logo);
            imgCompany.setImageResource(app.getRes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (act != null)
                act.onFragViewCreated(getClass().getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setApp(AndroidApp app) {
        this.app = app;
    }

    public void setDesign(Design design) {
        if (design == null)
            return;
        if (design.getInputRes() != -1)
            ires = design.getInputRes();
        if (design.getBtnRes() != -1)
            bres = design.getBtnRes();
    }

}
