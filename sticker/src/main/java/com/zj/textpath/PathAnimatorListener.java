package com.zj.textpath;

import android.animation.Animator;

/**
 * Created by zhangjun on 2018/5/5.
 */

public class PathAnimatorListener implements Animator.AnimatorListener {
    private PathView mPathView;
    protected boolean isCancle = false;

    protected void setTarget(PathView pathView) {
        this.mPathView = pathView;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        isCancle = false;
    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {
        isCancle = true;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
