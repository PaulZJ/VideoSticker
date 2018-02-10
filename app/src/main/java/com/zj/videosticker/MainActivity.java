package com.zj.videosticker;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zj.sticker.select.IPlayer;
import com.zj.sticker.select.SelectHolderImp;
import com.zj.sticker.select.SelectManager;
import com.zj.sticker.select.SelectView;
import com.zj.sticker.sticker.StickerHolderView;
import com.zj.sticker.sticker.StickerView;
import com.zj.sticker.sticker.config.IFonConfig;
import com.zj.sticker.sticker.config.ImageStickerConfig;
import com.zj.sticker.sticker.config.TextStickerConfig;
import com.zj.sticker.sticker.config.consts.TextDisplayType;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StickerHolderView.OnStickerSelectionCallback {
    private final int DEFAULT_COLOR = 0xFFffdd00; //ARGB
    private final int DEFAULT_BG_COLOR = 0xFF222222; //ARGB

    private StickerHolderView holderView;
    private RecyclerView recyclerView;
    private SelectManager selectManager;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        holderView.removeCallbacks(runnable);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        holderView = (StickerHolderView) findViewById(R.id.sticker_holder);
        holderView.setTextStickerSelectionCallback(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        final TestAdapter testAdapter = new TestAdapter(new ArrayList<String>() {{
            add("");
            add("");
            add("");
            add("");
            add("");
            add("");
            add("");
        }});
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(testAdapter);
                selectManager = new SelectManager(20000, 300, player,
                        (ViewGroup) recyclerView.getParent());
                selectManager.setRecycleView(recyclerView);
                selectManager.setTimelineDisplayWidth(recyclerView.getWidth());
            }
        });

    }

    public void OnClickAddImage(View view) {
        SelectHolderImp selectHolderImp = selectManager.addSelectHolder(selectManager.getCurrTimePoint(), 3000, new
                SelectView((ViewGroup) recyclerView.getParent()), 1000);
        ImageStickerConfig imageStickerConfig = new ImageStickerConfig(R.raw
                .sticker_mustache2);
        holderView.addStickerView(selectHolderImp, imageStickerConfig);

    }

    public void OnClickAddText(View view) {
        SelectHolderImp selectHolderImp = selectManager.addSelectHolder(selectManager.getCurrTimePoint(), 3000, new
                SelectView((ViewGroup) recyclerView.getParent()), 1000);
        holderView.addStickerView(selectHolderImp, new TextStickerConfig("this is a test", new IFonConfig
                () {
            @Nullable
            @Override
            public Typeface getTypeface() {
                return null;
            }

            @Override
            public int getDisplayType() {
                return TextDisplayType.STROKE_TYPE;
            }
        }, DEFAULT_COLOR, 20, DEFAULT_BG_COLOR, Paint.Align.LEFT,8));
    }

    public void OnClickRun(View view) {
        testVideoRun();
    }

    public void OnClickPause(View view) {
        holderView.removeCallbacks(runnable);
    }

    public float ratio = 0.0f;

    public void testVideoRun() {
        holderView.postDelayed(runnable, 100);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ratio += 0.01f;
            if (ratio <= 1) {
                selectManager.clearAllSelectState();
                selectManager.scroll(ratio);
            } else {
                ratio = 0.0f;
            }
            testVideoRun();
        }
    };

    private IPlayer player = new IPlayer() {
        @Override
        public long getCurrentTimePoint() {
            return 0;
        }

        @Override
        public void setCurrentTimePoint(long timePoint) {
            ratio = timePoint * 1.0f / 20000;
        }

        @Override
        public void pause() {
            holderView.removeCallbacks(runnable);
        }

        @Override
        public void start() {

        }

        @Override
        public void restart() {

        }
    };

    @Override
    public void onTextStickerSelected(TextStickerConfig config, boolean isNew) {

    }

    @Override
    public void onImageStickerSelected(ImageStickerConfig config, boolean isNew) {

    }

    @Override
    public void onNoneStickerSelected() {

    }

    @Override
    public void onTextStickerLongClick(StickerView textStickerView) {

    }

    private class TestAdapter extends RecyclerView.Adapter {
        private List<String> data;

        public TestAdapter(List<String> data) {
            this.data = data;
        }

        @Override
        public int getItemViewType(int position) {
            int type = 111;
            if (position == 0 || position == data.size() + 1)
                type = 1;

            return type;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View itemView = new View(parent.getContext());
                itemView.setLayoutParams(new ViewGroup.LayoutParams(recyclerView.getWidth() / 2,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return new TestViewHolder(itemView);
            } else {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(300,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return new TestViewHolder(imageView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position != 0 && position != (data.size() + 1))
                ((TestViewHolder) holder).onBind(data.get(position - 1));
        }

        @Override
        public int getItemCount() {
            return data.size() + 2;
        }
    }

    private class TestViewHolder extends RecyclerView.ViewHolder {

        public TestViewHolder(View itemView) {
            super(itemView);
        }

        public void onBind(String url) {
            ((ImageView) itemView).setImageResource(R.drawable.test);
        }
    }
}
