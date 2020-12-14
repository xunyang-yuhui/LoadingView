package com.yu.loadingdemo.TypeEvaluator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * 自定义的估值器，获取动画中的Point
 */
public class PointFEvaluator implements TypeEvaluator<PointF> {
    PointF pointf = new PointF();
    @Override
    public PointF evaluate(float fraction, PointF startPointF, PointF endPointF) {
        float x = startPointF.x+(endPointF.x - startPointF.x)*fraction;
        float y = startPointF.y + (endPointF.y - startPointF.y)*fraction;
        pointf.set(x,y);
        return pointf;
    }
}
