package com.zj.sticker.sticker.config;

/**
 * Created by zhangjun on 2018/1/19.
 */

public interface StickerConfig {
    enum StickType{
        IMAGE,
        TEXT
    }

    StickType getType();

    int getStickerId();
}
