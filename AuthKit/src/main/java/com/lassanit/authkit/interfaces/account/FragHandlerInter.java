package com.lassanit.authkit.interfaces.account;

import android.view.View;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface FragHandlerInter {
    void push(@NotNull Fragment frag);

    void push(@NotNull Fragment frag, @NotNull HashMap<String, View> map);

    void push(@NotNull Fragment frag, long delay);

    void push(@NotNull Fragment frag, @NotNull HashMap<String, View> map, long delay);

    void pop();

}
