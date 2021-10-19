package com.juplus.app.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.juplus.app.R;

import java.util.List;


public class BleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context               mContext;
    private       List<BluetoothDevice> mBluetoothDevices;

    public BleListAdapter(Context context, List<BluetoothDevice> list) {
        mContext = context;
        mBluetoothDevices = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_ble_list_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        BluetoothDevice bluetoothDevice = mBluetoothDevices.get(position);
        viewHolder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClickListener(bluetoothDevice, position);
                }
            }
        });
        viewHolder.mTvAddress.setText(bluetoothDevice.getAddress());
        viewHolder.mTvName.setText(bluetoothDevice.getName());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return null == mBluetoothDevices ? 0 : mBluetoothDevices.size();
    }

    public void setNewData(List<BluetoothDevice> bleList) {
        mBluetoothDevices = bleList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTvName, mTvAddress;
        LinearLayout mItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItem = (LinearLayout) itemView;
            mTvName = (TextView) itemView.findViewById(R.id.tv_ble_name);
            mTvAddress = (TextView) itemView.findViewById(R.id.tv_ble_address);
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(BluetoothDevice bleDevice, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
