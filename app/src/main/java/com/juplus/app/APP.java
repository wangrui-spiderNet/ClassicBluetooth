package com.juplus.app;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.Objects;

/**
 * Created by noah on 2020/7/28.
 * Email:   maqiankun1991@outlook.com;
 * Manual:
 */

public class APP extends Application {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static volatile APP instance = null;
    private static Toast sToast; // 单例Toast,避免重复创建，显示时间过长
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MMKV.initialize(this);
        sToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    public static APP getInstance() {
        return instance;
    }

    public static void toast(String txt, int duration) {
        sToast.setText(txt);
        sToast.setDuration(duration);
        sToast.show();
    }

    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }
    /**
     * 针对 Android 11 裁剪文件存储
     *
     * @return
     */
    public String filePath(){
        File externalFileRootDir = getInstance().getExternalFilesDir(null);
        do {
            externalFileRootDir = Objects.requireNonNull(externalFileRootDir).getParentFile();
        } while (Objects.requireNonNull(externalFileRootDir).getAbsolutePath().contains("/Android"));

        String saveDir = Objects.requireNonNull(externalFileRootDir).getAbsolutePath();
        return saveDir+ "/" + Environment.DIRECTORY_PICTURES;
    }
}
