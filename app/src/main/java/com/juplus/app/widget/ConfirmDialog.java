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
public class ConfirmDialog extends BaseDialog {

    private String mTip;
    private Spanned mSpanTip;
    private String mBtnName;

//    @BindView(R.id.tv_ok)
//    TextView mTvOk;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    private Context mContext;

    public ConfirmDialog(@NonNull Context context, String tip) {
        super(context);
        mContext = context;
        mTip = tip;
    }

    public ConfirmDialog(@NonNull Context context, String tip, String btnName) {
        super(context);
        mContext = context;
        mTip = tip;
        mBtnName = btnName;
    }

    public ConfirmDialog(@NonNull Context context, Spanned tip, String btnName) {
        super(context);
        mContext = context;
        mSpanTip = tip;
        mBtnName = btnName;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.dialog_confirm, null);
        setContentView(view);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setWindowParam(Gravity.CENTER,0);
        if (!TextUtils.isEmpty(mTip)) {
            mTvTip.setText(mTip);
        }

        if (!TextUtils.isEmpty(mSpanTip)) {
            mTvTip.setText(mSpanTip);
        }

        if(!TextUtils.isEmpty(mBtnName)){
//            mTvOk.setText(mBtnName);
        }

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
