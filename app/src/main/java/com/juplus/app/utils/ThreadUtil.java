package com.juplus.app.utils;

import android.util.Log;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private static final String TAG = ThreadUtil.class.getSimpleName();
    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public static void mkdirs(String filePath) {
        boolean mk = new File(filePath).mkdirs();
        Log.d(TAG, "mkdirs: " + mk);
    }
}
