package com.juplus.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.juplus.app.R;
import com.juplus.app.adapter.SettingActionAdapter;
import com.juplus.app.entity.SettingBean;
import com.juplus.app.utils.SystemUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 音效设置弹窗
 */
public class SettingActionListDialog extends BaseDialog {

    private String mTitleName;

    @BindView(R.id.tv_title_name)
    TextView tvTitleName;
    @BindView(R.id.lvSettings)
    ListView lvSettings;

    private Context mContext;
    private CallBack onClickListener;
    private List<SettingBean> settingBeanList;

    public SettingActionListDialog(@NonNull Context context, String titleName,
                                   List<SettingBean> settingBeans, CallBack onClickListener) {
        super(context);
        mContext = context;
        mTitleName = titleName;
        settingBeanList = settingBeans;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.dialog_setting_action, null);
        setContentView(view);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setWindowParam(Gravity.BOTTOM, 1);

        if(TextUtils.isEmpty(mTitleName)){
            tvTitleName.setVisibility(View.GONE);
        }else{
            tvTitleName.setVisibility(View.VISIBLE);
            tvTitleName.setText(mTitleName);
        }

        SettingActionAdapter settingActionAdapter =new SettingActionAdapter(settingBeanList,mContext);
        lvSettings.setAdapter(settingActionAdapter);
        lvSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for(SettingBean bean : settingBeanList){
                    bean.isSelected = false;
                }

                settingBeanList.get(i).isSelected = true;
                onClickListener.callBack(settingBeanList.get(i));
                ((SettingActionAdapter)lvSettings.getAdapter()).notifyDataSetChanged();
            }
        });

    }

    @OnClick({ R.id.tv_cancel})
    public void onViewClicked(View view) {
        if (SystemUtil.isFastClick()) {
            return;
        }

        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

}
