package com.juplus.app.bt;

import android.bluetooth.BluetoothDevice;

public interface BTConnectListener {
    void onConnected(BluetoothDevice device);
    void onDisConnected();
}
