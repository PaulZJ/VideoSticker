package com.zj.textpath;

import android.graphics.Paint;

/**
 * Created by zhangjun on 2018/5/5.
 */

public class TextUtils {

    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (null != str && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j =0; j< len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }

        return iRet;
    }
}
