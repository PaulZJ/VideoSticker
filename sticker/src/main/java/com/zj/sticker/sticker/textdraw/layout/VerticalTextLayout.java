package com.zj.sticker.sticker.textdraw.layout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjun on 2018/2/10.
 */


public class VerticalTextLayout implements ITextLayout {

    private static final float LINE_MULTIPLIER = 1.2F;

    private TextPaint paint;
    private String text;
    private TextGroup textGroup;

    /**
     * 目前文字只支持居中
     */
    public VerticalTextLayout(String text, TextPaint paint, int maxWordNumByLine) {
        this.paint = paint;
        this.text = text;
        this.textGroup = new TextGroup(maxWordNumByLine, text, paint);
    }

    public void draw(Canvas canvas) {
        textGroup.draw(canvas);
    }

    @Override
    public int getWidth() {
        return textGroup.getWidth();
    }

    @Override
    public int getHeight() {
        return textGroup.getHeight();
    }

    private static class TextGroup {
        private List<TextLine> lines = new ArrayList<>();
        private int limit;
        private String text;
        private TextPaint paint;

        public TextGroup(int limit, String text, TextPaint paint) {
            this.limit = limit;
            this.text = text;
            this.paint = paint;
            makeGroup();
        }

        private void makeGroup() {
            int used = 0;
            while (used < text.length()) {
                if (used + limit > text.length()) {
                    lines.add(new TextLine(text.substring(used), paint));
                } else {
                    lines.add(new TextLine(text.substring(used, used + limit), paint));
                }
                used += limit;
            }
        }

        private void draw(Canvas canvas) {
            canvas.save();
            for (int i = 0; i < lines.size(); i++) {
                lines.get(i).draw(canvas, i);
            }
            canvas.restore();
        }

        public int getWidth() {
            if (lines.size() == 0) {
                return 0;
            }
            return lines.size() * lines.get(0).getWidth();
        }

        public int getHeight() {
            if (lines.size() == 0) {
                return 0;
            }
            return lines.get(0).getHeight();
        }
    }

    private static class TextLine {
        private String text;
        private TextPaint paint;
        private List<Text> texts = new ArrayList<>();
        private int width;
        private int height;

        private TextLine(String text, TextPaint paint) {
            this.text = text;
            this.paint = paint;
            makeLine();
            this.width = (int) ((int) paint.measureText("中") * LINE_MULTIPLIER);
            this.height = (int) ((paint.getFontMetrics().descent - paint.getFontMetrics().ascent) * texts.size());
        }

        private void makeLine() {
            for (int i = 0; i < text.length(); i++) {
                texts.add(new Text(text.substring(i, i + 1), paint));
            }
        }

        private void draw(Canvas canvas, int pos) {
            int lineOffset = getWidth() * pos;
            canvas.translate(lineOffset, 0);
            for (int i = 0; i < text.length(); i++) {
                texts.get(i).draw(canvas, i);
            }
            canvas.translate(-lineOffset, 0);
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    private static class Text {
        private String text;
        private TextPaint paint;
        private float width;
        private float height;

        private Text(String text, TextPaint paint) {
            this.text = text;
            this.paint = paint;
            this.width = paint.measureText(text);
            this.height = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;
        }

        private void draw(Canvas canvas, int pos) {
      /*
        Pain 中 Align 为 {@link Paint.Align.CENTER} 的时候
        canvas.drawText(text, x, y, paint) 中传入的 x 为文字中心，y 为文字 botLine
       */
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            int baseLine = (int) (height / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
      /*
       * offsetX 这里把每个文字当做一个方块处理，所以用每个文字相同的高度来处理
       */
            float xOffset = height / 2;
            float yOffset = height * pos + baseLine;
            canvas.translate(xOffset, yOffset);
            canvas.drawText(text, 0, 0, paint);
            canvas.translate(-xOffset, -yOffset);
        }

        public float getHeight() {
            return height;
        }

        public float getWidth() {
            return width;
        }
    }

}
