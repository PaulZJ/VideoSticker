package com.zj.sticker.select;

/**
 * Created by zhangjun on 2018/1/21.
 */

public interface IPlayer {
    long getCurrentTimePoint();
    void setCurrentTimePoint(long tImePoint);
    void pause();
    void start();
    void restart();
}
