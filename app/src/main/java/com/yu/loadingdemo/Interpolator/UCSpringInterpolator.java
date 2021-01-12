package com.yu.loadingdemo.Interpolator;

import android.view.animation.Interpolator;

public class UCSpringInterpolator implements Interpolator {
    private float factor;

    public UCSpringInterpolator(float factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {

        return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
    }

}
