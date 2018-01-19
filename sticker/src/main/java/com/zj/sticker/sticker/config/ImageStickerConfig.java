package com.zj.sticker.sticker.config;

/**
 * Created by zhangjun on 2018/1/19.
 */

public class ImageStickerConfig implements StickerConfig {


    @Override
    public StickType getType() {
        return StickType.IMAGE;
    }

    @Override
    public int getStickerId() {
        return 0;
    }
}
