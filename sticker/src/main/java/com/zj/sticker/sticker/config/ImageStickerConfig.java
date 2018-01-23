package com.zj.sticker.sticker.config;

import android.support.annotation.DrawableRes;
import android.support.annotation.RawRes;

/**
 * Created by zhangjun on 2018/1/19.
 */

public class ImageStickerConfig implements StickerConfig {

    private @RawRes int stickerId;

    public ImageStickerConfig(@DrawableRes @RawRes int stickerId) {
        this.stickerId = stickerId;
    }

    @Override
    public StickType getType() {
        return StickType.IMAGE;
    }

    @Override
    public @DrawableRes @RawRes int getStickerId() {
        return 0;
    }
}
