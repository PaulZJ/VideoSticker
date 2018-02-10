package com.zj.sticker.sticker.textdraw;

import android.graphics.Picture;
import android.support.annotation.NonNull;

import com.zj.sticker.sticker.config.TextStickerConfig;

/**
 * Created by zhangjun on 2018/1/19.
 */

public interface ITextDraw {
    /**
     * get the Picture Object with the Path of Texts
     * */
    Picture drawTextToPicture();
    /**
     * limit the Region to draw text
     * */
    void measureOriginTextRegion();
}
