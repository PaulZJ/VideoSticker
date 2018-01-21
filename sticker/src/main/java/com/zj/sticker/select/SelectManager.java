package com.zj.sticker.select;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjun on 2018/1/21.
 */

public class SelectManager {

    private FrameLayout mContainer;
    private RecyclerView mRecyclerView;
    private long mTotalDuration;
    private long mCurrDuration = 0;
    private int mThumbnailNum;
    private float mTimelineViewWidth;    //整个时间轴View的宽度（缩略图个数 * 单个缩略图的宽度）
    private float mTimelineViewDisplayWidth;    //整个时间轴View的可显示宽度（屏幕内有效显示宽度）

    private IPlayer mPlayer;
    private PlayerSeekListener playerSeekListener;
    private int mSingleViewWidth;
    private float mCurrScroll;
    private float mErrorDis;
    private boolean mIsTouching = false;
    private List<SelectHolderImp> selectHolderImpList = new ArrayList<>();
    private int mScrollState;

    public SelectManager(long totalDuration, int singleThumbPicWidth, IPlayer player, ViewGroup holderView) {
        mTotalDuration = totalDuration;
        mSingleViewWidth = singleThumbPicWidth;
        mPlayer = player;
        mContainer = (FrameLayout) holderView;
    }

    public long getCurrTimePoint() {
        return mCurrDuration;
    }

    public void setTimelineDisplayWidth(int width) {
        this.mTimelineViewDisplayWidth = width;
    }

    public void setRecycleView(RecyclerView recycleView) {
        mRecyclerView = recycleView;
        this.mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            private float mDownX = 0.0f;
            private long mDownTime = 0L;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int actionMasked = MotionEventCompat.getActionMasked(event);
                switch (actionMasked) {
                    case MotionEvent.ACTION_DOWN:
                        mIsTouching = true;
                        mDownTime = System.currentTimeMillis();
                        mDownX = event.getX();
                        mPlayer.pause();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isOutSideOfAllSelectView(event)) {
                            clearAllSelectState();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mIsTouching = false;
                        if (isOutSideOfAllSelectView(event)) {
                            clearAllSelectState();
                        }

                        if (clickSelectHolderImp != null) {
                            if ((System.currentTimeMillis() - mDownTime) < 100) {
                                clickSelectHolderImp.responseClickEvent();
                            }
                            clickSelectHolderImp = null;
                        }
                        break;
                }
                return false;
            }
        });

        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (null != playerSeekListener)
                            playerSeekListener.onSeekFinish(mCurrDuration);
                        for (SelectHolderImp holderImp : selectHolderImpList) {
                            holderImp.requestLayout();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                }
                mScrollState = newState;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrScroll += dx;
                float rate = mCurrScroll / getTimelineBarViewWidth();
                long duration = (long) (rate * mTotalDuration);
                if (playerSeekListener != null
                        && (mIsTouching ||
                        mScrollState == RecyclerView.SCROLL_STATE_SETTLING)) {
                    if (null != playerSeekListener)
                        playerSeekListener.onSeek(mCurrDuration);
                }
                mCurrDuration = duration;
                mPlayer.setCurrentTimePoint(mCurrDuration);
                for (int i=0;i<selectHolderImpList.size();i++) {
                    selectHolderImpList.get(i).requestLayout();
                }
                resetStickViewVisiableState();
            }
        });
    }

    public void scrollToTimePoint(long timePoint) {
        float ratio = timePoint*1.0f/mTotalDuration;
        scroll(ratio);
    }

    public void scroll(float ratio) {
        float scrollBy = ratio * getTimelineBarViewWidth() - mCurrScroll;
        if (mErrorDis >= 1) {
            scrollBy += 1;
            mErrorDis -= 1;
        }
        mErrorDis = scrollBy - (int) scrollBy;
        mRecyclerView.scrollBy((int) scrollBy, 0);
    }

    public void resetStickViewVisiableState() {
        for (SelectHolderImp tmp: selectHolderImpList) {
            tmp.setStickViewVisible(tmp.isCurrentTimePointFit(mCurrDuration));
        }
    }

    private float getTimelineBarViewWidth() {
        if (mTimelineViewWidth == 0) {
            this.mThumbnailNum = (mRecyclerView.getAdapter().getItemCount() - 2);
            this.mTimelineViewWidth = mThumbnailNum * mSingleViewWidth;
        }
        return mTimelineViewWidth;
    }

    public void addSelectView(View selectView, final CursorViewTouchImp tailTouchImp, final SelectHolderImp holderImp) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(selectView, params);
        final View view = tailTouchImp.getView();
        selectView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = calculateTailViewPosition(tailTouchImp);
                view.requestLayout();
                holderImp.setVisibility(true);
            }
        });
    }

    public void removeSelectView(View selectView, final SelectHolderImp holderImp) {
        removeView(selectView);
        if (selectHolderImpList.contains(holderImp)) {
            selectHolderImpList.remove(holderImp);
        }
    }

    public void removeView(@NonNull final View view) {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "alpha",  view.getAlpha(), 0f),
                ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0f),
                ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 0f)
        );
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation)  {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                mContainer.removeView(view);
            }
        });
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    public SelectHolderImp addSelectHolder(long startTime, long duration, ISelectView view, long minDuration) {
        if (startTime < 0) {
            startTime = 0;
        }
        SelectHolderImp selectHolderImp = new SelectHolderImp(this, startTime, duration, view,
                mTotalDuration, minDuration);
        selectHolderImpList.add(selectHolderImp);

        return selectHolderImp;
    }

    public void setPlayerSeekListener(PlayerSeekListener listener) {
        playerSeekListener = listener;
    }

    public long distance2Duration(float distance) {
        float ratio = distance / getTimelineBarViewWidth();
        return (long) (mTotalDuration * ratio);
    }
    public int duration2Distance(long duration) {
        float ratio = duration * 1.0f / mTotalDuration;
        return (int) (getTimelineBarViewWidth() * ratio);
    }

    public void seekTo(long timePoint) {
        float ratio = timePoint * 1.0f / mTotalDuration;
        if (playerSeekListener != null && !mIsTouching) {
            playerSeekListener.onSeek(timePoint);
        }
        scroll(ratio);
    }

    int calculateTailViewPosition(CursorViewTouchImp tailTouchImp) {
        return (int) (mTimelineViewDisplayWidth / 2 - tailTouchImp.getView().getMeasuredWidth()
                + duration2Distance(tailTouchImp.getTimePoint()) - mCurrScroll);
    }

    public void clearAllSelectState() {
        for (SelectHolderImp tmp: selectHolderImpList) {
            tmp.clearSelectState();
            tmp.switchState(SelectHolderImp.STATE_FIX);

        }
    }

    private SelectHolderImp clickSelectHolderImp;

    public void markClickSelectHolder(SelectHolderImp selectHolderImp) {
        clickSelectHolderImp = selectHolderImp;
    }

    public void bringSelectViewFront(ISelectView selectView) {
        mContainer.bringChildToFront(selectView.getContainer());
    }

    public boolean isOutSideOfAllSelectView(MotionEvent event) {
        for (SelectHolderImp tmp: selectHolderImpList) {
            if (tmp.isInsideOfMiddleView(Math.round(event.getX()), Math.round(event.getY()))) {
                return false;
            }
        }
        return true;
    }
}

