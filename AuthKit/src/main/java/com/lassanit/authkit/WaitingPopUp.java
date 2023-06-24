package com.lassanit.authkit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

public class WaitingPopUp extends PopupWindow {
    View view;

    public WaitingPopUp(Context context) {
        super(context);
        init(context, "");
    }

    public WaitingPopUp(Context context, String text) {
        super(context);
        init(context, text);
    }

    public WaitingPopUp(Context context, @NotNull Runnable runnable) {
        super(context);
        init(context, "");
        new Handler(context.getMainLooper()).post(runnable);
    }

    private void init(@NonNull Context context, String text) {
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_waiting_popup, null);

        setAnimationStyle(android.R.anim.cycle_interpolator);
        setContentView(view);
        setFocusable(false);
        setOutsideTouchable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //Glide.with(context).load(new ColorDrawable(Color.LTGRAY)).into((CircleImageView) view.findViewById(R.id.videoView));
        ImageView img = view.findViewById(R.id.videoView);
        Glide.with(context).asGif().load(R.drawable.loader2).placeholder(new ColorDrawable(Color.LTGRAY)).into(img);
        ((TextView) getContentView().findViewById(R.id.loading_txt)).setText(text);
    }

    public void show() {
        try {
            showAtLocation(view, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDismiss(@NonNull Runnable runnable) {
        setOnDismissListener(runnable::run);
    }

}
