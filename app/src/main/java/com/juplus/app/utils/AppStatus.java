package com.juplus.app.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by noah on 2020/9/11.
 * Email:   maqiankun1991@outlook.com;
 * Manual:
 */

public class AppStatus {

    public static boolean isDebugVersion(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
