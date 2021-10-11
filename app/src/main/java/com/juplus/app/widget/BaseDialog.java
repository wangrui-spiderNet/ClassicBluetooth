package com.juplus.app.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDialog;

import com.juplus.app.MyApplication;
import com.juplus.app.R;
import com.juplus.app.utils.ScreenUtils;


/**
 * 统一设置了所有dialog的宽度属性
 */
public abstract class BaseDialog extends AppCompatDialog {

    private static final float SCREEN_RATE = 0.7f;
    public BaseDialog(Context context) {
        super(context, R.style.CustomDialogTrans);
    }

    /**
     * @param gravity Gravity.TOP ...
     * @param widthRate
     */
    protected void setWindowParam(int gravity,float widthRate){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        int width = ScreenUtils.getScreenWidth(MyApplication.getInstance());

        params.width = widthRate==0 ? (int)(width*SCREEN_RATE): (int)(width*widthRate);

        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = gravity;
        window.setAttributes(params);
    }
}
