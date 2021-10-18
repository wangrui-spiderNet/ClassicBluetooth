package com.juplus.app;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.juplus.app.utils.LogUtils;

import java.util.List;


public class SplashActivity extends AppCompatActivity {
    private ImmersionBar immersionBar;
    private static final String[] mBTPerms = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        immersionBar = ImmersionBar.with(this);
        immersionBar.init();

        initPermission();
    }

    private void initPermission() {
        if(XXPermissions.isGranted(this,mBTPerms)){
            LogUtils.logCommon("有权限");
            toHome();
        }else{
            XXPermissions.with(this)
                    .permission(mBTPerms)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {

                            toHome();
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {

                            finish();
                        }
                    });

        }
    }

    private void toHome() {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
