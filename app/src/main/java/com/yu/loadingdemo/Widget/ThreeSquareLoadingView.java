package com.yu.loadingdemo.Widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.yu.loadingdemo.R;
import com.yu.loadingdemo.TypeEvaluator.PointFEvaluator;

public class ThreeSquareLoadingView extends View {
    private PointF LTPointF,RTPointF,LBPointF,RBPointF;
    private Paint mPaint;
    private ValueAnimator valueAnimator;
    private TypeEvaluator<PointF> typeEvaluator;
    private TypeValue typeValue = TypeValue.LT2RT;
    private RectF animRectF;
    private PointF animPointF = new PointF(200,200);
    private int radius;
    private int during;
    private int color;
    private int space;

    public ThreeSquareLoadingView(Context context) {
        this(context,null);
    }

    public ThreeSquareLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ThreeSquareLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ThreeSquareLoadingView);
        radius = array.getDimensionPixelSize(R.styleable.ThreeSquareLoadingView_ts_radius,10);
        color = array.getColor(R.styleable.ThreeSquareLoadingView_ts_color, Color.BLACK);
        space = array.getDimensionPixelSize(R.styleable.ThreeSquareLoadingView_ts_space,5);
        during = array.getInt(R.styleable.ThreeSquareLoadingView_ts_during,1000);

        init();

        array.recycle();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        typeEvaluator = new PointFEvaluator();
        LTPointF = new PointF(0,0);
        RTPointF = new PointF(0,0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getWidthMeasure(widthMeasureSpec),getHeightMeasure(heightMeasureSpec));
    }


    private int getHeightMeasure(int heightMeasure) {
        int height;
        int size = MeasureSpec.getSize(heightMeasure);
        int mode = MeasureSpec.getMode(heightMeasure);

        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = radius*2*2+space+getPaddingTop()+getPaddingBottom();
        }
        return height;
    }

    private int getWidthMeasure(int widthMeasure) {
        int width;
        int size = MeasureSpec.getSize(widthMeasure);
        int mode = MeasureSpec.getMode(widthMeasure);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = radius*2*2+space+getPaddingLeft()+getPaddingRight();
        }
        return width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAnimatorPart(canvas);

        switch (typeValue) {
            case LT2RT:
                drawImmobilityPart(LBPointF,RBPointF,canvas);
                break;
            case RT2RB:
                drawImmobilityPart(LTPointF,LBPointF,canvas);
                break;
            case RB2LB:
                drawImmobilityPart(LTPointF,RTPointF,canvas);
                break;
            case LB2LT:
                drawImmobilityPart(RBPointF,RTPointF,canvas);
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LTPointF = new PointF(w/2-space/2-radius,h/2-space/2-radius);
        RTPointF = new PointF(w/2+space/2+radius,h/2-space/2-radius);
        LBPointF = new PointF(w/2-space/2-radius,h/2+space/2+radius);
        RBPointF = new PointF(w/2+space/2+radius,h/2+space/2+radius);
        animRectF = new RectF(LTPointF.x-radius,LTPointF.y-radius,LTPointF.x+radius,LTPointF.y+radius);
        removeAll();
        startAnim(TypeValue.LT2RT,LTPointF,RTPointF);
    }

    //绘制需要进行动画的部分
    private void drawAnimatorPart(Canvas canvas) {
        animRectF.left = animPointF.x -radius;
        animRectF.top = animPointF.y -radius;
        animRectF.right = animPointF.x + radius;
        animRectF.bottom = animPointF.y + radius;
        canvas.drawRect(animRectF,mPaint);

    }

    //绘制静止部分
    private void drawImmobilityPart(PointF startPoint, PointF endPoint,Canvas canvas) {

        canvas.drawRect(startPoint.x-radius,startPoint.y-radius,startPoint.x+radius,startPoint.y+radius,mPaint);
        canvas.drawRect(endPoint.x-radius,endPoint.y-radius,endPoint.x+radius,endPoint.y+radius,mPaint);
    }

    private void startAnim(final TypeValue tv, PointF startPointF, PointF endPointF) {
        typeValue = tv;
        valueAnimator = ValueAnimator.ofObject(typeEvaluator,startPointF,endPointF);
        valueAnimator.setDuration(during);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animPointF = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (valueAnimator != null) {
                    valueAnimator.removeAllUpdateListeners();
                    valueAnimator = null;
                }
                if(tv == TypeValue.LT2RT) {
                    startAnim(TypeValue.LB2LT,LBPointF,LTPointF);
                } else if (tv == TypeValue.LB2LT) {
                    startAnim(TypeValue.RB2LB,RBPointF,LBPointF);
                } else if (tv == TypeValue.RB2LB) {
                    startAnim(TypeValue.RT2RB,RTPointF,RBPointF);
                } else {
                    startAnim(TypeValue.LT2RT,LTPointF,RTPointF);
                }
            }
        });
        valueAnimator.start();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAll();
    }

    public void removeAll() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator.removeAllUpdateListeners();
            valueAnimator = null;
        }
    }

    //定义四种类型
    enum TypeValue {
        LT2RT,RT2RB,RB2LB,LB2LT
    }
}

