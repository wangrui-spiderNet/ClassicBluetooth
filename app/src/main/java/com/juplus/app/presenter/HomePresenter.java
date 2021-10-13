package com.juplus.app.presenter;

import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HomePresenter {


    /**
     * 申请运行时权限，不授予会搜索不到设备
     */
    private void initPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, "android.permission-group.LOCATION") != 0) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            "android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION",
                            "android.permission.ACCESS_WIFI_STATE"},
                    1
            );
        }
    }
}
