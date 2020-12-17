package com.yu.loadingdemo.Widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.yu.loadingdemo.R;
import com.yu.loadingdemo.TypeEvaluator.PointFBezierEvaluator;

public class ThreeBallLoadingView extends View {
    private Paint mPaintF;
    private Paint mPaintS;
    private Paint mPaintT;
    private int tb_radius;
    private int tb_space;
    private int tb_during;
    private int tb_color_first;
    private int tb_color_second;
    private int tb_color_third;
    private int tb_height;
    private int width, height;
    private PointObject firstPointObj,secondPointObj,thirdPointObj;
    private PointF firstPoint,thirdPoint;
    private final PointF secondPoint = new PointF(0,0);
    private PointF secondTopControl;
    private PointF animLeftPointF, animRightPointF;
    private ValueAnimator animator;
    private PointFBezierEvaluator evaluator;
    private boolean animTag = true;     //用来确定firstPoint 和 secondPoint
    public ThreeBallLoadingView(Context context) {
        this(context,null);
    }

    public ThreeBallLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ThreeBallLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ThreeBallLoadingView);
        tb_radius = array.getDimensionPixelSize(R.styleable.ThreeBallLoadingView_tb_radius,5);
        tb_space = array.getDimensionPixelSize(R.styleable.ThreeBallLoadingView_tb_space,5);
        tb_height = array.getDimensionPixelSize(R.styleable.ThreeBallLoadingView_tb_height,tb_radius+tb_space/2);
        tb_during = array.getInt(R.styleable.ThreeBallLoadingView_tb_during,1000);
        tb_color_first = array.getColor(R.styleable.ThreeBallLoadingView_tb_color_first, Color.RED);
        tb_color_second = array.getColor(R.styleable.ThreeBallLoadingView_tb_color_second, Color.GREEN);
        tb_color_third = array.getColor(R.styleable.ThreeBallLoadingView_tb_color_third, Color.BLUE);

        init();

        array.recycle();
    }

    private void init() {
        mPaintF = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintF.setStyle(Paint.Style.FILL);
        mPaintF.setAntiAlias(true);
        mPaintF.setColor(tb_color_first);

        mPaintS = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintS.setStyle(Paint.Style.FILL);
        mPaintS.setAntiAlias(true);
        mPaintS.setColor(tb_color_second);

        mPaintT = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintT.setStyle(Paint.Style.FILL);
        mPaintT.setAntiAlias(true);
        mPaintT.setColor(tb_color_third);

        evaluator = new PointFBezierEvaluator();
        initPoint();
    }

    private void initPoint() {
        firstPoint = new PointF(-(tb_radius*2+tb_space),0);
        thirdPoint = new PointF(tb_radius*2+tb_space,0);
        secondTopControl = new PointF(tb_radius+(float)tb_space/2,tb_height);

        PointF pointFF = new PointF(firstPoint.x,firstPoint.y);
        PointF pointFS = new PointF(secondPoint.x,secondPoint.y);
        PointF pointFT = new PointF(thirdPoint.x, thirdPoint.y);
        firstPointObj = new PointObject(pointFF,mPaintF);
        secondPointObj = new PointObject(pointFS,mPaintS);
        thirdPointObj = new PointObject(pointFT,mPaintT);

        animLeftPointF = firstPoint;
        animRightPointF = thirdPoint;

        startAnim(firstPointObj,secondPointObj,secondTopControl);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getWidthMeasure(widthMeasureSpec),getHeightMeasure(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private int getHeightMeasure(int heightMeasure) {
        int height;
        int size = MeasureSpec.getSize(heightMeasure);
        int mode = MeasureSpec.getMode(heightMeasure);

        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            //tb_radius+tb_height : 一侧所需要的的高度
            height = 2*(tb_radius+tb_height)+getPaddingTop()+getPaddingBottom();
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
            width = tb_radius*2*3+tb_space*2+getPaddingLeft()+getPaddingRight();
        }
        return width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(width/2,height/2);

        canvas.drawCircle(firstPointObj.pointF.x , firstPointObj.pointF.y ,tb_radius,firstPointObj.paint);
        canvas.drawCircle(secondPointObj.pointF.x,secondPointObj.pointF.y,tb_radius,secondPointObj.paint);
        canvas.drawCircle(thirdPointObj.pointF.x,thirdPointObj.pointF.y,tb_radius,thirdPointObj.paint);
        canvas.restore();
    }

    /**
     *动画 animTag is true   :  -xxx -> 0
     *     animTag is false  :  xxx  -> 0
     */
    private void startAnim(final PointObject firstObj,final PointObject secondObj, PointF controlPointF) {
        if (animator != null) {
            animator.removeAllUpdateListeners();
            animator = null;
        }

        evaluator.setControlPoint(controlPointF);
        //此处传入 估值器的起始值 ：左右动画其实是一样的，只是坐标一个正数一个负数
        animator = ValueAnimator.ofObject(evaluator,thirdPoint,secondPoint);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(tb_during);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (animTag) {
                    animLeftPointF= (PointF) valueAnimator.getAnimatedValue();
                    firstObj.getPointF().x = -animLeftPointF.x;
                    firstObj.getPointF().y = -animLeftPointF.y;
                    secondObj.getPointF().x = secondPoint.x+(firstPoint.x+animLeftPointF.x);
                    secondObj.getPointF().y = animLeftPointF.y;
                } else {
                    animRightPointF = (PointF)valueAnimator.getAnimatedValue();
                    firstObj.getPointF().x = animRightPointF.x;
                    firstObj.getPointF().y = -animRightPointF.y;
                    secondObj.getPointF().x = secondPoint.x+(thirdPoint.x - animRightPointF.x);
                    secondObj.getPointF().y = animRightPointF.y;
                }

                invalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animTag) {
                    resetPointObject(firstPointObj,secondPointObj);
                    animTag = !animTag;
                    startAnim(thirdPointObj,secondPointObj,secondTopControl);
                } else {
                    resetPointObject(thirdPointObj,secondPointObj);
                    animTag = !animTag;
                    startAnim(firstPointObj,secondPointObj,secondTopControl);
                }
            }
        });

        animator.start();
    }

    //一个动画结束时，交换执行动画的两个Point的内容（color），重新给坐标点附初始值
    private void resetPointObject(PointObject firstPointObj, PointObject secondPointObj) {
        int color = firstPointObj.getPaint().getColor();
        firstPointObj.getPaint().setColor(secondPointObj.getPaint().getColor());
        if (animTag) {
            firstPointObj.getPointF().x = firstPoint.x;
            firstPointObj.getPointF().y = firstPoint.y;
        } else {
            firstPointObj.getPointF().x = thirdPoint.x;
            firstPointObj.getPointF().y = thirdPoint.y;
        }
        secondPointObj.getPaint().setColor(color);
        secondPointObj.getPointF().x = 0;
        secondPointObj.getPointF().y = 0;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.removeAllUpdateListeners();
            animator = null;
        }
    }

    private class PointObject {
        private PointF pointF;
        private Paint paint;

        public PointObject(PointF pointF, Paint paint) {
            this.pointF = pointF;
            this.paint = paint;
        }

        public PointF getPointF() {
            return pointF;
        }

        public Paint getPaint() {
            return paint;
        }
    }
}
