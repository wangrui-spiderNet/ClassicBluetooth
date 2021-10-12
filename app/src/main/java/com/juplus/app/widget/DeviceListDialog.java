package com.juplus.app.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.juplus.app.R;
import com.juplus.app.adapter.BluetoothAdapter;
import com.juplus.app.entity.DeviceBean;
import com.juplus.app.utils.SystemUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 用于操作确认弹窗
 */
public class DeviceListDialog extends BaseDialog implements BluetoothAdapter.ItemClickListener {
    @BindView(R.id.tv_device_count)
    TextView tvDeviceCount;
    @BindView(R.id.tv_enter_bluetooth)
    TextView tvEnterBlueTooth;

    @BindView(R.id.recycler_view_paired)
    RecyclerView recyclerViewPaired;
    @BindView(R.id.iv_close_device_list)
    ImageView ivClose;
    @BindView(R.id.group_list)
    Group groupList;
    @BindView(R.id.group_empty)
    Group groupEmpty;
    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;

    private Context mContext;
    private List<DeviceBean> deviceBeans;
    private BluetoothAdapter mPairedAdapter;
    private BluetoothAdapter.ItemClickListener itemClickListener;

    public DeviceListDialog(@NonNull Context context
            , List<DeviceBean> deviceBeanList
            , BluetoothAdapter.ItemClickListener itemClickListener) {
        super(context);
        mContext = context;
        deviceBeans = deviceBeanList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.dialog_device_list, null);
        setContentView(view);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(true);
        setWindowParam(Gravity.TOP, 1f);

        mPairedAdapter = new BluetoothAdapter();
        recyclerViewPaired.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewPaired.addItemDecoration(new SpacesItemDecoration(10));
        recyclerViewPaired.setAdapter(mPairedAdapter);
        mPairedAdapter.setItemClickListener(this);
        updateData(deviceBeans);

    }

    public BluetoothAdapter getPairedAdapter() {
        return mPairedAdapter;
    }

    public void updateData(List<DeviceBean> deviceBeans){

        if (deviceBeans.size() == 0) {
            groupEmpty.setVisibility(View.VISIBLE);
            groupList.setVisibility(View.GONE);
        } else {
            groupEmpty.setVisibility(View.GONE);
            groupList.setVisibility(View.VISIBLE);
            tvDeviceCount.setText(deviceBeans.size() + "个设备");
            mPairedAdapter.addDataALL(deviceBeans);
            mPairedAdapter.notifyDataSetChanged();
        }

    }

    @OnClick({R.id.iv_close_device_list, R.id.tv_enter_bluetooth})
    public void onViewClicked(View view) {
        if (SystemUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {

            case R.id.iv_close_device_list:
                dismiss();

                break;

            case R.id.tv_enter_bluetooth:
                getContext().startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClickListener(DeviceBean deviceBean) {
        itemClickListener.onItemClickListener(deviceBean);
    }
}
