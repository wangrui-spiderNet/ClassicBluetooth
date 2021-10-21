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
import com.juplus.app.utils.BytesUtils;
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

import static com.juplus.app.bt.CMDConfig.CMD_00;
import static com.juplus.app.bt.CMDConfig.CMD_01;
import static com.juplus.app.bt.CMDConfig.CMD_02;
import static com.juplus.app.bt.CMDConfig.CMD_03;
import static com.juplus.app.bt.CMDConfig.CMD_04;
import static com.juplus.app.bt.CMDConfig.CMD_05;

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

    private Gson gson;
    private List<SettingBean> earSettingBeans,doubleSettingBeans, audioSettingBeans, longPressSettingBeans;
    private String SNShow;
    private String proType;
    private String leftEarDialogSettingName,rightEarDialogSettingName;
    private DeviceListDialog mDeviceListDialog;
    private ChangeNameDialog mChangeNameDialog;
    private SettingActionListDialog doubleSettingDialog, longSettingDialog, audioSettingDialog;

    private List<DeviceBean> mDeviceList = new ArrayList<>();
    private BluetoothSPPUtil mBluetoothSPPUtil;
    private String mKey2;
    private String mKeyData1;
    private String mKeyData2;
    private BluetoothDevice mBluetoothDevice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        gson = new Gson();

        String json_double_ear_setting = AssetUtil.getJsonFromAsset(APP.getInstance(), "setting_ear_double_array.json");
        String json_long_ear_setting = AssetUtil.getJsonFromAsset(APP.getInstance(), "setting_ear_long_array.json");
        String setting_audio_array = AssetUtil.getJsonFromAsset(APP.getInstance(), "setting_audio_array.json");

        doubleSettingBeans = gson.fromJson(json_double_ear_setting, new TypeToken<List<SettingBean>>() {
        }.getType());

        longPressSettingBeans = gson.fromJson(json_long_ear_setting, new TypeToken<List<SettingBean>>() {
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
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_NOISE_CONTROL + CMD_02));
                        break;

                    case R.id.rbCloseNoise:
                        rbCloseNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_close_noise_checked, 0, 0);
                        if (!hasDevice()) return;
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_NOISE_CONTROL + CMD_01));
                        break;

                    case R.id.rbVentilateNoise:
                        rbVentilateNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_ventilate_checked, 0, 0);
                        if (!hasDevice()) return;
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_NOISE_CONTROL + CMD_03));

                        break;
                }
            }
        });

        mBluetoothSPPUtil = new BluetoothSPPUtil(this, this);
        // 设置接收停止标志位字符串
        mBluetoothSPPUtil.setStopString("");
        mBluetoothSPPUtil.onCreate();
        getConnectBle();
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

                        LogUtils.logBlueTooth("新名字:" + o);

                        String hexString = Utils.stringToHexString(o);

                        LogUtils.logBlueTooth("名字16进制格式:" + hexString);

                        String len = Utils.decimalToHex(hexString.length());

                        LogUtils.logBlueTooth("名字16进制长度:" + len);

                        String changName = CMDConfig.CMD_WRITE_BT_NAME + len + hexString;

                        LogUtils.logBlueTooth("修改名字：" + changName);
                        mBluetoothSPPUtil.send(BytesUtils.hexStringToBytes(changName));
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

                if(earSettingBeans==null){

                    ToastUtil.showToast("获取蓝牙配置失败！");
                    earSettingBeans = longPressSettingBeans;
                    return;
                }

                doubleSettingDialog = new SettingActionListDialog(this, leftEarDialogSettingName, earSettingBeans, new CallBack<SettingBean>() {
                    @Override
                    public void callBack(SettingBean o) {
                        if (!hasDevice()) return;
                        tvLeftSetting.setText(o.name);
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_DOUBLE_CLICK_LEFT + o.code));
                    }
                });

                doubleSettingDialog.show();
                break;

            case R.id.tv_right_setting:

                if(earSettingBeans==null){

                    ToastUtil.showToast("获取蓝牙配置失败！");
                    earSettingBeans = longPressSettingBeans;
                    return;
                }

                longSettingDialog = new SettingActionListDialog(this, rightEarDialogSettingName, earSettingBeans, new CallBack<SettingBean>() {
                    @Override
                    public void callBack(SettingBean o) {
                        if (!hasDevice()) return;
                        tvRightSetting.setText(o.name);
                        mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_WRITE_LONG_PRESS_RIGHT + o.code));
                    }
                });

                longSettingDialog.show();
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
        getConnectBle();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.logBlueTooth("onRestart: ");
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


        mDeviceListDialog = new DeviceListDialog(HomeActivity.this, mDeviceList, new BtDeviceAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(DeviceBean deviceBean) {
                if (null != mBluetoothSPPUtil) {
                    mBluetoothSPPUtil.onDestroy();
                }
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
            mBluetoothSPPUtil = new BluetoothSPPUtil(this, this);
            // 设置接收停止标志位字符串
            mBluetoothSPPUtil.setStopString("");
            mBluetoothSPPUtil.connect(bleDevice);
        } else {
            showToast("设备已断开");
            getConnectBle();
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
//        ToastUtil.showToast("状态变化：" + device.getName());
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

        showToast(msg);
    }



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
                    showToast("握手失败");
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
                LogUtils.logBlueTooth("芯片型号:" + s);
                String substring1 = s.substring(6, s.length() - 2);
                String s1 = Utils.hexStringToString(substring1);

                // 247、247B、247C、TWS206
                LogUtils.logBlueTooth("芯片型号：" + s1);

                if (s1.equals("247")) {

                }

                BluetoothModel.getInstance().setDh(s1);

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_PRODUCT_MODEL_04));

                break;
            case "04":
                //产品型号响应
                LogUtils.logBlueTooth("产品型号:" + s);
                String substring2 = s.substring(6, s.length() - 2);
                String s2 = Utils.hexStringToString(substring2);

//                A6 Pro、A8
                LogUtils.logBlueTooth("产品型号：" + s2);
                BluetoothModel.getInstance().setProductNumber(s2);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_SOFTWARE_VERSION_05));
                break;
            case "05":
                //软件版本响应
                LogUtils.logBlueTooth("软件版本:" + s);
                String substring3 = s.substring(6, s.length() - 2);
                String s3 = Utils.hexStringToString(substring3);
                LogUtils.logBlueTooth("软件版本:" + s3);
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

                showToast("蓝牙地址：" + str.toString());

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_DEVICE_ID_07));
                LogUtils.logBlueTooth("蓝牙地址:" + s);
                break;

            case "07":
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_PRODUCT_TYPE_08));
                LogUtils.logBlueTooth("设备唯一ID:" + s);
                break;

            case "08":

                LogUtils.logBlueTooth("产品类型:" + s);

                proType = s.substring(6,8);

                String typeName = "";

//   02：tws公版二代；03：tws公版三代；04：tws私模；05：yp3；

                switch (proType) {
                    case CMD_02:
                        typeName = "tws公版二代";
                        LogUtils.logBlueTooth("产品类型:" + typeName);
                        leftEarDialogSettingName = "左耳 轻按2次";
                        rightEarDialogSettingName = "右耳 轻按2次";

                        earSettingBeans = doubleSettingBeans;
                        break;

                    case CMD_03:
                        typeName = "tws公版三代";
                        LogUtils.logBlueTooth("产品类型:" + typeName);
                        leftEarDialogSettingName = "左耳 长按";
                        rightEarDialogSettingName = "右耳 长按";

                        earSettingBeans = longPressSettingBeans;

                        break;

                    case CMD_04:
                        typeName = "tws私模";
                        LogUtils.logBlueTooth("产品类型:" + typeName);
                        break;

                    case CMD_05:
                        typeName = "yp3";
                        LogUtils.logBlueTooth("产品类型:" + typeName);
                        break;
                }

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_POWER_STATUS_09));
                break;

            case "09":

                LogUtils.logBlueTooth("电量:" + s);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_DIALOG_OP_50));

                String powerInfo = s.substring(6);

                if (powerInfo.length() == 6) {

                    LogUtils.logBlueTooth("左耳电量 left power:" + Utils.hexToInt(powerInfo.substring(0, 2)));
                    LogUtils.logBlueTooth("右耳电量 right power:" + Utils.hexToInt(powerInfo.substring(2, 4)));
                    LogUtils.logBlueTooth("盒子电量 box power:" + Utils.hexToInt(powerInfo.substring(4, 6)));
                }

                break;

            case "50":
                LogUtils.logBlueTooth("弹窗开关：" + s);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_SN_CODE_51));
                String content50 = s.substring(6, 8);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content50.equals("01")) {
                            checkEar.setChecked(true);
                        } else if (content50.equals("00")) {
                            checkEar.setChecked(false);
                        }
                    }
                });

                break;
            case "51":

                LogUtils.logBlueTooth("SN码：" + s);
//                String SNText = s.substring(6, s.length());
//
//
//                for (int i = 0; i < SNText.length(); i++) {
//                    String itemSn = SNText.charAt(i) + "";
//                    if (itemSn.equals("0") || itemSn.equals("F") || itemSn.equals("f")) {
//
//                    } else {
//
//                        String SN = Utils.hexStringToString(SNText);
//                        showToast("序列号" + SNText);
//                        StringBuffer stringBuffer = new StringBuffer();
//                        try {
//                            for (int a = 1; a < 4; a++) {
//                                stringBuffer.append(SN.substring(a == 1 ? 0 : (a - 1) * 4, a == 4 ? SN.length() : a * 4) + " ");
//                            }
//
//                            SNShow = stringBuffer.toString();
////                                    mTvSN.setText(stringBuffer.toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
////                                    mTvSN.setText("");
//                        }
//
//                        return;
//                    }
//                }

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_VOICE_WAKE_52));

                break;

            case "52":
                LogUtils.logBlueTooth("语音唤醒：" + s);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_BT_NAME_54));

                // 00 - 关； 01 - 开；
                String voice_up = s.substring(6, 8);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkSmart.setChecked(voice_up.equals("00") ? false : true);
                    }
                });


                break;
            case "54":
                LogUtils.logBlueTooth("蓝牙名字：" + s);

                String blueToothName = Utils.hexStringToString(s.substring(6));
                LogUtils.logBlueTooth("蓝牙名字：" + blueToothName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDeviceName.setText(blueToothName);
                        tvName.setText(blueToothName);
                    }
                });

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_CHECK_IN_EAR_55));
                break;
            case "55":
                LogUtils.logBlueTooth("自动入耳检测：" + s);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_AUTO_TYPE_56));

                // 00 - 关； 01 - 开；
                String auto_check = s.substring(6, 8);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkEar.setChecked(auto_check.equals("00") ? false : true);
                    }
                });

                break;
            case "56":
                LogUtils.logBlueTooth("音效设置：" + s);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_GAME_MODEL_57));
                //01 – 近场环绕；02 – 清澈旋律；03 – 现场律动；04 – 宽广环绕；

                String audio_setting = s.substring(6, 8);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (audio_setting) {
                            case CMD_01:

                                tvAudioType.setText("近场环绕");
                                break;

                            case CMD_02:
                                tvAudioType.setText("清澈旋律");
                                break;

                            case CMD_03:
                                tvAudioType.setText("现场律动");
                                break;

                            case CMD_04:
                                tvAudioType.setText("宽广环绕");
                                break;

                        }
                    }
                });

                break;

            case "57":
                LogUtils.logBlueTooth("游戏模式：" + s);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_AUDIO_SPACE_58));
                String game_model = s.substring(6, 8);

                LogUtils.logBlueTooth("游戏模式：" + (game_model.equals("01") ? true : false));

                break;

            case "58":
                LogUtils.logBlueTooth("空间音频：" + s);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_NOISE_CONTROL_59));

                String audio_space = s.substring(6, 8);

                LogUtils.logBlueTooth("空间音频：" + (audio_space.equals("01") ? true : false));

                break;

            case "59":
                LogUtils.logBlueTooth("噪音控制：" + s);
                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_LONG_PRESS_LEFT_5A));

                String noise_control = s.substring(6, 8);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (noise_control) {
                            case CMD_00:

                                rgNoiseManager.check(R.id.rbCloseNoise);
                                break;

                            case CMD_01:
                                rgNoiseManager.check(R.id.rbLowNoise);
                                break;

                            case CMD_02:
                                rgNoiseManager.check(R.id.rbVentilateNoise);
                                break;

                        }
                    }
                });

                break;

            case "5A":
                LogUtils.logBlueTooth("按住左耳：" + s);

                String press_left = s.substring(6, 8);

                switch (press_left){
                    case CMD_01:
                        tvLeftSetting.setText("语音唤醒");
                        break;
                    case CMD_02:
                        tvLeftSetting.setText("噪声控制（降噪/通透）");
                        break;
                    case CMD_03:
                        tvLeftSetting.setText("噪声控制（降噪/通透/关闭）");
                        break;
                }

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_LONG_PRESS_RIGHT_5B));
                break;

            case "5B":
                LogUtils.logBlueTooth("按住右耳：" + s);

                String press_right = s.substring(6, 8);

                switch (press_right){
                    case CMD_01:
                        tvRightSetting.setText("语音唤醒");
                        break;
                    case CMD_02:
                        tvRightSetting.setText("噪声控制（降噪/通透）");
                        break;
                    case CMD_03:
                        tvRightSetting.setText("噪声控制（降噪/通透/关闭）");
                        break;
                }

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_DOUBLE_CLICK_LEFT_5C));
                break;

            case "5C":
                LogUtils.logBlueTooth("双击左耳：" + s);

                String double_left = s.substring(6, 8);

                switch (double_left){
                    case CMD_01:
                        tvLeftSetting.setText("关闭");
                        break;
                    case CMD_02:
                        tvLeftSetting.setText("语音唤醒");
                        break;
                    case CMD_03:
                        tvLeftSetting.setText("播放/暂停");
                        break;
                    case CMD_04:
                        tvLeftSetting.setText("下一首");
                        break;
                    case CMD_05:
                        tvLeftSetting.setText("上一首");
                        break;
                }

                mBluetoothSPPUtil.send(Utils.hexStringToByteArray(CMDConfig.CMD_READ_DOUBLE_CLICK_RIGHT_5D));
                break;

            case "5D":
                LogUtils.logBlueTooth("双击右耳：" + s);
                String double_right = s.substring(6, 8);

                switch (double_right){
                    case CMD_01:
                        tvRightSetting.setText("关闭");
                        break;
                    case CMD_02:
                        tvRightSetting.setText("语音唤醒");
                        break;
                    case CMD_03:
                        tvRightSetting.setText("播放/暂停");
                        break;
                    case CMD_04:
                        tvRightSetting.setText("下一首");
                        break;
                    case CMD_05:
                        tvRightSetting.setText("上一首");
                        break;
                }

                break;

            case "60":
                LogUtils.logBlueTooth("弹窗开关：" + s);
                break;

            case "61":
                LogUtils.logBlueTooth("SN码：" + s);
                break;

            case "62":
                LogUtils.logBlueTooth("语音唤醒写入：" + s);
                break;

            case "63":
                LogUtils.logBlueTooth("蓝牙地址写入：" + s);
                break;

            case "64":
                LogUtils.logBlueTooth("蓝牙名字写入：" + s);
                break;

            case "65":
                LogUtils.logBlueTooth("自动入耳检测写入：" + s);
                break;

            case "66":
                LogUtils.logBlueTooth("音效设置：" + s);
                break;

            case "67":
                LogUtils.logBlueTooth("游戏模式写入：" + s);
                break;

            case "68":
                LogUtils.logBlueTooth("空间音频写入：" + s);
                break;

            case "69":
                LogUtils.logBlueTooth("噪音控制写入：" + s);
                break;

            case "6A":
                LogUtils.logBlueTooth("按住左边耳机写入：" + s);
                break;

            case "6B":
                LogUtils.logBlueTooth("按住右边耳机写入：" + s);
                break;

            case "6C":
                LogUtils.logBlueTooth("轻点左边两下耳机写入：" + s);
                break;

            case "6D":
                LogUtils.logBlueTooth("轻点右边两下耳机写入：" + s);
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