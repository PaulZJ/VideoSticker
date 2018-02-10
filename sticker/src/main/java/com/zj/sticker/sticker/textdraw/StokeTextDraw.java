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
    private final TextPaint paint;
    private final TextPaint stokePaint;
    private final String text;
    private final Paint bgPaint;
    int lineTextWidth = 0;
    int lineTextHeight = 0;

    private TextStickerConfig config;

    public StokeTextDraw(@NonNull TextStickerConfig config) {
        this.config = config;
        text = config.getText();
        bgPaint = new Paint();
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(config.getColor());
        paint.setTextSize(80);
        paint.setTypeface(config.getFontConfig().getTypeface());
        paint.setAntiAlias(true);
        paint.setTextAlign(config.getAlign());
        paint.setSubpixelText(true);
        paint.setHinting(Paint.HINTING_ON);
        paint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
        stokePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        stokePaint.setColor(Color.RED);
        stokePaint.setTextSize(80);
        stokePaint.setTypeface(config.getFontConfig().getTypeface());
        stokePaint.setAntiAlias(true);
        stokePaint.setStyle(Paint.Style.STROKE);
        stokePaint.setStrokeWidth(3);
        stokePaint.setTextAlign(config.getAlign());
        stokePaint.setSubpixelText(true);
        stokePaint.setHinting(Paint.HINTING_ON);
        bgPaint.setColor(config.getBackgroundColor());
    }

    @Override
    public Picture drawTextToPicture() {
        final Picture picture = new Picture();
        measureOriginTextRegion();

        StaticLayout staticLayout = new StaticLayout(text, paint, lineTextWidth, Layout.Alignment.ALIGN_CENTER,
                1f, 0.0f, false);
        StaticLayout stokestaticLayout = new StaticLayout(text, stokePaint, lineTextWidth, Layout.Alignment.ALIGN_CENTER,
                1f, 0.0f, false);
        final Canvas canvas = picture.beginRecording(lineTextWidth, lineTextHeight * staticLayout.getLineCount());
        final Rect rect = new Rect(0, 0, lineTextWidth, lineTextHeight * staticLayout.getLineCount());
//    canvas.drawRect(rect, bgPaint);
        canvas.save();
        stokestaticLayout.draw(canvas);
        staticLayout.draw(canvas);
        canvas.restore();
        picture.endRecording();

        return picture;
    }

    @Override
    public void measureOriginTextRegion() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< config.getMaxWordNumByLine(); i++)
            sb.append("中");

        StaticLayout testLayout = new StaticLayout("A", paint, (int) paint.measureText(sb.toString()), Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false);
        final int textHeight = testLayout.getHeight();
        final int textWidth = testLayout.getWidth();
        lineTextWidth = textWidth;
        lineTextHeight = textHeight;
    }
}
