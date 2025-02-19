package com.juplus.app.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.juplus.app.APP;
import com.juplus.app.R;

public class ToastUtil {

    public static void showToast( String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        View customview = LayoutInflater.from(APP.getInstance()).inflate(
                R.layout.toast_layout, null);
        TextView tvMeaagse = (TextView) customview.findViewById(R.id.message);
        tvMeaagse.setText(text);
        Toast mToast = Toast.makeText(APP.getInstance(),text,Toast.LENGTH_SHORT);
        mToast.setView(customview);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }
}
