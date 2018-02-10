package com.zj.sticker.sticker.textdraw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.LinearLayout;

import com.zj.sticker.sticker.config.TextStickerConfig;
import com.zj.sticker.sticker.textdraw.layout.ITextLayout;
import com.zj.sticker.sticker.textdraw.layout.NormalTextLayout;
import com.zj.sticker.sticker.textdraw.layout.VerticalTextLayout;

import java.nio.ByteBuffer;

/**
 * Created by zhangjun on 2018/2/10.
 */

public class ArtTextDraw implements ITextDraw {
    private static final int OFFSET = 12;
    private static final int FONT_SIZE = 160;
    private static final int TEXT_STROKE = 4;
    private static final int TOTAL_FRAME = 24;
    private static final int TOTAL_FRAME_STROKE = 4;

    private TextPaint paintMainText;
    private TextPaint paintSubText;
    private TextPaint paintMainTextStroke;
    private TextPaint paintSubTextStroke;
    private Paint paintAllFrame;
    private Paint paintAllFrameStroke;
    private Config artConfig;

    int lineTextWidth = 0;
    int lineTextHeight = 0;

    public ArtTextDraw(Config artConfig) {
        this.artConfig = artConfig;
        //主字
        paintMainText = new TextPaint();
        paintMainText.setColor(artConfig.mainTextColor);
        paintMainText.setAntiAlias(true);
        paintMainText.setTextSize(FONT_SIZE);
        paintMainText.setMaskFilter(null);
        paintMainText.setFakeBoldText(true);
        //次字
        paintSubText = new TextPaint();
        paintSubText.setColor(artConfig.subTextColor);
        paintSubText.setAntiAlias(true);
        paintSubText.setTextSize(FONT_SIZE);
        paintSubText.setFakeBoldText(true);

        //主字描边
        paintMainTextStroke = new TextPaint();
        paintMainTextStroke.setColor(artConfig.mainTextStrokeColor);
        paintMainTextStroke.setAntiAlias(true);
        paintMainTextStroke.setTextSize(FONT_SIZE);
        paintMainTextStroke.setStyle(Paint.Style.STROKE);
        paintMainTextStroke.setFakeBoldText(true);
        paintMainTextStroke.setStrokeWidth(TEXT_STROKE);

        //次字描边
        paintSubTextStroke = new TextPaint();
        paintSubTextStroke.setColor(artConfig.subTextStrokeColor);
        paintSubTextStroke.setAntiAlias(true);
        paintSubTextStroke.setTextSize(FONT_SIZE);
        paintSubTextStroke.setStyle(Paint.Style.STROKE);
        paintSubTextStroke.setFakeBoldText(true);
        paintSubTextStroke.setStrokeWidth(TEXT_STROKE);

        //整体描边
        paintAllFrame = new Paint();
        paintAllFrame.setStrokeWidth(TOTAL_FRAME);
        paintAllFrame.setStyle(Paint.Style.STROKE);
        paintAllFrame.setColor(artConfig.frameColor);
        paintAllFrame.setAntiAlias(true);
        paintAllFrame.setStrokeCap(Paint.Cap.ROUND);

        //整体描边的描边
        paintAllFrameStroke = new Paint();
        paintAllFrameStroke.setStrokeWidth(TOTAL_FRAME + TOTAL_FRAME_STROKE);
        paintAllFrameStroke.setStyle(Paint.Style.STROKE);
        paintAllFrameStroke.setColor(artConfig.frameStrokeColor);
        paintAllFrameStroke.setAntiAlias(true);
        paintAllFrameStroke.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public Picture drawTextToPicture() {
        Picture picture = new Picture();
        //主文字
        ITextLayout mainTextRegion = newTextLayout(artConfig.textStickerConfig.getText(), paintMainText);
        //次文字（主文字下方的立体雕塑）
        ITextLayout subTextRegion = newTextLayout(artConfig.textStickerConfig.getText(), paintSubText);
        //主文字描边
        ITextLayout mainTextRegionStroke = newTextLayout(artConfig.textStickerConfig.getText(), paintMainTextStroke);
        //次文字（主文字下方的立体雕塑）
        ITextLayout subTextRegionStroke = newTextLayout(artConfig.textStickerConfig.getText(), paintSubTextStroke);

        //创建一个 staticLayout 所需大小的 bitmap 和其 canvas
        Bitmap bitmap = Bitmap.createBitmap(mainTextRegion.getWidth() + OFFSET,
                mainTextRegion.getHeight() + OFFSET, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(bitmap);
        tempCanvas.translate(0, OFFSET);
        //画立体雕塑
        tempCanvas.translate(OFFSET, OFFSET);
        subTextRegion.draw(tempCanvas);
        tempCanvas.translate(-OFFSET, -OFFSET);

        //把字画在 canvas 的 bitmap 上
        mainTextRegion.draw(tempCanvas);

        //获取主文字+立体雕塑的边缘，至此所有数据准备完毕，tempCanvas 不再使用，因为要调整绘制顺序
        Path path = edge(bitmap.extractAlpha());
        //为了逻辑清晰，下面重新建立一个 canvas
        //调整顺序重新画一遍
        bitmap.recycle();
        Canvas picCanvas = picture.beginRecording(mainTextRegion.getWidth() + OFFSET, mainTextRegion.getHeight() + OFFSET);

        if (artConfig.enableFrameStroke) {
            //先画全部描边的描边
            picCanvas.drawPath(path, paintAllFrameStroke);
        }
        if (artConfig.enableFrame) {
            //先画全部描边
            picCanvas.drawPath(path, paintAllFrame);
        }

        //path 的位置是已经平移过的  所以啊，画完 path 再平移
        picCanvas.translate(0, OFFSET);
        //再画背景
        picCanvas.translate(OFFSET, OFFSET);
        if (artConfig.enableSubTextStroke) {
            subTextRegionStroke.draw(picCanvas);
        }
        if (artConfig.enableSubText) {
            subTextRegion.draw(picCanvas);
        }
        picCanvas.translate(-OFFSET, -OFFSET);
        if (artConfig.enableMainTextStroke) {
            //再画主字描边
            mainTextRegionStroke.draw(picCanvas);
        }
        //再画主字
        mainTextRegion.draw(picCanvas);

        picture.endRecording();
        return picture;
    }

    private ITextLayout newTextLayout(String text, TextPaint paint) {
        measureOriginTextRegion();
        switch (artConfig.orientation) {
            case LinearLayout.VERTICAL:
                return new VerticalTextLayout(text, paint, artConfig.textStickerConfig.getMaxWordNumByLine());
            case LinearLayout.HORIZONTAL:
            default:
                return new NormalTextLayout(text, paint, lineTextWidth, Layout.Alignment.ALIGN_CENTER);
        }
    }

    @Override
    public void measureOriginTextRegion() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< artConfig.textStickerConfig.getMaxWordNumByLine(); i++)
            sb.append("中");

        StaticLayout testLayout = new StaticLayout("A", paintMainText, (int) paintMainText.measureText(sb.toString()), Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false);
        final int textHeight = testLayout.getHeight();
        final int textWidth = testLayout.getWidth();
        lineTextWidth = textWidth;
        lineTextHeight = textHeight;
    }

    private Path edge(Bitmap bitmap) {
        int bmpW = bitmap.getWidth();
        int bmpH = bitmap.getHeight();
        Path path = new Path();
        ByteBuffer mBuf = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(mBuf);
        byte[] bytes = mBuf.array();
        for (int i = 0; i < bmpW; i++) {
            for (int j = 0; j < bmpH; j++) {
                if (getPixel(bytes, bmpW, i, j) != Color.TRANSPARENT
                        && (getPixel(bytes, bmpW, i - 1, j) == Color.TRANSPARENT
                        || getPixel(bytes, bmpW, i + 1, j) == Color.TRANSPARENT
                        || getPixel(bytes, bmpW, i, j - 1) == Color.TRANSPARENT
                        || getPixel(bytes, bmpW, i, j + 1) == Color.TRANSPARENT)) {
                    path.addCircle(i, j, 1, Path.Direction.CCW);
                }
            }
        }
        mBuf.clear();
        return path;
    }

    private int getPixel(byte[] bytes, int bmW, int j, int i) {
        int pos = i * bmW + j;
        if (pos < 0 || pos >= bytes.length) {
            return -1;
        }
        return bytes[pos];
    }

    public static class Config {
        public static final Config ART_WORD_ONE = new Config();
        public static final Config ART_WORD_TWO = new Config();
        public static final Config ART_WORD_THREE = new Config();
        public static final Config ART_WORD_FOUR = new Config();
        public static final Config ART_WORD_THREE_VERTICAL = new Config();

        public TextStickerConfig textStickerConfig;

        public Config setTextStickerConfig(TextStickerConfig textStickerConfig) {
            this.textStickerConfig = textStickerConfig;
            return this;
        }

        static {
            ART_WORD_ONE.mainTextColor = Color.parseColor("#FFC817");
            ART_WORD_ONE.enableMainTextStroke = true;
            ART_WORD_ONE.mainTextStrokeColor = Color.BLACK;
            ART_WORD_ONE.enableSubText = true;
            ART_WORD_ONE.subTextColor = Color.WHITE;
            ART_WORD_ONE.enableSubTextStroke = true;
            ART_WORD_ONE.subTextStrokeColor = Color.BLACK;
            ART_WORD_ONE.enableFrame = false;
            ART_WORD_ONE.enableFrameStroke = false;

            ART_WORD_TWO.mainTextColor = Color.parseColor("#FFC817");
            ART_WORD_TWO.enableMainTextStroke = true;
            ART_WORD_TWO.mainTextStrokeColor = Color.BLACK;
            ART_WORD_TWO.enableSubText = true;
            ART_WORD_TWO.subTextColor = Color.BLACK;
            ART_WORD_TWO.enableSubTextStroke = true;
            ART_WORD_TWO.subTextStrokeColor = Color.BLACK;
            ART_WORD_TWO.enableFrame = false;
            ART_WORD_TWO.enableFrameStroke = false;

            ART_WORD_THREE.mainTextColor = Color.parseColor("#FFC817");
            ART_WORD_THREE.enableMainTextStroke = true;
            ART_WORD_THREE.mainTextStrokeColor = Color.BLACK;
            ART_WORD_THREE.enableSubText = true;
            ART_WORD_THREE.subTextColor = Color.BLACK;
            ART_WORD_THREE.enableSubTextStroke = true;
            ART_WORD_THREE.subTextStrokeColor = Color.BLACK;
            ART_WORD_THREE.enableFrame = true;
            ART_WORD_THREE.frameColor = Color.WHITE;
            ART_WORD_THREE.enableFrameStroke = false;

            ART_WORD_FOUR.mainTextColor = Color.BLACK;
            ART_WORD_FOUR.enableMainTextStroke = true;
            ART_WORD_FOUR.mainTextStrokeColor = Color.WHITE;
            ART_WORD_FOUR.enableSubText = true;
            ART_WORD_FOUR.subTextColor = Color.BLACK;
            ART_WORD_FOUR.enableSubTextStroke = true;
            ART_WORD_FOUR.subTextStrokeColor = Color.WHITE;
            ART_WORD_FOUR.enableFrame = false;
            ART_WORD_FOUR.enableFrameStroke = false;

            ART_WORD_THREE_VERTICAL.mainTextColor = Color.parseColor("#FFC817");
            ART_WORD_THREE_VERTICAL.enableMainTextStroke = true;
            ART_WORD_THREE_VERTICAL.mainTextStrokeColor = Color.BLACK;
            ART_WORD_THREE_VERTICAL.enableSubText = true;
            ART_WORD_THREE_VERTICAL.subTextColor = Color.BLACK;
            ART_WORD_THREE_VERTICAL.enableSubTextStroke = true;
            ART_WORD_THREE_VERTICAL.subTextStrokeColor = Color.BLACK;
            ART_WORD_THREE_VERTICAL.enableFrame = true;
            ART_WORD_THREE_VERTICAL.frameColor = Color.WHITE;
            ART_WORD_THREE_VERTICAL.enableFrameStroke = false;
            ART_WORD_THREE_VERTICAL.orientation = LinearLayout.VERTICAL;
        }


        public boolean enableFrame = true;
        public boolean enableFrameStroke = true;
        public boolean enableSubText = true;
        public boolean enableSubTextStroke = true;
        public boolean enableMainTextStroke = true;

        public int mainTextColor = Color.RED;
        public int mainTextStrokeColor = Color.BLACK;
        public int subTextColor = Color.BLACK;
        public int subTextStrokeColor = Color.YELLOW;
        public int frameColor = Color.WHITE;
        public int frameStrokeColor = Color.BLUE;

        public int orientation = LinearLayout.HORIZONTAL;

    }
}
