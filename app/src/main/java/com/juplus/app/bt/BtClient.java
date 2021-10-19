package com.juplus.app.bt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.juplus.app.utils.ThreadUtil;
import com.juplus.app.utils.Utils;


/**
 * 客户端，与服务端建立长连接
 */
@SuppressLint("MissingPermission")
public class BtClient extends BtBase {

    private BtClient(BTConnectListener connectListener,BTByteListener byteListener) {
        super(connectListener,byteListener);
    }

    private static BtClient mBtClient;

    public static BtClient getInstance(BTConnectListener connectListener,BTByteListener byteListener){
        if(mBtClient==null){
            mBtClient = new BtClient(connectListener,byteListener);
        }

        return mBtClient;
    }

    /**
     * 与远端设备建立长连接
     *
     * @param dev 远端设备
     */
    public void connect(BluetoothDevice dev) {
        close();
        try {
//             final BluetoothSocket socket = dev.createRfcommSocketToServiceRecord(SPP_UUID); //加密传输，Android系统强制配对，弹窗显示配对码
            final BluetoothSocket socket = dev.createInsecureRfcommSocketToServiceRecord(SPP_UUID); //明文传输(不安全)，无需配对
            // 开启子线程
            ThreadUtil.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    loopRead(socket); //循环读取
                }
            });
        } catch (Throwable e) {
            close();
        }
    }

    public void sharkHands(){
        byte[] handshakeCmd = Utils.getHandshakeCmd();
        sendByte(handshakeCmd);
    }

    private String mKeyData1, mKeyData2, mKey2;

    /**
     * 开始验证
     */
    public void startVerify() {
        String[] verificationCommand = Utils.getVerificationCommand();
        mKeyData1 = verificationCommand[3];
        mKeyData2 = verificationCommand[4];
        mKey2 = Utils.getTheAccumulatedValueAnd(verificationCommand[2]);
        //                    Log.i(TAG, "onReceiveBytes:Key2 " + mKey2);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(verificationCommand[0]);
        stringBuffer.append(verificationCommand[1]);
        stringBuffer.append(verificationCommand[2]);
        stringBuffer.append(verificationCommand[3]);
        sendByte(Utils.hexStringToByteArray(stringBuffer.toString()));
    }

    /**
     * 解密并验证数据
     *
     * @param data
     * @return
     */
    public boolean verificationCmd(String data) {

        String substring = data.substring(32, 96);
        //        Log.i("TAG", "verificationCmd: 30-3F： " + oldData);
        //        Log.i("TAG", "verificationCmd: 10-2F： " + old2Data);
        //        Log.i("TAG", "verificationCmd: new 10-2F： " + substring);
        byte[] bytes =Utils.hexStringToByteArray(substring);
        int i = Integer.parseInt(mKey2, 16);
        //解密数据
        String s2 = Utils.bytesToHexString(Utils.encryptData(i, bytes, bytes.length));
        //        Log.i("TAG", "verificationCmd:解密后的10-2F： " + s2);
        //异或
        String substring1 = s2.substring(0, 32);
        String substring2 = s2.substring(32);
        byte[] bytes1 = Utils.hexStringToByteArray(substring1);
        byte[] bytes2 = Utils.hexStringToByteArray(substring2);
        byte[] bytes3 = Utils.hexStringToByteArray(mKeyData1);
        byte[] bytes4 = new byte[bytes1.length + bytes2.length];
        for (int j = 0; j < bytes1.length; j++) {
            bytes4[j] = (byte) (bytes1[j] ^ bytes3[j]);
        }
        for (int j = 0; j < bytes2.length; j++) {
            bytes4[j + bytes2.length] = (byte) (bytes2[j] ^ bytes3[j]);
        }
        //        String s3 = bytesToHexString(encryptData(i, bytes4, bytes4.length));
        //        Log.i("TAG", "verificationCmd:异或后的数据： " + bytesToHexString(bytes4));
        return Utils.bytesToHexString(bytes4).equalsIgnoreCase(mKeyData2);
    }

}