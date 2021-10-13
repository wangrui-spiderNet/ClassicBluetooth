package com.juplus.app.bluetooth.interfaces;

/**
 *@date 2019/7/23
 *@desc 蓝牙连接监听
 *
 */

public interface IBTMessageListener {
    void onReceive(byte[] data);
    void onSendSuccess();
    void onSendFail(Exception e);
}
