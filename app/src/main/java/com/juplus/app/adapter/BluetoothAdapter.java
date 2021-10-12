package com.juplus.app.adapter;

import android.bluetooth.BluetoothDevice;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juplus.app.MyApplication;
import com.juplus.app.R;
import com.juplus.app.entity.DeviceBean;
import com.juplus.app.utils.UIUtil;
import com.noober.background.drawable.DrawableCreator;

import java.util.ArrayList;
import java.util.List;

public class BluetoothAdapter extends RecyclerView.Adapter<BluetoothAdapter.MyViewHolder> {
    private List<DeviceBean> datas;
    private ItemClickListener mItemClickListener;

    public BluetoothAdapter() {
        datas = new ArrayList<>();
    }

    public void addData(DeviceBean deviceBean) {
        datas.add(deviceBean);
        notifyDataSetChanged();
    }

    public void add(int index, DeviceBean deviceBean) {
        datas.add(index, deviceBean);
        notifyDataSetChanged();
    }

    public void addDataALL(List<DeviceBean> deviceBeans) {
        datas.clear();
        datas.addAll(deviceBeans);
        notifyDataSetChanged();
    }

    public void clear() {
        datas.clear();
    }

    public List<DeviceBean> getData() {
        return datas;
    }

    public void remove(Object o) {
        datas.remove(o);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DeviceBean deviceBean = datas.get(position);
        BluetoothDevice bluetoothDevice = deviceBean.getBluetoothDevice();
        holder.tv_device_name.setText(TextUtils.isEmpty(bluetoothDevice.getName()) ? bluetoothDevice.getAddress() : bluetoothDevice.getName());
        //连接状态
//        switch (deviceBean.getState()) {
//            case DeviceBean.STATE_UNCONNECT://未连接
//            case DeviceBean.STATE_BOND_NONE://未配对
//                holder.iv_headset_status.setText("");
//                break;
//            case DeviceBean.STATE_BONDING://配对中
//                holder.iv_headset_status.setText("配对中");
//                break;
//            case DeviceBean.STATE_BONDED://已配对
//                holder.iv_headset_status.setText("已配对");
//                break;
//            case DeviceBean.STATE_CONNECTING://连接中
//                holder.iv_headset_status.setText("连接中");
//                break;
//            case DeviceBean.STATE_CONNECTED://已连接
//                holder.iv_headset_status.setText("已连接");
//                break;
//            case DeviceBean.STATE_DISCONNECTING://断开中
//                holder.iv_headset_status.setText("断开中");
//                break;
//            case DeviceBean.STATE_DISCONNECTED://已断开
//                holder.iv_headset_status.setText("已保存");
//                break;
//        }

//        @SuppressLint("MissingPermission") int styleMajor = bluetoothDevice.getBluetoothClass().getMajorDeviceClass();//获取蓝牙主要分类
//        switch (styleMajor) {
//            case BluetoothClass.Device.Major.AUDIO_VIDEO://音频设备
//                holder.img_signal.setImageResource(R.mipmap.icon_headset);
//                break;
//            case BluetoothClass.Device.Major.COMPUTER://电脑
//                holder.img_signal.setImageResource(R.mipmap.icon_computer);
//                break;
//            case BluetoothClass.Device.Major.HEALTH://健康状况
//                holder.img_signal.setImageResource(R.mipmap.icon_bluetooth);
//                break;
//            case BluetoothClass.Device.Major.IMAGING://镜像，映像
//                holder.img_signal.setImageResource(R.mipmap.icon_bluetooth);
//                break;
//            case BluetoothClass.Device.Major.MISC://麦克风
//                holder.img_signal.setImageResource(R.mipmap.icon_bluetooth);
//                break;
//            case BluetoothClass.Device.Major.NETWORKING://网络
//                holder.img_signal.setImageResource(R.mipmap.icon_bluetooth);
//                break;
//            case BluetoothClass.Device.Major.PERIPHERAL://外部设备
//                holder.img_signal.setImageResource(R.mipmap.icon_bluetooth);
//                break;
//            case BluetoothClass.Device.Major.PHONE://电话
//                holder.img_signal.setImageResource(R.mipmap.icon_phone);
//                break;
//            case BluetoothClass.Device.Major.TOY://玩具
//                holder.img_signal.setImageResource(R.mipmap.icon_bluetooth);
//                break;
//            case BluetoothClass.Device.Major.UNCATEGORIZED://未知的
//                holder.img_signal.setImageResource(R.mipmap.icon_bluetooth);
//                break;
//            case BluetoothClass.Device.Major.WEARABLE://穿戴设备
//                holder.img_signal.setImageResource(R.mipmap.icon_bluetooth);
//                break;
//        }

        Drawable drawableBg;
        if (deviceBean.getSelected()) {
            drawableBg = new DrawableCreator.Builder().setCornersRadius(UIUtil.dip2px(MyApplication.getInstance(), 10))
                    .setSolidColor(Color.parseColor("#1BAEAE"))
                    .build();
            holder.iv_headset_status.setImageResource(R.mipmap.icon_head_status_on);
            holder.tv_device_name.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.white));
            holder.tv_device_status.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.white));
        } else {
            drawableBg = new DrawableCreator.Builder().setCornersRadius(UIUtil.dip2px(MyApplication.getInstance(), 10))
                    .setSolidColor(Color.parseColor("#F6F7F9"))
                    .build();
            holder.iv_headset_status.setImageResource(R.mipmap.icon_head_status_off);
            holder.tv_device_name.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_222222));
            holder.tv_device_status.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_222222));
        }

        holder.layout.setBackground(drawableBg);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    for (DeviceBean dBean:datas){
                        dBean.setSelected(false);
                    }

                    deviceBean.setSelected(true);
                    mItemClickListener.onItemClickListener(deviceBean);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_device_name,tv_device_status;
        private ImageView iv_headset_status;
        private View layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_device_name = itemView.findViewById(R.id.tv_device_name);
            tv_device_status = itemView.findViewById(R.id.tv_device_status);
            iv_headset_status = itemView.findViewById(R.id.iv_headset_status);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    public void setItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClickListener(DeviceBean deviceBean);
    }
}
