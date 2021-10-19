package com.juplus.app.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.juplus.app.APP;
import com.juplus.app.utils.LogUtils;
import com.juplus.app.utils.ToastUtil;
import com.juplus.app.utils.ThreadUtil;
import com.juplus.app.utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * 客户端和服务端的基类，用于管理socket长连接
 */
public class BtBase {
    static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String FILE_PATH = APP.getInstance().filePath() + "/bluetooth/";
    private static final int FLAG_MSG = 0;  //消息标记
    private static final int FLAG_FILE = 1; //文件标记
    private static final int FLAG_BYTE = 2; //文件标记

    public static final int DISCONNECTED = 0;
    public static final  int CONNECTED = 1;
    public static final  int MSG = 2;

    private BluetoothSocket mSocket;
    private DataOutputStream mDataOut;
    private OutputStream mStreamOut;
    private boolean isRead;
    private boolean isSending;

    private BTByteListener byteListener;
    private BTFileListener fileListener;
    private BTMsgListener msgListener;
    private BTConnectListener connectListener;

    BtBase(BTConnectListener connectListener,BTByteListener byteListener) {
        this.byteListener = byteListener;
        this.connectListener = connectListener;
    }

    public void setByteListener(BTByteListener byteListener) {
        this.byteListener = byteListener;
    }

    public void setMsgListener(BTMsgListener msgListener) {
        this.msgListener = msgListener;
    }

    public void setFileListener(BTFileListener fileListener) {
        this.fileListener = fileListener;
    }

    /**
     * 循环读取对方数据(若没有数据，则阻塞等待)
     */
    void loopRead(BluetoothSocket socket) {
        mSocket = socket;
        try {
            if (!mSocket.isConnected())
                mSocket.connect();

            if (connectListener != null){
                connectListener.onConnected(socket.getRemoteDevice());
            }

            mDataOut = new DataOutputStream(mSocket.getOutputStream());
            mStreamOut = mSocket.getOutputStream();
            InputStream mInputStream = mSocket.getInputStream();
            DataInputStream in = new DataInputStream(mSocket.getInputStream());
            isRead = true;

            while (isRead) { //死循环读取

                byte[] bytes2=new byte[128];
                mInputStream.read(bytes2);
                receiveUpdateUIFromByte(MSG, bytes2);

                switch (in.readInt()) {
                    case FLAG_MSG: //读取短消息
                        String msg = in.readUTF();
                        receiveUpdateUIFromMsg(MSG,  msg);
                        break;
                    case FLAG_FILE: //读取文件
                        ThreadUtil.mkdirs(FILE_PATH);
                        String fileName = in.readUTF(); //文件名
                        long fileLen = in.readLong(); //文件长度
                        // 读取文件内容
                        long len = 0;
                        int r;
                        byte[] b = new byte[4 * 1024];
                        FileOutputStream out = new FileOutputStream(FILE_PATH + fileName);

                        while ((r = in.read(b)) != -1) {
                            out.write(b, 0, r);
                            len += r;
                            if (len >= fileLen)
                                break;
                        }

                        receiveUpdateUIFromFile(MSG,  fileName );

                        break;
                }


            }
        } catch (Throwable e) {
            showErrorMsg("连接时出错:"+e.getMessage());
            close();
        }
    }

    /**
     * 发送短消息
     */
    public void sendMsg(String msg) {
        if (checkSend()) return;
        isSending = true;
        try {
            mDataOut.writeInt(FLAG_MSG); //消息标记
            mDataOut.writeUTF(msg);
            mDataOut.flush();
        } catch (Throwable e) {
            close();
        }
        isSending = false;
    }

    /**
     * 发送字节数组
     */
    public void sendByte(byte[] bytes) {
        if (checkSend()) return;
        isSending = true;
        try {
//            mStreamOut.write(FLAG_BYTE); //消息标记
            LogUtils.logBlueTooth("发送消息:"+ Utils.bytesToHexString(bytes));
            LogUtils.logBlueTooth("发送消息:"+ Arrays.toString(bytes));
            mStreamOut.write(bytes);
            mStreamOut.flush();

        } catch (Throwable e) {
            showErrorMsg("发送字节的时候出错:"+e.getMessage());
            close();
        }
        isSending = false;
    }

    /**
     * 发送文件
     */
    public void sendFile(final String filePath) {
        if (checkSend()) return;
        isSending = true;
        ThreadUtil.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream in = new FileInputStream(filePath);
                    File file = new File(filePath);
                    mDataOut.writeInt(FLAG_FILE); //文件标记
                    mDataOut.writeUTF(file.getName()); //文件名
                    mDataOut.writeLong(file.length()); //文件长度
                    int r;
                    byte[] b = new byte[4 * 1024];
                    while ((r = in.read(b)) != -1)
                        mDataOut.write(b, 0, r);
                    mDataOut.flush();
                } catch (Throwable e) {
                    close();
                }
                isSending = false;
            }
        });
    }

    /**
     * 释放监听引用(例如释放对Activity引用，避免内存泄漏)
     */
    public void unListener() {
        byteListener = null;
        msgListener = null;
        fileListener = null;
    }

    /**
     * 关闭Socket连接
     */
    public void close() {
        try {
            isRead = false;
            if(null !=mSocket){
                mSocket.close();
            }
            receiveUpdateUIDisConnected();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前设备与指定设备是否连接
     */
    public boolean isConnected(BluetoothDevice dev) {
        boolean connected = (mSocket != null && mSocket.isConnected());
        if (dev == null)
            return connected;
        return connected && mSocket.getRemoteDevice().equals(dev);
    }

    // ============================================通知UI===========================================================
    private boolean checkSend() {
        if (isSending) {
            APP.toast("正在发送其它数据,请稍后再发...", 0);
            return true;
        }
        return false;
    }

    private void showErrorMsg(String msg){
        APP.runUi(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(msg);
            }
        });
    }

    private void receiveUpdateUIDConnected(BluetoothDevice device) {
        APP.runUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if (connectListener != null)
                        connectListener.onConnected(device);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void receiveUpdateUIDisConnected() {
        APP.runUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if (connectListener != null)
                        connectListener.onDisConnected();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void receiveUpdateUIFromMsg(final int state, final String obj) {
        APP.runUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if (msgListener != null)
                        msgListener.onReceiveMsg(state, obj);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void receiveUpdateUIFromFile(final int state, final Object filePath) {
        APP.runUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fileListener != null)
                        fileListener.onReceiveFile(state, filePath);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void receiveUpdateUIFromByte(final int state, final byte[] obj) {
        APP.runUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if (byteListener != null)
                        byteListener.onReceiveByte(state, obj);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
