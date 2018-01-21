package com.zj.sticker.select;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangjun on 2018/1/21.
 */

public interface ISelectView {
    ViewGroup getContainer();
    View getHeadView();
    View getTailView();
    View getMiddleView();
}
