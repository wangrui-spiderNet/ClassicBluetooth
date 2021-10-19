package com.juplus.app;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.ReplacementTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.juplus.app.adapter.BleListAdapter;
import com.juplus.app.bt.BluetoothSPPUtil;
import com.juplus.app.bt.CMDConfig;
import com.juplus.app.entity.BluetoothModel;
import com.juplus.app.utils.ToastUtil;
import com.juplus.app.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements  BleListAdapter.OnItemClickListener, BluetoothSPPUtil.OnBluetoothAction {

    private String TAG = getClass().getSimpleName();

    private RecyclerView mRvBleList;
    private LinearLayout mLlBle;
    private List<BluetoothDevice> mBleList = new ArrayList<>();
    private BleListAdapter mBleListAdapter;
    private TextView mTvRefresh;
    private BluetoothSPPUtil mBluetoothSPPUtil;
    private String mKey2;
    private String mKeyData1;
    private String mKeyData2;
    private RelativeLayout mLlLoading, mMainView, mRlModify;
    private TextView mTvError;
    private TextView mTvTip;
    private ImageView mIvIcon;
    private Button mBtnSearch, mBtnSearch1;
    private ScrollView mLlSuccess;
    private TextView mProductNum;
    private TextView mBleDH;
    private TextView mSoftVer;
    private TextView mBleModelAdress;
    private TextView mSoftDate;
    private TextView mProducer;
    private BluetoothDevice mBluetoothDevice;
    private TextView mTvVer;
    private boolean isOpen = false;
    private Switch mSawtooth;
    private TextView mTvSN, mModify, mTvConfirm, mTvCancel, mTvBleName, mTvBleAddress;

    private boolean isWhat110 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRvBleList = (RecyclerView) findViewById(R.id.rv_ble_list);
        mMainView = (RelativeLayout) findViewById(R.id.main);
        mLlBle = (LinearLayout) findViewById(R.id.ll_ble);
        mLlSuccess = (ScrollView) findViewById(R.id.ll_success);
        mLlLoading = (RelativeLayout) findViewById(R.id.ll_loading);
        mTvRefresh = (TextView) findViewById(R.id.tv_refresh);
        mTvVer = (TextView) findViewById(R.id.tv_ver);
        mTvError = (TextView) findViewById(R.id.tv_error);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mIvIcon = (ImageView) findViewById(R.id.iv_icon);
        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mBtnSearch1 = (Button) findViewById(R.id.btn_search1);

        //成功展示
        mProductNum = (TextView) findViewById(R.id.tv_ble_productNumber);
        mBleDH = (TextView) findViewById(R.id.tv_ble_dh);
        mSoftVer = (TextView) findViewById(R.id.tv_ble_softwareVersion);
        mBleModelAdress = (TextView) findViewById(R.id.tv_ble_model_address);
        mSoftDate = (TextView) findViewById(R.id.tv_ble_softwareDate);
        mProducer = (TextView) findViewById(R.id.tv_ble_producer);

        //修改开关和序列号
        mRlModify = (RelativeLayout) findViewById(R.id.rl_modify);
        mSawtooth = findViewById(R.id.sawtooth);
        mTvSN = findViewById(R.id.tv_sn);
        mModify = findViewById(R.id.tv_modify);
        mTvConfirm = findViewById(R.id.tv_confirm);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvBleName = findViewById(R.id.tv_ble_name);
        mTvBleAddress = findViewById(R.id.tv_ble_address);
        mRlModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mSawtooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOpen = isChecked;
                mSawtooth.setChecked(isChecked);
            }
        });
        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出输入框
                setDialogEditTextSN();
            }
        });
        mTvSN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出输入框
                setDialogEditTextSN();
            }
        });

        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认修改
                StringBuffer cmd = new StringBuffer();
                cmd.append("C0");
                cmd.append("60");
                cmd.append("01");
                cmd.append(isOpen ? "01" : "00");
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(cmd.toString()));
                String sn = mTvSN.getText().toString().replace(" ", "");
                if (sn.length() != 12) {
                    showBleList();
                }
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBleList();
            }
        });
        mTvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBleList();
            }
        });
        mBtnSearch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWhat110 = false;
                showBleList();
            }
        });
        mBleListAdapter = new BleListAdapter(this, mBleList);
        mBleListAdapter.setOnItemClickListener(this);
        mRvBleList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRvBleList.setAdapter(mBleListAdapter);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            mTvVer.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mTvVer.setText("v2.0.0");
        }
        initData();
    }

    private void showBleList() {
        mLlLoading.setVisibility(View.GONE);
        mLlSuccess.setVisibility(View.GONE);
        mRlModify.setVisibility(View.GONE);
        mLlBle.setVisibility(View.VISIBLE);
//        mMainView.setBackground(getDrawable(R.mipmap.main_bg));
        BluetoothModel.getInstance().clear();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
        initData();
    }

    /**
     * 初始化已链接设备
     */
    private void initData() {
        getConnectBle();
    }

    /**
     * 蓝牙是否打开
     *
     * @return
     */
    private boolean isBlueEnable() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        return defaultAdapter != null && defaultAdapter.isEnabled();
    }

    /**
     * 初始化蓝牙
     */
    private void getConnectBle() {
        
        Observable.create(new ObservableOnSubscribe<BluetoothDevice>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BluetoothDevice> emitter) throws Throwable {
                // 初始化
                //获取已链接蓝牙设备
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (null == defaultAdapter) {
                    showToast("设备蓝牙无法使用");
                    emitter.onComplete();
                    return;
                }
                mBleList.clear();
                try {
                    //是否存在连接的蓝牙设备
                    Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
                    Iterator<BluetoothDevice> iterator = bondedDevices.iterator();
                    while (iterator.hasNext()) {
                        BluetoothDevice bluetoothDevice = iterator.next();
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        isConnectedMethod.setAccessible(true);
                        boolean isConnected = (boolean) isConnectedMethod.invoke(bluetoothDevice, (Object[]) null);
                        if (isConnected) {
                            emitter.onNext(bluetoothDevice);
                            //Log.i("BLUETOOTH", "connected:" + bluetoothDevice.getName());
                        }
                    }

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BluetoothDevice>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }


                    @Override
                    public void onNext(@NonNull BluetoothDevice integer) {
                        mBleList.add(integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showToast("您的手机系统版本不支持此验证方式");
                    }


                    @Override
                    public void onComplete() {
                        
                        //筛选完成，展示
                        mBleListAdapter.setNewData(mBleList);
                    }
                });
    }

    /**
     * 蓝牙点击，准备校验
     *
     * @param bleDevice
     * @param position
     */
    @Override
    public void onItemClickListener(BluetoothDevice bleDevice, int position) {
        
        if (null != mBluetoothSPPUtil) {
            mBluetoothSPPUtil.onDestroy();
        }
        mBluetoothDevice = bleDevice;
        boolean isConnected = false;
        try {
            Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
            isConnectedMethod.setAccessible(true);
            isConnected = (boolean) isConnectedMethod.invoke(mBluetoothDevice, (Object[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (isConnected) {
            mTvBleName.setText(mBluetoothDevice.getName() + "");
            mTvBleAddress.setText(mBluetoothDevice.getAddress() + "");
            //        BluetoothSPPUtil.setEnableLogOut();
            mBluetoothSPPUtil = new BluetoothSPPUtil(this, this);
            // 设置接收停止标志位字符串
            mBluetoothSPPUtil.setStopString("");
            mBluetoothSPPUtil.connect(bleDevice);
        } else {
            showToast("设备已断开");
            initData();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 110) {
            if (isBlueEnable()) {
                getConnectBle();
            } else {
                showToast("蓝牙未打开");
            }
        }
    }

    private void showToast(String context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, context, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 当发现新设备
     *
     * @param device 设备
     */
    @Override
    public void onFoundDevice(BluetoothDevice device) {
        //不需要，因为不扫描
    }

    /**
     * 当连接成功
     *
     * @param device
     */
    @Override
    public void onConnectSuccess(BluetoothDevice device) {
        mBluetoothDevice = device;
        BluetoothModel.getInstance().setBluetoothName(device.getName());
        //链接成功开始写数据
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                
                mLlBle.setVisibility(View.GONE);
                showLoading();
                byte[] handshakeCmd = CMDConfig.getHandshakeCmd();
                mBluetoothSPPUtil.send(handshakeCmd);
            }
        });

    }

    private void showLoading() {
        mLlLoading.setVisibility(View.VISIBLE);
        mIvIcon.setBackgroundResource(R.drawable.bg_frame_add_nomal);
        mTvError.setVisibility(View.GONE);
        mTvTip.setText("正在获取信息...");
    }

    private void showError() {
        mLlLoading.setVisibility(View.VISIBLE);
//        mIvIcon.setBackgroundResource(R.drawable.bg_ble_error);
//        mMainView.setBackground(getDrawable(R.drawable.error_bg));
        mTvError.setVisibility(View.VISIBLE);
        mTvError.setText("验证失败");
        mTvError.setTextColor(getResources().getColor(R.color.white));
        mTvTip.setText("您的产品使用的不是");
        mTvTip.setTextColor(getResources().getColor(R.color.white));
    }

    private void showSuccess1() {
        mLlLoading.setVisibility(View.GONE);
        mLlSuccess.setVisibility(View.VISIBLE);
//        mMainView.setBackground(getDrawable(R.drawable.success1_bg));
        mProductNum.setText(BluetoothModel.getInstance().getProductNumber());
        mBleDH.setText(BluetoothModel.getInstance().getDh());
        mBleModelAdress.setText(BluetoothModel.getInstance().getBluetoothAddress());
        mSoftDate.setText(BluetoothModel.getInstance().getSoftwareDate());
        mSoftVer.setText(BluetoothModel.getInstance().getSoftwareVersion());
        mProducer.setText(BluetoothModel.getInstance().getProducer());
    }

    private void showSuccess2() {
        mLlLoading.setVisibility(View.VISIBLE);
//        mMainView.setBackground(getDrawable(R.drawable.success2_bg));
//        mIvIcon.setBackgroundResource(R.drawable.bg_ble_success2);
        mTvError.setVisibility(View.GONE);
        mTvTip.setText("当前软件版本不支持");
//        mTvTip.setTextColor(getResources().getColor(R.color.error));
    }

    private void showSuccess3() {
        mLlLoading.setVisibility(View.VISIBLE);
//        mIvIcon.setBackgroundResource(R.drawable.bg_ble_success3);
        mTvError.setVisibility(View.VISIBLE);
        mTvError.setText("验证成功");
        mTvError.setTextColor(getResources().getColor(R.color.white));
        mTvTip.setText("您的产品使用的是");
        mTvTip.setTextColor(getResources().getColor(R.color.white));
    }

    private void showSuccess4() {
        mRlModify.setVisibility(View.VISIBLE);
    }

    /**
     * 当连接失败
     *
     * @param msg 失败信息
     */
    @Override
    public void onConnectFailed(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                
                mLlBle.setVisibility(View.GONE);
                showSuccess2();
            }
        });
    }

    /**
     * 当接收到 byte 数组
     *
     * @param bytes 内容
     */
    @Override
    public void onReceiveBytes(byte[] bytes) {
        String s = Utils.bytesToHexString(bytes);
        if (TextUtils.isEmpty(s)) {
            return;
        }
        //if (s.length() < 4) {
        //    //这里可能需要使用Macaddress来校验
        //    showSuccess2();
        //    return;
        //}
        String code = s.substring(2, 4);
        switch (code) {
            case "01":
                //握手响应
                String substring = s.substring(6, 10);
                if (substring.equalsIgnoreCase("534C")) {
                    String[] verificationCommand = CMDConfig.getVerificationCommand();
                    mKeyData1 = verificationCommand[3];
                    mKeyData2 = verificationCommand[4];
                    mKey2 = Utils.getTheAccumulatedValueAnd(verificationCommand[2]);

                    Log.i(TAG, "onReceiveBytes:mKeyData1 " + mKeyData1);
                    Log.i(TAG, "onReceiveBytes:mKeyData2 " + mKeyData2);
                    Log.i(TAG, "onReceiveBytes:Key2 " + mKey2);

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(verificationCommand[0]);
                    stringBuffer.append(verificationCommand[1]);
                    stringBuffer.append(verificationCommand[2]);
                    stringBuffer.append(verificationCommand[3]);

                    Log.i(TAG, "onReceiveBytes:最终命令 " + stringBuffer.toString());
                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray(stringBuffer.toString()));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showError();
                        }
                    });
                }
                break;
            case "02":
                //校验响应
                Log.i(TAG, "校验响应:原始数据 " + s );
                Log.i(TAG, "校验响应:mKeyData1 " + mKeyData1);
                Log.i(TAG, "校验响应:mKeyData2 " + mKeyData2);
                Log.i(TAG, "校验响应:Key2 " + mKey2);

                boolean b = Utils.verificationCmd(s, mKey2, mKeyData1, mKeyData2);
                if (b) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast("校验成功");
                        }
                    });

                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C003"));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showError();
                        }
                    });
                }
                break;
            case "03":
                isWhat110=true;
                //芯片型号响应
                String substring1 = s.substring(6, s.length() - 2);
                String s1 = Utils.hexStringToString(substring1);
                //                Log.i(TAG, "onReceiveBytes: " + s1);
                //                mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C004"));
                BluetoothModel.getInstance().setDh(s1);

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C050"));
                if ("545753313036".equalsIgnoreCase(substring1)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2 * 1000);  //线程休眠10秒执行
                                handler.sendEmptyMessage(666);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C004"));
//                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C050"));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSuccess2();
                        }
                    });
                }
                break;
            case "04":
                //产品型号响应
                String substring2 = s.substring(6, s.length() - 2);
                String s2 = Utils.hexStringToString(substring2);
                //                Log.i(TAG, "onReceiveBytes: " + s2);
                BluetoothModel.getInstance().setProductNumber(s2);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C005"));
                break;
            case "05":
                //软件版本响应
                String substring3 = s.substring(6, s.length() - 2);
                String s3 = Utils.hexStringToString(substring3);
                //                Log.i(TAG, "onReceiveBytes: " + s3);
                BluetoothModel.getInstance().setSoftwareVersion(s3);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C006"));
                break;
            case "06":
                //蓝牙地址响应
                String substring4 = s.substring(6);
                int len = substring4.length();
                int num = 0;
                StringBuffer str = new StringBuffer();
                while (num < len) {
                    String substring5 = substring4.substring(num, num + 2);
                    str.append(substring5);
                    if (num != len - 2) {
                        str.append(":");
                    }
                    num = num + 2;
                }
                BluetoothModel.getInstance().setBluetoothAddress(str.toString());
                //                Log.i(TAG, "onReceiveBytes:  " + str.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSuccess1();
                    }
                });
                break;
            case "50":
                isWhat110= false;
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C051"));
                String content50 = s.substring(6, 8);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content50.equals("01")) {
                            isOpen = true;
                            mSawtooth.setChecked(true);
                        } else if (content50.equals("00")) {
                            isOpen = false;
                            mSawtooth.setChecked(false);
                        }
                        logD(content50);
                    }
                });

                break;
            case "51":
                String SNText = s.substring(6, s.length());
                for (int i = 0; i < SNText.length(); i++) {
                    String itemSn = SNText.charAt(i) + "";
                    if (itemSn.equals("0") || itemSn.equals("F") || itemSn.equals("f")) {

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String SN = Utils.hexStringToString(SNText);
                                logD(SN);
                                StringBuffer stringBuffer = new StringBuffer();
                                try {
                                    for (int a = 1; a < 4; a++) {
                                        stringBuffer.append(SN.substring(a == 1 ? 0 : (a - 1) * 4, a == 4 ? SN.length() : a * 4) + " ");
                                    }
                                    mTvSN.setText(stringBuffer.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mTvSN.setText("");
                                }

                                showSuccess4();
                            }
                        });
                        return;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvSN.setText("");
                        showSuccess4();
                    }
                });
                break;
            case "60":
                boolean isSN_0 = false;
                String sn = mTvSN.getText().toString().replace(" ", "");
                if (sn.length() == 12) {
                    for (int i = 0; i < 12; i++) {
                        String snIndex = sn.charAt(i) + "";
                        if (snIndex.equals("0")) {
                            isSN_0 = true;
                        } else {
                            isSN_0 = false;
                        }
                    }
                    if (isSN_0) {
                        //确认修改
                        StringBuffer cmd_sn = new StringBuffer();
                        cmd_sn.append("C0");
                        cmd_sn.append("61");
                        cmd_sn.append("0C");
                        //cmd_sn.append(Utils.stringToHexString(sn));
                        cmd_sn.append("000000000000000000000000");
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(cmd_sn.toString()));
                    } else {
                        //确认修改
                        StringBuffer cmd_sn = new StringBuffer();
                        cmd_sn.append("C0");
                        cmd_sn.append("61");
                        cmd_sn.append("0C");
                        cmd_sn.append(Utils.stringToHexString(sn));
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(cmd_sn.toString()));
                    }
                }
                break;
            case "61":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showBleList();
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 当调用接口发送了 byte 数组
     *
     * @param bytes 内容
     */
    @Override
    public void onSendBytes(byte[] bytes) {
        //Log.i(TAG, "onSendBytes: " + Utils.bytesToHexString(bytes));
    }

    /**
     * 当结束搜索设备
     */
    @Override
    public void onFinishFoundDevice() {
        //不需要
    }

    /**
     * 打印日志
     */
    private static void logD(String msg) {
        //Log.d(MainActivity.class.getSimpleName(), msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 666 && isWhat110) {
                showSuccess2();
            }
        }
    };

    private void setDialogEditTextSN() {
        final EditText inputSN = new EditText(this);
        inputSN.setHint("请输入序列号");
        String regular = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        inputSN.setTransformationMethod(new AllCapTransformationMethod());
        inputSN.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        inputSN.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] ac = regular.toCharArray();
                return ac;
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 40;
        params.weight = 1;
        inputSN.setLayoutParams(params);
        TextView tvTextNumber = new TextView(this);
        inputSN.setLetterSpacing(0.1f);
        inputSN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (12 - s.toString().length() == 0) {
                    tvTextNumber.setText("");
                } else {
                    tvTextNumber.setText("缺少" + (12 - s.toString().length()) + "个字符");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout2.addView(inputSN);
        linearLayout2.addView(tvTextNumber);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayout2);
        TextView textView = new TextView(this);
        textView.setText("      * 序列号为12个字符，0-9、A-Z、a-z");
        linearLayout.addView(textView);
        builder.setTitle("序列号").setView(linearLayout)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Field field = dialog.getClass()
                                    .getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            // 将mShowing变量设为false，表示对话框已关闭
                            field.set(dialog, true);
                            dialog.dismiss();

                        } catch (Exception e) {

                        }
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String sn = inputSN.getText().toString();

                if (sn.length() == 12) {
                    StringBuffer stringBuffer = new StringBuffer();

                    try {
                        for (int a = 1; a < 4; a++) {
                            stringBuffer.append(sn.substring(a == 1 ? 0 : (a - 1) * 4, a == 4 ? sn.length() : a * 4) + " ");
                        }
                        mTvSN.setText(stringBuffer.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        mTvSN.setText("");
                    }

                    mTvSN.setText(stringBuffer.toString().toUpperCase());
                    logD(mTvSN.getText().toString());
                    try {
                        Field field = dialog.getClass()
                                .getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        // 将mShowing变量设为false，表示对话框已关闭
                        field.set(dialog, true);
                        dialog.dismiss();

                    } catch (Exception e) {

                    }
                } else {
                    try {
                        Field field = dialog.getClass()
                                .getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        // 将mShowing变量设为false，表示对话框已关闭
                        field.set(dialog, false);
                        dialog.dismiss();

                    } catch (Exception e) {

                    }
                    //doHuWeiToastCovered(mTvSN);
                }


            }
        });
        builder.show();
    }

    private class AllCapTransformationMethod extends ReplacementTransformationMethod {
        @Override
        protected char[] getOriginal() {
            char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] cc = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
            return cc;
        }

    }

    private void doHuWeiToastCovered(TextView mTvSN) {
        if (Build.MANUFACTURER.equalsIgnoreCase("huawei")) {
            View focus = getWindow().getDecorView().findFocus();
            if (focus instanceof EditText) {
                EditText et = (EditText) focus;
                if ((et.getInputType() == (InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT))
                        || (et.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                    InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    methodManager.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } else {
            Toast.makeText(getBaseContext(), "输入不少于12个字", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBluetoothSPPUtil) {
            mBluetoothSPPUtil.onDestroy();
        }
    }
}