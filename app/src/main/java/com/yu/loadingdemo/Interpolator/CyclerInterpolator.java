package com.yu.loadingdemo.Interpolator;

import android.view.animation.Interpolator;

public class CyclerInterpolator implements Interpolator {
    private int time;

    public CyclerInterpolator(int time) {
        this.time = time;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.sin(1 * input * Math.PI));
    }
}
