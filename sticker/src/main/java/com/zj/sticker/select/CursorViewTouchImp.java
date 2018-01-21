package com.zj.sticker.select;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhangjun on 2018/1/21.
 */

public class CursorViewTouchImp implements View.OnTouchListener {
    private View mView;             //对应的View
    private long mTimePoint;         //所处的时长
    private OnPositionListener mPositionListener;
    private float mStartX;

    public CursorViewTouchImp(View view, long timePoint) {
        mView = view;
        mView.setOnTouchListener(this);
        mTimePoint = timePoint;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMasked = MotionEventCompat.getActionMasked(event);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - mStartX;
                mStartX = event.getRawX();
                if(mPositionListener != null) {
                    mPositionListener.onPositionChanged(dx);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mPositionListener != null) {
                    mPositionListener.onPositionComplete();
                }
                mStartX = 0;
                break;
            default:
                mStartX = 0;
        }

        return true;
    }

    public void setPositionListener(OnPositionListener positionListener) {
        mPositionListener = positionListener;
    }

    public void active() {
        mView.setVisibility(View.VISIBLE);
    }

    public void fix() {
        mView.setVisibility(View.INVISIBLE);
    }

    public void changeDuration(long duration) {
        mTimePoint += duration;
    }

    public long getTimePoint() {
        return mTimePoint;
    }

    public View getView() {
        return mView;
    }

    public interface OnPositionListener {
        void onPositionChanged(float dx);
        void onPositionComplete();
    }
}
