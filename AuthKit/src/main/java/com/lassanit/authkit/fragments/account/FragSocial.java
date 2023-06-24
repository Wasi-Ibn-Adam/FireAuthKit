package com.lassanit.authkit.fragments.account;

import static com.lassanit.authkit.interactions.AuthKitOptions.Method;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.OAuthProvider;
import com.lassanit.authkit.R;
import com.lassanit.authkit.interactions.classes.Company;
import com.lassanit.authkit.interfaces.account.RegisterSocial;

import java.util.ArrayList;
import java.util.List;

public class FragSocial extends Fragment {
    RegisterSocial imp;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        imp = (RegisterSocial) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(requireContext()).inflate(R.layout.frag_account_social, container, false);
    }

    TextView txtCompany;
    ImageView imgCompany;
    ImageView phone, google, fb, yahoo, git, twitter, microsoft;
    private final Company company;
    private final Method[] list;

    public FragSocial(@Nullable Company company, @Nullable Method[] list) {
        this.company = company;
        this.list = list;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtCompany = view.findViewById(R.id.txt_company);
        imgCompany = view.findViewById(R.id.img_company);

        view.findViewById(R.id.layout_alter).setVisibility((list == null || list.length == 0) ? View.INVISIBLE : View.VISIBLE);

        phone = view.findViewById(R.id.btn_phone);
        google = view.findViewById(R.id.btn_google);
        fb = view.findViewById(R.id.btn_fb);
        yahoo = view.findViewById(R.id.btn_yahoo);
        git = view.findViewById(R.id.btn_git);
        twitter = view.findViewById(R.id.btn_twitter);
        microsoft = view.findViewById(R.id.btn_microsoft);
        phone.setOnClickListener(v -> {
            if (imp != null) imp.onPhoneClick();
        });
        google.setOnClickListener(v -> {
            if (imp != null) imp.onGoogleClick();
        });
        fb.setOnClickListener(v -> {
            if (imp != null) imp.onFacebookClick();
        });
        git.setOnClickListener(v -> {
            if (imp != null) {
                OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                List<String> scopes =
                        new ArrayList<String>() {
                            {
                                add("user:email");
                            }
                        };
                provider.setScopes(scopes);
                imp.loginWithProvider(provider.build());
            }
        });
        yahoo.setOnClickListener(v -> {
            if (imp != null) imp.loginWithProvider(OAuthProvider.newBuilder("yahoo.com").build());
        });
        twitter.setOnClickListener(v -> {
            if (imp != null) imp.loginWithProvider(OAuthProvider.newBuilder("twitter.com").build());
        });
        microsoft.setOnClickListener(v -> {
            if (imp != null)
                imp.loginWithProvider(OAuthProvider.newBuilder("microsoft.com").build());
        });

        setViewVisibility();
        setCompany();
    }

    void setViewVisibility() {
        if (phone != null) phone.setVisibility(View.GONE);
        if (microsoft != null) microsoft.setVisibility(View.GONE);
        if (twitter != null) twitter.setVisibility(View.GONE);
        if (git != null) git.setVisibility(View.GONE);
        if (google != null) google.setVisibility(View.GONE);
        if (yahoo != null) yahoo.setVisibility(View.GONE);
        if (fb != null) fb.setVisibility(View.GONE);
        if (list != null) {
            for (Method method : list) {
                switch (method) {
                    case MICROSOFT: {
                        if (microsoft != null) {
                            microsoft.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case TWITTER: {
                        if (twitter != null) {
                            twitter.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case GITHUB: {
                        if (git != null) {
                            git.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case PHONE: {
                        if (phone != null) {
                            phone.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case GOOGLE: {
                        if (google != null) {
                            google.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case YAHOO: {
                        if (yahoo != null) {
                            yahoo.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case FACEBOOK: {
                        if (fb != null) {
                            fb.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                }
            }
        }
    }

    void setCompany() {
        if (company != null) {
            txtCompany.setText(company.getName());
            imgCompany.setImageResource(company.getRes());
            requireView().findViewById(R.id.layout_powered).setVisibility(View.VISIBLE);
        }else{
            requireView().findViewById(R.id.layout_powered).setVisibility(View.GONE);
        }

    }

}
