package com.yu.loadingdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.fragment.app.DialogFragment;

import com.yu.loadingdemo.Widget.ThreeBallLoadingView;
import com.yu.loadingdemo.Widget.ThreeSquareLoadingView;

public class LoadingDialogFragment extends DialogFragment {
    private Type type;
    private ThreeSquareLoadingView loading_ts;
    private ThreeBallLoadingView loading_tb;

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;

        window.setAttributes(windowParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        View view = inflater.inflate(R.layout.view_dialog, container);
        loading_ts = (view).findViewById(R.id.loading_ts);
        loading_tb = (view).findViewById(R.id.loading_tb);
        switch (type) {
            case THREEBALL:
                loading_tb.setVisibility(View.VISIBLE);
                break;
            case THREESQUARE:
                loading_ts.setVisibility(View.VISIBLE);
                break;
        }
        return view;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        THREESQUARE, THREEBALL
    }
}
