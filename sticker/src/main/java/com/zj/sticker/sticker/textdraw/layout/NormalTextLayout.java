package com.zj.sticker.sticker.textdraw.layout;

import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by zhangjun on 2018/2/10.
 */

public class NormalTextLayout extends StaticLayout implements ITextLayout{
    public NormalTextLayout(CharSequence source, TextPaint paint, int width, Alignment align) {
        super(source, paint, width,
                align, 1f, 0.0f, false);
    }
}
