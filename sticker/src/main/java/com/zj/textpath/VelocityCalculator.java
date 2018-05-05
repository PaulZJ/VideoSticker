package com.zj.textpath;

import lombok.Getter;

/**
 * Created by zhangjun on 2018/5/5.
 */

public class VelocityCalculator {
    private float mLastX = 0;
    private float mLastY = 0;
    private long mLastTime = 0;
    private boolean first = true;

    @Getter
    private float velocityX = 0;
    @Getter
    private float velocityY = 0;

    public void reset() {
        mLastX = 0;
        mLastY = 0;
        mLastTime = 0;
        first = true;
    }

    public void calculate(float x, float y) {
        long time = System.currentTimeMillis();
        if (!first) {
            velocityX = x - mLastX;
            velocityY = y - mLastY;
        }else {
            first = false;
        }

        mLastX = x;
        mLastY = y;
        mLastTime = time;
    }

}
