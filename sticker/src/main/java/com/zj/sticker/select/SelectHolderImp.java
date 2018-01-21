package com.zj.sticker.select;

import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zj.sticker.R;

/**
 * Created by zhangjun on 2018/1/21.
 */

public class SelectHolderImp {
    public static final byte STATE_ACTIVE = 1; //激活态（编辑态）
    public static final byte STATE_FIX = 2;    //固定态(非编辑态)
    private byte mState;

    private long mMinDuration = 2000000;   //最小时长，到达最小时长内再无法缩减, 默认2s
    private long mMaxDuration = 0;
    private long mDuration;     //时长
    private int mDistance;      //距离（TailView和HeadView的距离）（与时长对应）

    private CursorViewTouchImp mHeadTouchImp, mTailTouchImp;
    private SelectManager mSelectManager;
    private ISelectView mSelectView;

    private OnSelectedDurationChangeListener selectedDurationChangeListener;
    private OnSelectListener selectListener;

    public SelectHolderImp(SelectManager manager,    long startTime, long duration, ISelectView selectView,
                           long maxDuration, long minDuration) {
        this.mSelectManager = manager;
        mDuration = duration;
        mState = STATE_ACTIVE;
        mSelectView = selectView;
        mMaxDuration = maxDuration;
        mMinDuration = minDuration;
        initView(startTime);
        invalidate();
    }

    public void initView(long startTime) {
        if(mDuration < mMinDuration) {//不满足最小时长，则默认设置为最小时长
            mDuration = mMinDuration;
        }else if(mMaxDuration <= mDuration) {//如果动图时长，比最大时长（一般为视频时长）还要长，则默认设置为最大时长
            startTime = 0;
            mDuration = mMaxDuration;
        }
        if(mDuration+startTime > mMaxDuration) {//如果动图时长+startTime比最大时长大，则要向前移动，保证不超出范围。
            mDuration = mMaxDuration - startTime;
        }
        mHeadTouchImp = new CursorViewTouchImp(mSelectView.getHeadView(), startTime + mDuration);
        mTailTouchImp = new CursorViewTouchImp(mSelectView.getTailView(), startTime);
        setVisibility(false);
        mSelectManager.addSelectView(mSelectView.getContainer(), mTailTouchImp, this);

        mHeadTouchImp.setPositionListener(new CursorViewTouchImp.OnPositionListener() {
            @Override
            public void onPositionChanged(float dx) {
                long duration = mSelectManager.distance2Duration(dx);
                if (duration<0 &&
                        (mHeadTouchImp.getTimePoint() + duration - mTailTouchImp.getTimePoint()) < mMinDuration) {
                    //先计算可以减少的duration
                    duration = mMinDuration + mTailTouchImp.getTimePoint() - mHeadTouchImp.getTimePoint();
                } else if (duration > 0 && mHeadTouchImp.getTimePoint() + duration > mMaxDuration) {
                    duration = mMaxDuration - mHeadTouchImp.getTimePoint();
                }
                if (duration == 0) {
                    return;
                }
                mDuration += duration;

                ViewGroup.LayoutParams params = mSelectView.getMiddleView().getLayoutParams();
                params.width = mSelectManager.duration2Distance(mDuration);
                mHeadTouchImp.changeDuration(duration);
                mSelectView.getMiddleView().setLayoutParams(params);

                if (null != selectedDurationChangeListener) {
                    selectedDurationChangeListener.onDurationChange(mTailTouchImp.getTimePoint(),
                            mHeadTouchImp.getTimePoint(), mDuration);
                }
            }

            @Override
            public void onPositionComplete() {
                mSelectManager.seekTo(mHeadTouchImp.getTimePoint());
            }
        });

        mTailTouchImp.setPositionListener(new CursorViewTouchImp.OnPositionListener() {
            @Override
            public void onPositionChanged(float distance) {
                long duration = mSelectManager.distance2Duration(distance);
                if (duration > 0 && mDuration - duration < mMinDuration) {
                    duration = mDuration - mMinDuration;
                } else if (duration < 0 && mTailTouchImp.getTimePoint() + duration < 0) {
                    duration = -mTailTouchImp.getTimePoint();
                }
                if (duration == 0) {
                    return;
                }
                mDuration -= duration;
                mTailTouchImp.changeDuration(duration);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mTailTouchImp.getView().getLayoutParams();
                int dx = layoutParams.leftMargin;
                requestLayout();
                dx = layoutParams.leftMargin - dx;
                mTailTouchImp.getView().setLayoutParams(layoutParams);
                layoutParams = (ViewGroup.MarginLayoutParams) mSelectView.getMiddleView().getLayoutParams();
                layoutParams.width -= dx; // mTimelineBar.duration2Distance(mDuration);
                mSelectView.getMiddleView().setLayoutParams(layoutParams);
                if (null != selectedDurationChangeListener) {
                    selectedDurationChangeListener.onDurationChange(mTailTouchImp.getTimePoint(),
                            mHeadTouchImp.getTimePoint(), mDuration);
                }
            }


            @Override
            public void onPositionComplete() {
                mSelectManager.seekTo(mTailTouchImp.getTimePoint());
            }
        });

        mSelectView.getMiddleView().setOnTouchListener(new View.OnTouchListener() {
            private float mDownX = 0.0f;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
    /*    if (mState ==STATE_ACTIVE)
        return false;
*/
                int actionMasked = MotionEventCompat.getActionMasked(event);
                switch (actionMasked) {
                    case MotionEvent.ACTION_DOWN:
                        mSelectManager.markClickSelectHolder(SelectHolderImp.this);
      /*      mSelectManager.clearAllSelectState();
            //Fix: when stickerView is invisible, onSelect func not work
            mSelectManager.scrollToTimePoint(mTailTouchImp.getTimePoint());
            v.postDelayed(new Runnable() {
              @Override
              public void run() {
                selectListener.onSelect();
                switchState(STATE_ACTIVE);
              }
            },100);
            mSelectManager.bringSelectViewFront(mSelectView);*/
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
            /*if (Math.abs(mDownX - event.getX()) < 50) {
              mSelectManager.clearAllSelectState();
              selectListener.onSelect();
            }
            break;*/
                }

                return false;
            }
        });

    }

    public void responseClickEvent() {
        mSelectManager.clearAllSelectState();
        //Fix: when stickerView is invisible, onSelect func not work
        mSelectManager.scrollToTimePoint(mTailTouchImp.getTimePoint());
        mSelectView.getMiddleView().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectListener.onSelect();
                switchState(STATE_ACTIVE);
            }
        },100);
        mSelectManager.bringSelectViewFront(mSelectView);
    }

    public void requestLayout() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mTailTouchImp.getView().getLayoutParams();
        layoutParams.leftMargin = mSelectManager.calculateTailViewPosition(mTailTouchImp);
        mTailTouchImp.getView().setLayoutParams(layoutParams);
    }

    public void switchState(byte state) {
        mState = state;
        switch (state) {
            case STATE_ACTIVE://显示HeadView和TailView
                mTailTouchImp.active();
                mHeadTouchImp.active();
                mSelectView.getMiddleView().setBackgroundColor(mSelectView.getContainer().getContext().getResources()
                        .getColor(R.color.select_mask_color));
                mSelectView.getMiddleView().findViewById(R.id.middle_view_line_top).setVisibility(View.VISIBLE);
                mSelectView.getMiddleView().findViewById(R.id.middle_view_line_bottom).setVisibility(View.VISIBLE);
                break;
            case STATE_FIX:
                mTailTouchImp.fix();
                mHeadTouchImp.fix();
                mSelectView.getMiddleView().setBackgroundColor(mSelectView.getContainer().getContext().getResources()
                        .getColor(R.color.select_mask_color));
                mSelectView.getMiddleView().findViewById(R.id.middle_view_line_top).setVisibility(View.INVISIBLE);
                mSelectView.getMiddleView().findViewById(R.id.middle_view_line_bottom).setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void invalidate() {
        //首先根据duration 计算midlleView 的宽度
        mDistance = mSelectManager.duration2Distance(mDuration);
        ViewGroup.LayoutParams layoutParams = mSelectView.getMiddleView().getLayoutParams();
        layoutParams.width = mDistance;
        mSelectView.getMiddleView().setLayoutParams(layoutParams);
        switchState(mState);
    }

    public void setVisibility(boolean isVisible) {
        if(isVisible) {
            mTailTouchImp.getView().setAlpha(1);
            mHeadTouchImp.getView().setAlpha(1);
            mSelectView.getMiddleView().setAlpha(1);
        }else {
            mTailTouchImp.getView().setAlpha(0);
            mHeadTouchImp.getView().setAlpha(0);
            mSelectView.getMiddleView().setAlpha(0);
        }
    }

    public void clearSelectState() {
        if (null != selectListener) {
            selectListener.onUnSelect();
        }
    }

    public boolean isInsideOfMiddleView(int x, int y) {
        Rect rect = new Rect();
        mSelectView.getMiddleView().getHitRect(rect);
        return rect.contains(x, y);
    }

    public boolean isCurrentTimePointFit(long timePoint) {
        if (timePoint >= mTailTouchImp.getTimePoint() &&
                timePoint <= mHeadTouchImp.getTimePoint())
            return true;

        return false;
    }

    public void removeSelectFromManager() {
        mSelectManager.removeSelectView(mSelectView.getContainer(), this);
    }

    public void setSelectedDurationChangeListener(OnSelectedDurationChangeListener selectedDurationChangeListener) {
        this.selectedDurationChangeListener = selectedDurationChangeListener;
    }

    public void setSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public void setStickViewVisible(boolean visible) {
        if (null != selectListener) {
            if (visible) {
                selectListener.onShow();
            }else {
                selectListener.onDismiss();
            }
        }
    }

    public interface OnSelectedDurationChangeListener {
        void onDurationChange(long startTime, long endTime, long duration);
    }

    public interface OnSelectListener {
        void onSelect();
        void onUnSelect();
        void onShow();
        void onDismiss();
    }
}

