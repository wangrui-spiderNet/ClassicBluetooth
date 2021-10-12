package com.juplus.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.juplus.app.R;
import com.juplus.app.SimpleAdapter;
import com.juplus.app.entity.DeviceBean;
import com.juplus.app.utils.SystemUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 用于操作确认弹窗
 */
public class DeviceListDialog extends BaseDialog implements SimpleAdapter.ItemClickListener {
    @BindView(R.id.tv_device_count)
    TextView tvDeviceCount;
    @BindView(R.id.recycler_view_paired)
    RecyclerView recyclerViewPaired;
    @BindView(R.id.iv_close_device_list)
    ImageView ivClose;

    private Context mContext;
    private List<DeviceBean> deviceBeans;

    public DeviceListDialog(@NonNull Context context, List<DeviceBean> deviceBeanList) {
        super(context);
        mContext = context;
        deviceBeans = deviceBeanList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.dialog_device_list, null);
        setContentView(view);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setWindowParam(Gravity.TOP, 1f);

        SimpleAdapter mPairedAdapter = new SimpleAdapter();
        recyclerViewPaired.setLayoutManager(new LinearLayoutManager(getContext()));
//        mRecyclerPaired.addItemDecoration(new SpacesItemDecoration(10));
        recyclerViewPaired.setAdapter(mPairedAdapter);
        mPairedAdapter.setItemClickListener(this);
        mPairedAdapter.addDataALL(deviceBeans);
        mPairedAdapter.notifyDataSetChanged();

    }

    @OnClick({R.id.iv_close_device_list})
    public void onViewClicked(View view) {
        if (SystemUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {

            case R.id.iv_close_device_list:
                dismiss();

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClickListener(DeviceBean deviceBean) {

    }
}
