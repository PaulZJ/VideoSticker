package com.zj.textpath;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.zj.sticker.R;

/**
 * Created by zhangjun on 2018/5/5.
 */

public abstract class PathView extends View {
    public static final int NONE = 0;
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    @IntDef({NONE, RESTART, REVERSE})
    public @interface Repeat{}
    @Repeat
    protected int mRepeatStyle = NONE;

    protected Paint mDrawPaint;
    protected Paint mPaint;
    protected Path mDst = new Path(), mPaintPath = new Path();
    protected ValueAnimator mAnimator;
    protected float mAnimatorValue = 0;

    protected float mStop = 0;
    protected boolean showPainter = true, showPainterActually = false;
    protected float[] mCurPos = new float[2];
    protected float mPathWidth = 0, mPathHeight = 0;
    protected int mDuration = 6000;

    protected PathMeasure mPathMeasure = new PathMeasure();
    protected Path mPath;
    protected int mPathStrokeWidth = 5, mPaintStrokeWidth = 3;
    protected int mTextStrokeColor = Color.BLACK, mPaintStrokeColor = Color.BLACK;

    protected boolean mShouldFill = false;
    protected PathAnimatorListener mAnimatorListener;
    protected boolean nullPath = true;

    public PathView(Context context) {
        super(context);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    protected void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PathView);
        mDuration = typedArray.getInteger(R.styleable.PathView_duration, mDuration);
        showPainter = typedArray.getBoolean(R.styleable.PathView_showPainter, showPainter);
        showPainterActually = typedArray.getBoolean(R.styleable.PathView_showPainterActually, showPainterActually);
        mPathStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.PathView_pathStrokeWidth, mPathStrokeWidth);
        mTextStrokeColor = typedArray.getColor(R.styleable.PathView_pathStrokeColor, mTextStrokeColor);
        mPaintStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.PathView_paintStrokeWidth,
                mPaintStrokeWidth);
        mPaintStrokeColor = typedArray.getColor(R.styleable.PathView_paintStrokeColor, mPaintStrokeColor);
        mRepeatStyle = typedArray.getInt(R.styleable.PathView_repeat, mRepeatStyle);
        typedArray.recycle();
    }

    protected void initPaint() {
        mDrawPaint = new Paint();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setColor(mTextStrokeColor);
        mDrawPaint.setStrokeWidth(mPaintStrokeWidth);
        mDrawPaint.setStyle(Paint.Style.STROKE);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mPaintStrokeColor);
        mPaint.setStrokeWidth(mPaintStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    protected void initAnimator(float start, float end, int animationStyle, int repeatCount) {
        mAnimator = ValueAnimator.ofFloat(start, end);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                drawPath(mAnimatorValue);
            }
        });
        if (null == mAnimatorListener) {
            mAnimatorListener = new PathAnimatorListener();
            mAnimatorListener.setTarget(this);
        }
        mAnimator.removeAllListeners();
        mAnimator.addListener(mAnimatorListener);

        mAnimator.setDuration(mDuration);
        mAnimator.setInterpolator(new LinearInterpolator());
        if (animationStyle == RESTART) {
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setRepeatCount(repeatCount);
        } else if (animationStyle == REVERSE) {
            mAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mAnimator.setRepeatCount(repeatCount);
        }
    }

    public void startAnimation(float start, float end) {
        startAnimation(start, end, mRepeatStyle, ValueAnimator.INFINITE);
    }

    public void startAnimation(float start, float end, int animationStyle, int repeatCount) {
        if (!isProgressValid(start) || !isProgressValid(end))
            return;

        if (mAnimator != null)
            mAnimator.cancel();

        initAnimator(start, end, animationStyle, repeatCount);
        showPainterActually = showPainter;
        mAnimator.start();
    }

    public void stopAnimation(){
        showPainterActually = false;
        clear();
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    protected boolean isProgressValid(float progress) {
        if (progress <0 || progress > 1) {
            try {
                throw new Exception("Progress is invalid");
            }catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        mAnimatorValue = 0;
        mDst.reset();
        mPaintPath.reset();
        postInvalidate();
    }

    public abstract void drawPath(float progress);
}
