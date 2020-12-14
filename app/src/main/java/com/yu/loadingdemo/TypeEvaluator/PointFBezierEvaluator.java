package com.yu.loadingdemo.TypeEvaluator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class PointFBezierEvaluator implements TypeEvaluator {
    private PointF mPointF = new PointF(0,0);
    private PointF controlPoint;
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        PointF startPoint = (PointF)startValue;
        PointF endPoint = (PointF) endValue;

        mPointF.x = (float) Math.pow((double) (1-fraction),2)*startPoint.x + 2*fraction*(1-fraction)*controlPoint.x+(float) Math.pow((double) fraction,2)*endPoint.x;
        mPointF.y = (float) Math.pow((double) (1-fraction),2)*startPoint.y + 2*fraction*(1-fraction)*controlPoint.y+(float) Math.pow((double) fraction,2)*endPoint.y;

        return mPointF;
    }

    public void setControlPoint(PointF controlPoint) {
        this.controlPoint = controlPoint;
    }
}
