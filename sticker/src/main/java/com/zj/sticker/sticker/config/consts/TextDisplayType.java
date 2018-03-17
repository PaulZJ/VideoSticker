package com.zj.sticker.sticker.config.consts;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.zj.sticker.sticker.config.consts.TextDisplayType.ART_TYPE_ONE;
import static com.zj.sticker.sticker.config.consts.TextDisplayType.NORMAL_TYPE;
import static com.zj.sticker.sticker.config.consts.TextDisplayType.STOKE_TYPE;

/**
 * Created by zhangjun on 2018/1/19.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({NORMAL_TYPE, STOKE_TYPE, ART_TYPE_ONE})
public @interface TextDisplayType {
    int NORMAL_TYPE = 0;
    int STOKE_TYPE = 1;
    int ART_TYPE_ONE = 2;
    int ART_TYPE_TWO = 3;
    int ART_TYPE_THREE = 4;
    int ART_TYPE_FOUR = 5;
    int ART_TYPE_THREE_VERTICAL = 6;
}
