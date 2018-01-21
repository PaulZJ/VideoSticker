package com.zj.sticker.sticker.textdraw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.zj.sticker.sticker.config.TextStickerConfig;

/**
 * Created by zhangjun on 2018/1/21.
 */

public class StokeTextDraw implements ITextDraw {
    @Override
    public Picture drawTextToPicture(@NonNull TextStickerConfig config) {
        final String text = config.getText();

        final Rect textBounds = new Rect();
        final Paint bgPaint = new Paint();
        final Picture picture = new Picture();
        final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        final TextPaint stokePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(config.getColor());
        paint.setTextSize(80);
        paint.setTypeface(config.getFontConfig().getTypeface());
        paint.setAntiAlias(true);
        paint.setTextAlign(config.getAlign());
        paint.setSubpixelText(true);
        paint.setHinting(Paint.HINTING_ON);
        paint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);

        stokePaint.setColor(Color.RED);
        stokePaint.setTextSize(80);
        stokePaint.setTypeface(config.getFontConfig().getTypeface());
        stokePaint.setAntiAlias(true);
        stokePaint.setStyle(Paint.Style.STROKE);
        stokePaint.setStrokeWidth(3);
        stokePaint.setSubpixelText(true);
        stokePaint.setHinting(Paint.HINTING_ON);

        bgPaint.setColor(config.getBackgroundColor());

        StaticLayout staticLayout = new StaticLayout("AAAAAAAA", paint, 1000, Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false);
        final int textHeight = staticLayout.getHeight();
        final int textWidth = staticLayout.getWidth();

        staticLayout = new StaticLayout(text, paint, textWidth, Layout.Alignment.ALIGN_CENTER,
                1.0f, 0.0f, false);

        StaticLayout stokeStaticLayout = new StaticLayout(text, stokePaint, textWidth, Layout.Alignment.ALIGN_CENTER,
                1.0f, 0.0f, false);

        final Canvas canvas = picture.beginRecording(textWidth, textHeight*staticLayout.getLineCount());
        final Rect rect = new Rect(0, 0, textWidth, textHeight*staticLayout.getLineCount());

        canvas.drawRect(rect, bgPaint);
        canvas.save();
        staticLayout.draw(canvas);
        stokeStaticLayout.draw(canvas);
        canvas.restore();

        picture.endRecording();
        return picture;
    }
}
