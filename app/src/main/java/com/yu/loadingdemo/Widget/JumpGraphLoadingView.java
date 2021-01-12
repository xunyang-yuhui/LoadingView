package com.yu.loadingdemo.Widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.yu.loadingdemo.Interpolator.CyclerInterpolator;
import com.yu.loadingdemo.Interpolator.UCSpringInterpolator;
import com.yu.loadingdemo.Utils.ScreenUtils;

public class JumpGraphLoadingView  extends View {
    private static final int RADIUS = 20;           //图形半径，以圆形为基准，正方形为圆形外切正方形，三角形为内接等边三角形
    private static final int MAX_HEIGHT = 60;      //圆形运动最高点
    private static final int OFFSET_TEXT = 20;      //文字动画偏移
    private static final int SIZE_TEXT = 20;        //文字大小
    private Paint mPaint;
    private Path mTextPath;
    private Path mDetalPath;
    private int width;
    private int height;
    private String text;
    private float textWidth;
    private float textHeight;

    private boolean isClockWise;            //是否顺时针
    private int graphIndex = 0;             //图形标志
    private int animDegree;                 //角度
    private int graphY;                   //图形圆心Y坐标
    private int offsetControlY;             //控制点偏移
    private int pathY;                      //绘制文字所依赖的Path的坐标
    private int deltaX;

    private ValueAnimator textAnimator;     //文字动画
    private ValueAnimator grapahTransAnimator;      //图形位移动画
    private ValueAnimator graphRotateAnimator;      //图形旋转动画


    private UCSpringInterpolator ucSpringInterpolator;
    private CyclerInterpolator ucCyclerInterpolator;
    private LinearInterpolator linearInterpolator;

    public JumpGraphLoadingView(Context context) {
        this(context,null);
    }

    public JumpGraphLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public JumpGraphLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        text = "Please wait ...";
        init();
        initPath();
    }

    private void initPath() {
        mTextPath = new Path();

    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(ScreenUtils.sp2px(getContext(),SIZE_TEXT));
        mPaint.setStrokeWidth(3);
        mPaint.setTextAlign(Paint.Align.CENTER);

        textHeight = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
        textWidth = mPaint.measureText(text);

        ucSpringInterpolator = new UCSpringInterpolator(0.4f);
        ucCyclerInterpolator = new CyclerInterpolator(2);
        linearInterpolator = new LinearInterpolator();

        mDetalPath = new Path();

        deltaX = (int) Math.ceil(Math.sqrt(3));
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasureWidth(widthMeasureSpec),getMeasureHeight(heightMeasureSpec));
    }

    private int getMeasureWidth(int widthMeasureSpec) {
        int width = 0;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = (int)textWidth + getPaddingLeft() + getPaddingRight();
        }
        return width;
    }

    private int getMeasureHeight(int heightMeasureSpec) {
        int height = 0;
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = (int)textHeight + getPaddingTop() + getPaddingBottom() + MAX_HEIGHT + 2*RADIUS + OFFSET_TEXT;
        }
        return height;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        pathY = height - OFFSET_TEXT - getPaddingBottom();
        offsetControlY = pathY;

        startAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //绘制图形
        drawGraph(canvas);
        canvas.restore();

        drawPath(canvas);
        drawText(canvas);

    }

    //绘制Path
    private void drawPath(Canvas canvas) {
        mTextPath.reset();
        mTextPath.moveTo(0,pathY);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.STROKE);
        mTextPath.quadTo(width/2,offsetControlY,width,pathY);
        canvas.drawPath(mTextPath,mPaint);
    }
    //绘制文字
    private void drawText(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#80ffffff"));
        mPaint.setTextSize(ScreenUtils.sp2px(getContext(),SIZE_TEXT));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawTextOnPath(text,mTextPath,0,0,mPaint);
    }

    //绘制图形
    private void drawGraph(Canvas canvas) {
        canvas.translate(width/2,graphY);
        canvas.rotate(isClockWise?animDegree:-animDegree);
        if (graphIndex%3 == 0) {
            drawDelta(canvas);
        } else if(graphIndex%3 == 1) {
            drawRect(canvas);
        } else if(graphIndex%3 == 2) {
            drawCircle(canvas);
        }

    }

    private void drawRect(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#12aa9c"));
        canvas.drawRect(-RADIUS,-RADIUS,RADIUS,RADIUS,mPaint);
    }

    private void drawCircle(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#eea6b7"));
        canvas.drawCircle(0,0,RADIUS,mPaint);
    }

    private void drawDelta(Canvas canvas){
        mPaint.setColor(Color.parseColor("#425066"));
        mDetalPath.reset();
        mDetalPath.moveTo(0,-RADIUS);
        mDetalPath.lineTo(RADIUS/2* deltaX,RADIUS/2);
        mDetalPath.lineTo(-RADIUS/2* deltaX,RADIUS/2);
        mDetalPath.close();
        canvas.drawPath(mDetalPath,mPaint);
    }

    private void startAnim() {
        final int startY = pathY;
        //文字动画
        textAnimator = ValueAnimator.ofInt(startY,startY+OFFSET_TEXT,startY,startY-OFFSET_TEXT,startY);
        textAnimator.setInterpolator(ucSpringInterpolator);
        textAnimator.setDuration(2000);
        textAnimator.setRepeatCount(ValueAnimator.INFINITE);
        textAnimator.setRepeatMode(ValueAnimator.RESTART);
        textAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetControlY = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        textAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        textAnimator.start();

        //图形位移动画
        grapahTransAnimator = ValueAnimator.ofInt(startY - (int) textHeight,getPaddingTop() + RADIUS);
        grapahTransAnimator.setInterpolator(ucCyclerInterpolator);
        grapahTransAnimator.setDuration(2000);
        grapahTransAnimator.setRepeatCount(ValueAnimator.INFINITE);
        grapahTransAnimator.setRepeatMode(ValueAnimator.RESTART);
        grapahTransAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                graphY  = (int)animation.getAnimatedValue();
                invalidate();
            }
        });
        grapahTransAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                graphIndex = graphIndex+1;
            }
        });
        grapahTransAnimator.start();


        //图形旋转动画（改变canvas的角度）
        graphRotateAnimator = ValueAnimator.ofInt(0,360);
        graphRotateAnimator.setDuration(2000);
        graphRotateAnimator.setInterpolator(linearInterpolator);
        graphRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        graphRotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        graphRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animDegree = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        graphRotateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                isClockWise = !isClockWise;
            }
        });
        graphRotateAnimator.start();

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (textAnimator != null) {
            textAnimator.cancel();
            textAnimator = null;
        }

        if (grapahTransAnimator != null) {
            grapahTransAnimator.cancel();
            grapahTransAnimator = null;
        }

        if (graphRotateAnimator != null) {
            graphRotateAnimator.cancel();
            graphRotateAnimator = null;
        }
    }
}
