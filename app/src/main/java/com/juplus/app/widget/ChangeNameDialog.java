package com.juplus.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.juplus.app.R;
import com.juplus.app.utils.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 用于操作确认弹窗
 */
public class ChangeNameDialog extends BaseDialog {

    private String mName;

    @BindView(R.id.tv_save)
    TextView mTvSave;
    @BindView(R.id.et_Name)
    EditText etName;
    @BindView(R.id.iv_clear_name)
    ImageView ivClearName;

    private Context mContext;
    private CallBack onClickListener;

    public ChangeNameDialog(@NonNull Context context, String name, CallBack onClickListener) {
        super(context);
        mContext = context;
        mName = name;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.dialog_change_name, null);
        setContentView(view);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setWindowParam(Gravity.BOTTOM, 1);
        if (!TextUtils.isEmpty(mName)) {
            etName.setText(mName);
        }

    }

    @OnClick({R.id.tv_save, R.id.tv_cancel, R.id.iv_clear_name})
    public void onViewClicked(View view) {
        if (SystemUtil.isFastClick()) {
            return;
        }

        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;

            case R.id.tv_save:
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    return;
                }
                onClickListener.callBack(etName.getText().toString());
                dismiss();
                break;

            case R.id.iv_clear_name:
                etName.setText("");
                break;
        }
    }

}
