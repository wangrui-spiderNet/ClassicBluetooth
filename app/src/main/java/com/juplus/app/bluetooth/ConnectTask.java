package com.juplus.app.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import com.juplus.app.utils.BLESPPUtils;
import com.juplus.app.utils.BitUtils;
import com.juplus.app.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class ConnectTask extends AsyncTask<String, Byte[], Void> {

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket bluetoothSocket;
    BluetoothDevice romoteDevice;
    BLESPPUtils.OnBluetoothAction onBluetoothAction;
    boolean isRunning = false;
    String stopString = "\r\n";

    @Override
    protected Void doInBackground(String... bluetoothDevicesMac) {
        // 记录标志位，开始运行
        isRunning = true;

        // 尝试获取 bluetoothSocket
        try {
            LogUtils.logBlueTooth("尝试获取Socket");
            UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            romoteDevice = bluetoothAdapter.getRemoteDevice(bluetoothDevicesMac[0]);
//            bluetoothSocket = romoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);

            Method m = romoteDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            bluetoothSocket = (BluetoothSocket) m.invoke(romoteDevice, 1);
//            bluetoothSocket.connect();

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
                LogUtils.logBlueTooth("looping");
                byte[] buffer = new byte[256];
                // 等待有数据
                while (inputStream.available() == 0 && isRunning) {
                    if (System.currentTimeMillis() < 0) break;
                }
                while (isRunning) {
                    try {
                        int num = inputStream.read(buffer);
                        byte[] temp = new byte[result.length + num];
                        System.arraycopy(result, 0, temp, 0, result.length);
                        System.arraycopy(buffer, 0, temp, result.length, num);
                        result = temp;
                        if (inputStream.available() == 0) break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        onBluetoothAction.onConnectFailed("接收数据单次失败：" + e.getMessage());
                        break;
                    }
                }
                try {
                    // 返回数据
                    LogUtils.logBlueTooth("当前累计收到的数据=>" + BitUtils.byteArray2Hex(result));
                    byte[] stopFlag = stopString.getBytes();
                    int stopFlagSize = stopFlag.length;
                    boolean shouldCallOnReceiveBytes = false;
                    LogUtils.logBlueTooth("标志位为：" + BitUtils.byteArray2Hex(stopFlag));
                    for (int i = stopFlagSize - 1; i >= 0; i--) {
                        int indexInResult = result.length - (stopFlagSize - i);
                        if (indexInResult >= result.length || indexInResult < 0) {
                            shouldCallOnReceiveBytes = false;
                            LogUtils.logBlueTooth("收到的数据比停止字符串短");
                            break;
                        }
                        if (stopFlag[i] == result[indexInResult]) {
                            LogUtils.logBlueTooth("发现" +BitUtils. byteArray2Hex(stopFlag[i]) + "等于" + BitUtils.byteArray2Hex(result[indexInResult]));
                            shouldCallOnReceiveBytes = true;
                        } else {
                            LogUtils.logBlueTooth("发现" + BitUtils.byteArray2Hex(stopFlag[i]) + "不等于" + BitUtils.byteArray2Hex(result[indexInResult]));
                            shouldCallOnReceiveBytes = false;
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
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送
     *
     * @param msg 内容
     */
    void send(byte[] msg) {
        try {
            bluetoothSocket.getOutputStream().write(msg);
            onBluetoothAction.onSendBytes(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
