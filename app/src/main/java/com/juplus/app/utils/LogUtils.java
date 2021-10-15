package com.juplus.app.utils;

import android.util.Log;

import com.juplus.app.APP;

public class LogUtils {
    public static final String TAG_COMMON = ">>>>>>>>common";
    public static final String TAG_BLUETOOTH = ">>>>>>>>bluetooth";

    public static void logCommon(String log){
        if(AppStatus.isDebugVersion(APP.getInstance())){
            Log.e(TAG_COMMON,log);
        }
    }

    public static void logBlueTooth(String log){
        if(AppStatus.isDebugVersion(APP.getInstance())){
            Log.e(TAG_BLUETOOTH,log);
        }
    }

}
