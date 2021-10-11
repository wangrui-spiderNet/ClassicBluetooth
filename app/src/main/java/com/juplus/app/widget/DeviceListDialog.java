package com.juplus.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.juplus.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 用于操作确认弹窗
 */
public class DeviceListDialog extends BaseDialog {
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    private Context mContext;

    public DeviceListDialog(@NonNull Context context, String tip) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.dialog_device_list, null);
        setContentView(view);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setWindowParam(Gravity.CENTER,1f);


    }

//    @OnClick({R.id.tv_ok})
//    public void onViewClicked(View view) {
//        if (SystemUtil.isFastClick()) {
//            return;
//        }
//        switch (view.getId()) {
//
//            case R.id.tv_ok:
//                dismiss();
//
//                break;
//            default:
//                break;
//        }
//    }


}
