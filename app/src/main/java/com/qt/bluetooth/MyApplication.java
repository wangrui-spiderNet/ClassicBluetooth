package com.qt.bluetooth;

import android.app.Application;

import com.tencent.mmkv.MMKV;

/**
 * Created by noah on 2020/7/28.
 * Email:   maqiankun1991@outlook.com;
 * Manual:
 */

public class MyApplication extends Application {

    private static volatile MyApplication instance = null;
    public int FrameStateMarginRight;
    public int modulus;
    public float mDensity;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MMKV.initialize(this);

    }

    public static MyApplication getInstance() {
        return instance;
    }


}
