package com.lassanit.authkit.fragments;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lassanit.authkit.fragments.account.Frag;
import com.lassanit.authkit.interactions.classes.AndroidApp;
import com.lassanit.authkit.interfaces.account.FragHandlerInter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class FragNavHandler implements FragHandlerInter {
    private final FragmentManager manager;
    private FragmentChangeListener listener;
    private final Context context;
    private final int res;
    String stack = "STACK_";
    ArrayList<Integer> list = new ArrayList<>();
    private int rIn, rOut;
    private int pIn, pOut;
    private int hIn, hOut;

    private AndroidApp app;


    public FragNavHandler(@NonNull Context context, @NonNull FragmentManager manager, int res, int st, AndroidApp app) {
        this.context = context;
        this.manager = manager;
        this.res = res;
        this.app = app;
        stack += st;


        manager.addFragmentOnAttachListener((fragmentManager, frag) -> {
            if (listener != null)
                listener.onChange(frag.getClass().getSimpleName(), true);
        });
        manager.addOnBackStackChangedListener(() -> {
            if (listener == null)
                return;
            try {
                Fragment frag = manager.getFragments().get(manager.getFragments().size() - 1);
                if (frag != null)
                    listener.onChange(frag.getClass().getSimpleName(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Nullable
    public Fragment getCurr() {
        return manager.findFragmentById(list.get(list.size() - 1));
    }

    @Nullable
    public Fragment getFragment(Class _class) {
        return manager.getFragments().stream()
                .filter(fragment -> fragment != null && fragment.getClass().equals(_class))
                .findFirst()
                .orElse(null);
    }


    private void load(@NotNull Fragment frag, @Nullable HashMap<String, View> map) {
        try {
            ((Frag) frag).setApp(app);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FragmentTransaction transaction = manager.beginTransaction().setCustomAnimations(rIn, rOut, pIn, pOut);
            if (map != null)
                for (String key : map.keySet()) {
                    View v = map.get(key);
                    if (v == null)
                        continue;
                    transaction.addSharedElement(v, key);
                }
            int i = transaction.replace(res, frag).addToBackStack(stack).commit();
            list.add(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void push(@NotNull Fragment frag) {
        load(frag, null);
    }


    @Override
    public void push(@NotNull Fragment frag, long delay) {
        new Handler().postDelayed(() -> load(frag, null), delay);
    }

    @Override
    public void push(@NonNull Fragment frag, @NonNull HashMap<String, View> map) {
        load(frag, map);
    }

    @Override
    public void push(@NonNull Fragment frag, @NotNull HashMap<String, View> map, long delay) {
        new Handler().postDelayed(() -> load(frag, map), delay);
    }

    public void hide(Class _class) {
        try {
            int id = list.get(list.size() - 1);
            Log.d("sso", "hideLast " + id);
            Fragment frag = getFragment(_class);
            if (frag != null && frag.isAdded() && frag.isVisible())
                manager.beginTransaction()
                        .setCustomAnimations(hIn, hOut).hide(frag).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(Class _class) {
        try {
            Fragment frag = getFragment(_class);
            if (frag != null && frag.isAdded() && frag.isHidden())
                manager.beginTransaction()
                        .setCustomAnimations(hIn, hOut)
                        .show(frag)
                        .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pop() {
        try {
            int id = list.remove(list.size() - 1);
            manager.popBackStack(id, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void popPrev() {
        try {
            int id = list.remove(list.size() - 2);
            if (id >= 0)
                manager.popBackStack(id, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean handleBackPress() {
        Log.d("sso", "handleBackPress " + list.toString());
        if (list.size() <= 2)
            return false;
        pop();
        return true;
    }


    public void addFragmentChangeListener(FragmentChangeListener listener) {
        this.listener = listener;
    }

    public void setAnim(int in, int out) {
        rIn = in;
        rOut = out;
    }

    public void setPopAnim(int in, int out) {
        pIn = in;
        pOut = out;
    }

    public void setHideAnime(int in, int out) {
        hIn = in;
        hOut = out;
    }


    public interface FragmentChangeListener {
        void onChange(String name, boolean attached);
    }
}
