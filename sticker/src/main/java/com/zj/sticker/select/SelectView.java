package com.zj.sticker.select;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zj.sticker.R;

/**
 * Created by zhangjun on 2018/1/21.
 */

public class SelectView implements ISelectView {
    private ViewGroup root;
    public SelectView(ViewGroup parent) {
        root = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.select_view_layout, parent, false);
    }

    @Override
    public ViewGroup getContainer() {
        return root;
    }

    @Override
    public View getHeadView() {
        return root.findViewById(R.id.head_view);
    }

    @Override
    public View getTailView() {
        return root.findViewById(R.id.tail_view);
    }

    @Override
    public View getMiddleView() {
        return root.findViewById(R.id.middle_view);
    }
}
