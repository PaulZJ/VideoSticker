package com.zj.sticker.sticker.config.consts;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.zj.sticker.sticker.config.consts.TestDisplayType.NORMAL_TYPE;
import static com.zj.sticker.sticker.config.consts.TestDisplayType.STROKE_TYPE;

/**
 * Created by zhangjun on 2018/1/19.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({NORMAL_TYPE, STROKE_TYPE})
public @interface TestDisplayType {
    int NORMAL_TYPE = 0;
    int STROKE_TYPE = 1;
}
