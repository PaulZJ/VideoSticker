package com.zj.sticker.sticker.config;

import android.graphics.Paint;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by zhangjun on 2018/1/19.
 */
@AllArgsConstructor
@Data
public class TextStickerConfig implements StickerConfig{
    private String text;
    private IFonConfig fontConfig;
    private int color;
    private int size;
    private int backgroundColor;
    private Paint.Align align;

    private final String identifierId = UUID.randomUUID().toString();

    @Override
    public StickType getType() {
        return StickType.TEXT;
    }

    @Override
    public int getStickerId() {
        return -1;
    }
}
