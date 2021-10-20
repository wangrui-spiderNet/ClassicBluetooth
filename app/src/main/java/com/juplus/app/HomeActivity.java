package com.juplus.app;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.juplus.app.adapter.BtDeviceAdapter;
import com.juplus.app.bt.BluetoothSPPUtil;
import com.juplus.app.bt.CMDConfig;
import com.juplus.app.entity.BluetoothModel;
import com.juplus.app.entity.DeviceBean;
import com.juplus.app.entity.SettingBean;
import com.juplus.app.utils.AssetUtil;
import com.juplus.app.utils.LogUtils;
import com.juplus.app.utils.SystemUtil;
import com.juplus.app.utils.ToastUtil;
import com.juplus.app.utils.Utils;
import com.juplus.app.widget.CallBack;
import com.juplus.app.widget.ChangeNameDialog;
import com.juplus.app.widget.DeviceListDialog;
import com.juplus.app.widget.SettingActionListDialog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("MissingPermission")
public class HomeActivity extends AppCompatActivity implements BluetoothSPPUtil.OnBluetoothAction {

    @BindView(R.id.tv_title_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_left)
    TextView tvLeftBattery;
    @BindView(R.id.tv_right)
    TextView tvRightBattery;
    @BindView(R.id.tv_charge_box)
    TextView tvChargeBox;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.check_ear)
    Switch checkEar;
    @BindView(R.id.check_smart_wake)
    Switch checkSmart;
    @BindView(R.id.tv_audio_type)
    TextView tvAudioType;
    @BindView(R.id.tv_left_setting)
    TextView tvLeftSetting;
    @BindView(R.id.tv_right_setting)
    TextView tvRightSetting;
    @BindView(R.id.tv_new_version)
    TextView tvNewVersion;
    @BindView(R.id.tv_mac_address)
    TextView tvMacAddress;
    @BindView(R.id.rgNoiseManager)
    RadioGroup rgNoiseManager;
    @BindView(R.id.rbLowNoise)
    RadioButton rbLowNoise;
    @BindView(R.id.rbCloseNoise)
    RadioButton rbCloseNoise;
    @BindView(R.id.rbVentilateNoise)
    RadioButton rbVentilateNoise;

    private DeviceListDialog mDeviceListDialog;
    private ChangeNameDialog mChangeNameDialog;
    private SettingActionListDialog leftDoubleSettingDialog, leftLongSettingDialog, audioSettingDialog;
    private List<SettingBean> leftSettingBeans, audioSettingBeans, leftEarLongSettingBeans;

    private List<DeviceBean> mDeviceList = new ArrayList<>();
    private BluetoothSPPUtil mBluetoothSPPUtil;
    private String mKey2;
    private String mKeyData1;
    private String mKeyData2;
    private BluetoothDevice mBluetoothDevice;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        gson = new Gson();

        String json_double_ear_setting = AssetUtil.getJsonFromAsset(APP.getInstance(), "setting_ear_double_array.json");
        String json_long_ear_setting = AssetUtil.getJsonFromAsset(APP.getInstance(), "setting_ear_long_array.json");
        String setting_audio_array = AssetUtil.getJsonFromAsset(APP.getInstance(), "setting_audio_array.json");

        leftSettingBeans = gson.fromJson(json_double_ear_setting, new TypeToken<List<SettingBean>>() {
        }.getType());
        leftEarLongSettingBeans = gson.fromJson(json_long_ear_setting, new TypeToken<List<SettingBean>>() {
        }.getType());
        audioSettingBeans = gson.fromJson(setting_audio_array, new TypeToken<List<SettingBean>>() {
        }.getType());

        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .statusBarColor(R.color.color_f6f7f9)
                .fitsSystemWindows(true)
                .init();

        checkSmart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!hasDevice()) return;

                if (b) {
                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_AUTO_CHECK_ON));
                } else {
                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_AUTO_CHECK_OFF));
                }

            }
        });

        checkEar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!hasDevice()) return;

                if (b) {
                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_VOICE_WAKE_ON));
                } else {
                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_VOICE_WAKE_OFF));
                }
            }
        });

        rgNoiseManager.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                rbLowNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_low_noise_normal, 0, 0);
                rbCloseNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_close_noise_normal, 0, 0);
                rbVentilateNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_ventilate_normal, 0, 0);

                switch (i) {
                    case R.id.rbLowNoise:
                        rbLowNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_low_noise_checked, 0, 0);
                        if (!hasDevice()) return;
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_NOISE_CONTROL + CMDConfig.CMD_02));
                        break;

                    case R.id.rbCloseNoise:
                        rbCloseNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_close_noise_checked, 0, 0);
                        if (!hasDevice()) return;
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_NOISE_CONTROL + CMDConfig.CMD_01));
                        break;

                    case R.id.rbVentilateNoise:
                        rbVentilateNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_ventilate_checked, 0, 0);
                        if (!hasDevice()) return;
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_NOISE_CONTROL + CMDConfig.CMD_03));

                        break;
                }
            }
        });
        initData();
    }

    private boolean hasDevice() {

        boolean hasNo = mBluetoothSPPUtil != null && mBluetoothDevice != null;

        if (!hasNo) {
            ToastUtil.showToast("没有连接耳机设备");
        }

        return hasNo;
    }

    @SuppressLint("StringFormatMatches")
    @Optional
    @OnClick({R.id.tv_title_name, R.id.tv_left_setting, R.id.tv_audio_type, R.id.tv_right_setting, R.id.tv_new_version, R.id.tv_name})
    public void onViewClicked(View view) {
        if (SystemUtil.isFastClick()) {
            return;
        }

        switch (view.getId()) {
            case R.id.tv_title_name:

                showDeviceList();

                break;

            case R.id.tv_name:

                mChangeNameDialog = new ChangeNameDialog(this, tvName.getText().toString(), new CallBack<String>() {
                    @Override
                    public void callBack(String o) {

                        tvDeviceName.setText(o);
                        tvName.setText(o);

                        if (!hasDevice()) return;
                        int length = o.length();
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_BT_NAME + length + o));
                    }
                });
                mChangeNameDialog.show();

                break;

            case R.id.tv_audio_type:
                audioSettingDialog = new SettingActionListDialog(this, "", audioSettingBeans, new CallBack<SettingBean>() {
                    @Override
                    public void callBack(SettingBean o) {
                        if (!hasDevice()) return;
                        tvAudioType.setText(o.name);
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_AUDIO_TYPE + o.code));
                    }
                });
                audioSettingDialog.show();

                break;

            case R.id.tv_left_setting:

                leftDoubleSettingDialog = new SettingActionListDialog(this, "左耳 轻按2次", leftSettingBeans, new CallBack<SettingBean>() {
                    @Override
                    public void callBack(SettingBean o) {
                        if (!hasDevice()) return;
                        tvLeftSetting.setText(o.name);
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_DOUBLE_CLICK_LEFT + o.code));
                    }
                });

                leftDoubleSettingDialog.show();
                break;

            case R.id.tv_right_setting:
                leftLongSettingDialog = new SettingActionListDialog(this, "右耳 长按", leftEarLongSettingBeans, new CallBack<SettingBean>() {
                    @Override
                    public void callBack(SettingBean o) {
                        if (!hasDevice()) return;
                        tvRightSetting.setText(o.name);
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_LONG_PRESS_RIGHT + o.code));
                    }
                });

                leftLongSettingDialog.show();
                break;

            case R.id.tv_new_version:
                toSettingMore();
                break;

            default:
                break;

        }
    }

    private void toSettingMore() {
        Intent intent = new Intent(this, SettingMoreActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.picture_anim_enter,R.anim.picture_anim_exit);
    }

    private void showBleList() {
        BluetoothModel.getInstance().clear();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.logBlueTooth("onRestart: ");
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

    private DeviceBean createBluetoothItem(BluetoothDevice device) {
        DeviceBean deviceBean = new DeviceBean();
        deviceBean.setBluetoothDevice(device);
        return deviceBean;
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
                mDeviceList.clear();
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
                    public void onNext(@NonNull BluetoothDevice device) {

                        mDeviceList.add(createBluetoothItem(device));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showToast("您的手机系统版本不支持此验证方式");
                    }


                    @Override
                    public void onComplete() {

                        showDeviceList();
                    }
                });
    }

    private void updateDeviceAdapter() {
        if (mDeviceListDialog != null && mDeviceListDialog.isShowing() && mDeviceListDialog.getPairedAdapter() != null) {
            mDeviceListDialog.updateData(mDeviceList);
        }
    }

    private void showDeviceList() {
        if (mDeviceListDialog != null) {
            mDeviceListDialog.dismiss();
            mDeviceListDialog = null;
        }

        mBluetoothSPPUtil = new BluetoothSPPUtil(this, this);
        // 设置接收停止标志位字符串
        mBluetoothSPPUtil.setStopString("");
        mBluetoothSPPUtil.onCreate();

        mDeviceListDialog = new DeviceListDialog(HomeActivity.this, mDeviceList, new BtDeviceAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(DeviceBean deviceBean) {

                setDeviceInfo(deviceBean);
                connectDevice(deviceBean.getBluetoothDevice());

            }
        });
        mDeviceListDialog.show();
    }

    private void setDeviceInfo(DeviceBean deviceBean) {

        if (deviceBean != null) {
            tvDeviceName.setText(deviceBean.getBluetoothDevice().getName());
            tvName.setText(deviceBean.getBluetoothDevice().getName());
            tvMacAddress.setText("蓝牙地址：" + deviceBean.getBluetoothDevice().getAddress());
        } else {
            tvDeviceName.setText("未连接");
            tvName.setText("未连接");
            tvMacAddress.setText("未连接设备");
        }

    }

    private void connectDevice(BluetoothDevice bleDevice) {
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
            //        BluetoothSPPUtil.setEnableLogOut();

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

    private void showToast(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(msg);
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

    @Override
    public void onStateChanged(BluetoothDevice device) {
        ToastUtil.showToast("状态变化：" + device.getName());
    }

    @Override
    public void onDisconnectedChanged(BluetoothDevice device) {
        for (DeviceBean deviceBean : mDeviceList) {
            if (deviceBean.getBluetoothDevice().getName().equals(device.getName())
                    && deviceBean.getBluetoothDevice().getAddress().equals(device.getAddress())) {
                mDeviceList.remove(deviceBean);
                mBluetoothDevice = null;
                setDeviceInfo(null);
                break;
            }
        }

        updateDeviceAdapter();

        ToastUtil.showToast("断开连接：" + device.getName());
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
                byte[] handshakeCmd = CMDConfig.getHandshakeCmd();
                mBluetoothSPPUtil.send(handshakeCmd);
            }
        });
    }

    @Override
    public void onNewDeviceConnect(BluetoothDevice device) {
        ToastUtil.showToast("新连上一个设备：" + device.getName() + device.getBondState());
        if (!isContains(device)) {
            mDeviceList.add(createBluetoothItem(device));
            updateDeviceAdapter();
        }
    }

    private boolean isContains(BluetoothDevice device) {
        for (DeviceBean deviceBean : mDeviceList) {
            if (deviceBean.getBluetoothDevice().getName().equals(device.getName())
                    && deviceBean.getBluetoothDevice().getAddress().equals(device.getAddress())) {
                mDeviceList.remove(deviceBean);
                return true;
            }
        }

        return false;
    }



    /**
     * 当连接失败
     *
     * @param msg 失败信息
     */
    @Override
    public void onConnectFailed(String msg) {

        showToast("连接失败");
    }

    private String SNShow;

    /**
     * 当接收到 byte 数组
     *
     * @param bytes 内容
     */
    @Override
    public void onReceiveBytes(byte[] bytes) {
        String s = Utils.bytesToHexString(bytes);

        LogUtils.logBlueTooth("收到消息：" + s);

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


                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(verificationCommand[0]);
                    stringBuffer.append(verificationCommand[1]);
                    stringBuffer.append(verificationCommand[2]);
                    stringBuffer.append(verificationCommand[3]);

                    LogUtils.logBlueTooth("onReceiveBytes:最终命令 " + stringBuffer.toString());
                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray(stringBuffer.toString()));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast("握手失败");
                        }
                    });
                }
                break;
            case "02":
                //校验响应
                LogUtils.logBlueTooth("校验响应:原始数据 " + s);

                boolean b = Utils.verificationCmd(s, mKey2, mKeyData1, mKeyData2);
                if (b) {
                    showToast("校验成功");

                    mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_CHIP_MODEL_03));
                } else {
                    showToast("校验失败");
                }
                break;
            case "03":
                //芯片型号响应
                String substring1 = s.substring(6, s.length() - 2);
                String s1 = Utils.hexStringToString(substring1);
                //                LogUtils.logBlueTooth( "onReceiveBytes: " + s1);
                //                mBluetoothSPPUtil.send(Utils.hexStringToByteArray("C004"));
                BluetoothModel.getInstance().setDh(s1);

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_PRODUCT_MODEL_04));

                break;
            case "04":
                //产品型号响应
                String substring2 = s.substring(6, s.length() - 2);
                String s2 = Utils.hexStringToString(substring2);
                //                LogUtils.logBlueTooth( "onReceiveBytes: " + s2);
                BluetoothModel.getInstance().setProductNumber(s2);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_SOFTWARE_VERSION_05));
                break;
            case "05":
                //软件版本响应
                String substring3 = s.substring(6, s.length() - 2);
                String s3 = Utils.hexStringToString(substring3);
                //                LogUtils.logBlueTooth( "onReceiveBytes: " + s3);
                BluetoothModel.getInstance().setSoftwareVersion(s3);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_BT_ADDRESS_06));
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

                showToast(str.toString());

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_DIALOG_OP_50));

                break;
            case "50":
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_SN_CODE_51));
                String content50 = s.substring(6, 8);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content50.equals("01")) {
                            checkEar.setChecked(false);
                        } else if (content50.equals("00")) {
                            checkEar.setChecked(true);
                        }
                    }
                });

                break;
            case "51":
                String SNText = s.substring(6, s.length());


                for (int i = 0; i < SNText.length(); i++) {
                    String itemSn = SNText.charAt(i) + "";
                    if (itemSn.equals("0") || itemSn.equals("F") || itemSn.equals("f")) {

                    } else {

                        String SN = Utils.hexStringToString(SNText);
                        showToast("序列号" + SNText);
                        StringBuffer stringBuffer = new StringBuffer();
                        try {
                            for (int a = 1; a < 4; a++) {
                                stringBuffer.append(SN.substring(a == 1 ? 0 : (a - 1) * 4, a == 4 ? SN.length() : a * 4) + " ");
                            }

                            SNShow = stringBuffer.toString();
//                                    mTvSN.setText(stringBuffer.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
//                                    mTvSN.setText("");
                        }

                        return;
                    }
                }

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_60));

                break;
            case "60":
                boolean isSN_0 = false;
                String sn = SNShow.replace(" ", "");
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
        //LogUtils.logBlueTooth( "onSendBytes: " + Utils.bytesToHexString(bytes));
        LogUtils.logBlueTooth("发送消息：" + Utils.bytesToHexString(bytes));
    }

    /**
     * 当结束搜索设备
     */
    @Override
    public void onFinishFoundDevice() {
        //不需要
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