package com.qt.bluetooth;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;


public class HomeActivity extends AppCompatActivity {
    ImmersionBar immersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        immersionBar= ImmersionBar.with(this);
//        immersionBar.init();
    }

}
