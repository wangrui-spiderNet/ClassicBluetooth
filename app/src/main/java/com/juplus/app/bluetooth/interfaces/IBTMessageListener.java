package com.juplus.app.bluetooth.interfaces;

/**
 *@date 2019/7/23
 *@desc 蓝牙连接监听
 *
 */

public interface IBTMessageListener {
    void onRead(byte[] data);
    void onWrite(byte[] data);
    void onConnectFail();
    void onSendSuccess();
    void onMessageFail(String e);
}
