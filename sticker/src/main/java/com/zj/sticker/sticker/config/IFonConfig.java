package com.zj.sticker.sticker.config;

import android.graphics.Typeface;

import com.zj.sticker.sticker.config.consts.TestDisplayType;

/**
 * Created by zhangjun on 2018/1/19.
 */

public interface IFonConfig {

    Typeface getTypeface();

    @TestDisplayType int getDisplayType();
}
