package com.zj.videosticker;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.zj.sticker.sticker.StickerView;
import com.zj.sticker.sticker.config.TextStickerConfig;

/**
 * Created by zhangjun on 2018/1/24.
 */

public class TextChangeDialog extends Dialog{

    private View content;
    private StickerView stickerView;
    private EditText editText;

    public TextChangeDialog(Context context, StickerView stickerView) {
        super(context, R.style.bottom_dialog);
        this.stickerView = stickerView;
        initDialog(context);
    }

    private void initDialog(Context context) {
        content = LayoutInflater.from(context).inflate(R.layout.text_dialog,null);
        editText = content.findViewById(R.id.edit_reply);
        editText.setText(((TextStickerConfig)stickerView.getConfig()).getText());
        editText.setSelection(editText.getText().length());
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        content.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != stickerView) {
                    if (!TextUtils.isEmpty(editText.getText().toString().trim()) &&
                            stickerView.getConfig() instanceof TextStickerConfig) {
                        ((TextStickerConfig)stickerView.getConfig()).setText(editText.getText().toString().trim());
                        stickerView.loadBitmapCache(true);
                        dismiss();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initWindows();
        super.onCreate(savedInstanceState);
        setContentView(content);
        ObjectAnimator animator = ObjectAnimator.ofFloat(content, "alpha", 0, 1);
        animator.setStartDelay(200);
        animator.setDuration(200);
        animator.start();
    }

    private void initWindows() {
        Window window = getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
    }

}
