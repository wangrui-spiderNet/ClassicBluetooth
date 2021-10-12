package com.juplus.app.utils;

import android.content.Context;

public class UIUtil {
    public static int dip2px(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) dip * scale + 0.5F);
    }
}
