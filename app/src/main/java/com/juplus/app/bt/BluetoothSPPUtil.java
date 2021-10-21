package com.juplus.app.bt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.juplus.app.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.UUID;


@SuppressLint("MissingPermission")
public class BluetoothSPPUtil {

    private static boolean mEnableLogOut = false;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private OnBluetoothAction mOnBluetoothAction;
    private ConnectTask mConnectTask = new ConnectTask();

    /**
     * 搜索到新设备广播广播接收器
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (device == null) {
                return;
            }

            switch (action) {

//                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
//                    break;
//                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                    break;

                case BluetoothDevice.ACTION_FOUND:
                    if (mOnBluetoothAction != null)
                        mOnBluetoothAction.onFoundDevice(device);
                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST: //在系统弹出配对框之前，实现自动配对，取消系统配对框
                /*try {
                    abortBroadcast();//终止配对广播，取消系统配对框
                    boolean ret = dev.setPin("1234".getBytes()); //设置PIN配对码(必须是固定的)
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    if (mOnBluetoothAction != null)
                        mOnBluetoothAction.onStateChanged(device);
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    if (mOnBluetoothAction != null)
                        mOnBluetoothAction.onNewDeviceConnect(device);
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    if (mOnBluetoothAction != null)
                        mOnBluetoothAction.onDisconnectedChanged(device);
                    break;

            }

        }
    };

    /**
     * 连接任务
     */
    private static class ConnectTask extends AsyncTask<String, Byte[], Void> {
        private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket bluetoothSocket;
        BluetoothDevice romoteDevice;
        OnBluetoothAction onBluetoothAction;
        boolean isRunning = false;
        String stopString = "";

        @Override
        protected Void doInBackground(String... bluetoothDevicesMac) {
            // 记录标志位，开始运行
            isRunning = true;

            // 尝试获取 bluetoothSocket
            try {
                UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                romoteDevice = bluetoothAdapter.getRemoteDevice(bluetoothDevicesMac[0]);
                bluetoothSocket = romoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (Exception e) {
                LogUtils.logBlueTooth("获取Socket失败");
                isRunning = false;
                e.printStackTrace();
                return null;
            }

            // 检查有没有获取到
            if (bluetoothSocket == null) {
                onBluetoothAction.onConnectFailed("连接失败:获取Socket失败");
                isRunning = false;
                return null;
            }

            // 尝试连接
            try {
                // 等待连接，会阻塞线程
                bluetoothSocket.connect();
                 LogUtils.logBlueTooth("连接成功");
                onBluetoothAction.onConnectSuccess(romoteDevice);
            } catch (Exception connectException) {
                connectException.printStackTrace();
                 LogUtils.logBlueTooth("连接失败:" + connectException.getMessage());
                onBluetoothAction.onConnectFailed("连接失败:" + connectException.getMessage());
                return null;
            }

            // 开始监听数据接收
            try {
                InputStream inputStream = bluetoothSocket.getInputStream();
                byte[] result = new byte[0];
                while (isRunning) {
//                     LogUtils.logBlueTooth("looping");
                    byte[] buffer = new byte[256];
                    // 等待有数据
                    while (inputStream.available() == 0 && isRunning) {
                        if (System.currentTimeMillis() < 0)
                            break;
                    }
                    while (isRunning) {
                        try {
                            int num = inputStream.read(buffer);
                            byte[] temp = new byte[result.length + num];
                            System.arraycopy(result, 0, temp, 0, result.length);
                            System.arraycopy(buffer, 0, temp, result.length, num);
                            result = temp;
                            if (inputStream.available() == 0)
                                break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            onBluetoothAction.onConnectFailed("接收数据单次失败：" + e.getMessage());
                            break;
                        }
                    }
                    try {
                        // 返回数据
//                         LogUtils.logBlueTooth("当前累计收到的数据=>" + byte2Hex(result));

                        byte[] stopFlag = stopString.getBytes();
                        int stopFlagSize = stopFlag.length;
                        boolean shouldCallOnReceiveBytes = false;
                        if (TextUtils.isEmpty(stopString)) {
                            String s = byte2Hex(result);
                            if (s.length() > 6) {
                                String substring = s.substring(4, 6);
                                int i = Integer.parseInt(substring, 16);
//                                Log.i("TAG", "doInBackground: " + i + "---" + result.length);
                                if (result.length == i + 3) {
                                    shouldCallOnReceiveBytes = true;
                                }
                            } else if (s.length() == 0) {
                                shouldCallOnReceiveBytes = true;
                            }
                        } else {
                             LogUtils.logBlueTooth("标志位为：" + byte2Hex(stopFlag));
                            for (int i = stopFlagSize - 1; i >= 0; i--) {
                                int indexInResult = result.length - (stopFlagSize - i);
                                if (indexInResult >= result.length || indexInResult < 0) {
                                    shouldCallOnReceiveBytes = false;
                                     LogUtils.logBlueTooth("收到的数据比停止字符串短");
                                    break;
                                }
                                if (stopFlag[i] == result[indexInResult]) {
                                     LogUtils.logBlueTooth("发现" + byte2Hex(stopFlag[i]) + "等于" + byte2Hex(result[indexInResult]));
                                    shouldCallOnReceiveBytes = true;
                                } else {
                                     LogUtils.logBlueTooth("发现" + byte2Hex(stopFlag[i]) + "不等于" + byte2Hex(result[indexInResult]));
                                    shouldCallOnReceiveBytes = false;
                                }
                            }
                        }
                        if (shouldCallOnReceiveBytes) {

                            onBluetoothAction.onReceiveBytes(result);
                            // 清空
                            result = new byte[0];
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onBluetoothAction.onConnectFailed("验证收到数据结束标志出错：" + e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onBluetoothAction.onConnectFailed("接收数据失败：" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            try {
                 LogUtils.logBlueTooth("AsyncTask 开始释放资源");
                isRunning = false;
                if (null != bluetoothSocket) {
                    bluetoothSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 发送
         *
         * @param msg 内容
         */
        public void send(byte[] msg) {
            try {
                bluetoothSocket.getOutputStream().write(msg);
                onBluetoothAction.onSendBytes(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置停止标志位字符串
     *
     * @param stopString 停止位字符串
     */
    @SuppressWarnings("SameParameterValue")
    public void setStopString(String stopString) {
        mConnectTask.stopString = stopString;
    }

    /**
     * 蓝牙活动回调
     */
    public interface OnBluetoothAction {
        /**
         * 当发现新设备
         *
         * @param device 设备
         */
        void onFoundDevice(BluetoothDevice device);

        /**
         * 当连接成功
         */
        void onConnectSuccess(BluetoothDevice device);

        /**
         * 当连接成功
         */
        void onNewDeviceConnect(BluetoothDevice device);
        /**
         * 状态变化
         *
         * @param device
         */
        void onStateChanged(BluetoothDevice device);

        /**
         * 断开连接
         *
         * @param device
         */
        void onDisconnectedChanged(BluetoothDevice device);

        /**
         * 当连接失败
         *
         * @param msg 失败信息
         */
        void onConnectFailed(String msg);

        /**
         * 当接收到 byte 数组
         *
         * @param bytes 内容
         */
        void onReceiveBytes(byte[] bytes);

        /**
         * 当调用接口发送了 byte 数组
         *
         * @param bytes 内容
         */
        void onSendBytes(byte[] bytes);

        /**
         * 当结束搜索设备
         */
        void onFinishFoundDevice();
    }

    /**
     * 构造蓝牙工具
     *
     * @param context           上下文
     * @param onBluetoothAction 蓝牙状态改变回调
     */
    public BluetoothSPPUtil(Context context, OnBluetoothAction onBluetoothAction) {
        mContext = context;
        mOnBluetoothAction = onBluetoothAction;
    }

    /**
     * 初始化
     */
    public void onCreate() {
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        foundFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙开关状态
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//蓝牙开始搜索
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//蓝牙搜索结束

        foundFilter.addAction(BluetoothDevice.ACTION_FOUND);//蓝牙发现新设备(未配对的设备)
        foundFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);//在系统弹出配对框之前(确认/输入配对码)
        foundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//设备配对状态改变
        foundFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//最底层连接建立
        foundFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//最底层连接断开

        foundFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED); //BluetoothAdapter连接状态
        foundFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); //BluetoothHeadset连接状态
        foundFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED); //BluetoothA2dp连接状态
        mContext.registerReceiver(mReceiver, foundFilter);
    }

    /**
     * 销毁，释放资源
     */
    public void onDestroy() {
        try {
             LogUtils.logBlueTooth("onDestroy，开始释放资源");
            mConnectTask.isRunning = false;
            mConnectTask.cancel(true);
            //            mContext.unregisterReceiver(mReceiver);
            //            mContext.unregisterReceiver(mFinishFoundReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始搜索
     */
    void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 使用搜索到的数据连接
     *
     * @param device 设备
     */
    public void connect(BluetoothDevice device) {
        mBluetoothAdapter.cancelDiscovery();
        connect(device.getAddress());
    }

    /**
     * 使用Mac地址来连接
     *
     * @param deviceMac 要连接的设备的 MAC
     */
    private void connect(String deviceMac) {
        if (mConnectTask.getStatus() == AsyncTask.Status.RUNNING && mConnectTask.isRunning) {
            if (mOnBluetoothAction != null)
                mOnBluetoothAction.onConnectFailed("有正在连接的任务");
            return;
        }
        mConnectTask.onBluetoothAction = mOnBluetoothAction;
        try {
            mConnectTask.execute(deviceMac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送 byte 数组到串口
     *
     * @param bytes 要发送的数据
     */
    public void send(byte[] bytes) {
        if (mConnectTask != null)
            mConnectTask.send(bytes);
    }

    /**
     * 获取用户是否打开了蓝牙
     */
    boolean isBluetoothEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 开启蓝牙
     */
    void enableBluetooth() {
        mBluetoothAdapter.enable();
    }

    /**
     * 字节转换为 16 进制字符串
     *
     * @param b 字节
     * @return Hex 字符串
     */
    private static String byte2Hex(byte b) {
        StringBuilder hex = new StringBuilder(Integer.toHexString(b));
        if (hex.length() > 2) {
            hex = new StringBuilder(hex.substring(hex.length() - 2));
        }
        while (hex.length() < 2) {
            hex.insert(0, "0");
        }
        return hex.toString();
    }


    /**
     * 字节数组转换为 16 进制字符串
     *
     * @param bytes 字节数组
     * @return Hex 字符串
     */
    private static String byte2Hex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hash = formatter.toString();
        formatter.close();
        return hash;
    }

}
