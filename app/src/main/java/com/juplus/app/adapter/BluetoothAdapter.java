package com.juplus.app.adapter;

import android.bluetooth.BluetoothDevice;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juplus.app.R;
import com.juplus.app.entity.DeviceBean;

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
        holder.txt_wifi_name.setText(TextUtils.isEmpty(bluetoothDevice.getName()) ? bluetoothDevice.getAddress() : bluetoothDevice.getName());
        //连接状态
        switch (deviceBean.getState()) {
            case DeviceBean.STATE_UNCONNECT://未连接
            case DeviceBean.STATE_BOND_NONE://未配对
                holder.txt_link_tips.setText("");
                break;
            case DeviceBean.STATE_BONDING://配对中
                holder.txt_link_tips.setText("配对中");
                break;
            case DeviceBean.STATE_BONDED://已配对
                holder.txt_link_tips.setText("已配对");
                break;
            case DeviceBean.STATE_CONNECTING://连接中
                holder.txt_link_tips.setText("连接中");
                break;
            case DeviceBean.STATE_CONNECTED://已连接
                holder.txt_link_tips.setText("已连接");
                break;
            case DeviceBean.STATE_DISCONNECTING://断开中
                holder.txt_link_tips.setText("断开中");
                break;
            case DeviceBean.STATE_DISCONNECTED://已断开
                holder.txt_link_tips.setText("已保存");
                break;
        }

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

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(deviceBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_signal;
        private TextView txt_wifi_name;
        private TextView txt_link_tips;
        private View layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            img_signal = itemView.findViewById(R.id.img_signal);
            txt_wifi_name = itemView.findViewById(R.id.txt_wifi_name);
            txt_link_tips = itemView.findViewById(R.id.txt_link_tips);
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
