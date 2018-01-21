package com.zj.sticker.select;

/**
 * Created by zhangjun on 2018/1/21.
 */

public interface PlayerSeekListener {
    void onSeek(long duration);
    void onSeekFinish(long duration);
}
