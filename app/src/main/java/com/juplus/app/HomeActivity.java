package com.juplus.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.juplus.app.adapter.BtDeviceAdapter;
import com.juplus.app.bt.BTByteListener;
import com.juplus.app.bt.BTConnectListener;
import com.juplus.app.bt.BTFileListener;
import com.juplus.app.bt.BTMsgListener;
import com.juplus.app.bt.BtBase;
import com.juplus.app.bt.BtClient;
import com.juplus.app.entity.DeviceBean;
import com.juplus.app.entity.SettingBean;
import com.juplus.app.utils.AssetUtil;
import com.juplus.app.utils.BtReceiver;
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
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

@SuppressLint("MissingPermission")
public class HomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, BTConnectListener,BTByteListener, BtReceiver.Listener {
    private ImmersionBar immersionBar;

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
    private List<DeviceBean> deviceBeanList = new ArrayList<>();

    private SettingActionListDialog leftDoubleSettingDialog, leftLongSettingDialog, audioSettingDialog;
    private List<SettingBean> leftSettingBeans, audioSettingBeans, leftEarLongSettingBeans;
    public String[] mBTPerms = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int OPEN_BLUETOOTH_CODE = 110;

    private Gson gson;
    private BtClient mClient = new BtClient(this,this);
    private BluetoothAdapter mBluetoothadapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        immersionBar = ImmersionBar.with(this);
//        immersionBar.init();

        checkSmart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                ToastUtil.showToast("自动检测耳机：" + b);
            }
        });

        checkEar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ToastUtil.showToast("语音唤醒：" + b);
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

                        mClient.sendByte(new byte[]{1, 2, 3});
                        break;

                    case R.id.rbCloseNoise:
                        rbCloseNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_close_noise_checked, 0, 0);
                        mClient.sendByte(new byte[]{4, 5, 6});
                        break;

                    case R.id.rbVentilateNoise:
                        rbVentilateNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_ventilate_checked, 0, 0);
                        mClient.sendByte(new byte[]{7, 8, 9});
                        break;
                }
            }
        });

        initBluetoothAdapter();
        initPermissionData();

        mClient.setFileListener(new BTFileListener() {
            @Override
            public void onReceiveFile(int state, Object msg) {

            }
        });

        mClient.setMsgListener(new BTMsgListener() {
            @Override
            public void onReceiveMsg(int state, String msg) {

            }
        });

    }

    @Override
    public void foundDev(BluetoothDevice dev) {

    }

    @Override
    public void onReceiveByte(int state, byte[] msg) {

    }


    @Override
    public void onConnected(BluetoothDevice device) {
        String msg = String.format("与%s(%s)连接成功", device.getName(), device.getAddress());
        ToastUtil.showToast(msg);

        byte[] handshakeCmd = Utils.getHandshakeCmd();
        mClient.sendByte(handshakeCmd);
    }

    @Override
    public void onDisConnected() {

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

                mDeviceListDialog = new DeviceListDialog(this, deviceBeanList, new BtDeviceAdapter.ItemClickListener() {
                    @Override
                    public void onItemClickListener(DeviceBean deviceBean) {

                        setDeviceInfo(deviceBean);

                        if (null != mClient) {
                            mClient.close();
                        }
                        boolean isConnected = false;
                        try {
                            Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                            isConnectedMethod.setAccessible(true);
                            isConnected = (boolean) isConnectedMethod.invoke(deviceBean.getBluetoothDevice(), (Object[]) null);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        BluetoothDevice bluetoothDevice = deviceBean.getBluetoothDevice();

                        if (isConnected) {
                            tvDeviceName.setText(bluetoothDevice.getName() + "");
                            tvName.setText(bluetoothDevice.getAddress() + "");
                            //        BluetoothSPPUtil.setEnableLogOut();
                            // 设置接收停止标志位字符串
//                            mBluetoothSPPUtil.setStopString("");
                            mClient.connect(deviceBean.getBluetoothDevice());
                        } else {
                            ToastUtil.showToast("设备已断开");
                            initPermissionData();
                        }
                    }
                });

                mDeviceListDialog.show();

                break;

            case R.id.tv_name:
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);

                mChangeNameDialog = new ChangeNameDialog(this, tvName.getText().toString(), new CallBack<String>() {
                    @Override
                    public void callBack(String o) {

                        tvDeviceName.setText(o);
                        tvName.setText(o);

                        mClient.sendMsg(o);
                    }
                });
                mChangeNameDialog.show();

                break;

            case R.id.tv_audio_type:
                audioSettingDialog = new SettingActionListDialog(this, "", audioSettingBeans, new CallBack<SettingBean>() {
                    @Override
                    public void callBack(SettingBean o) {
                        tvAudioType.setText(o.name);
                        mClient.sendMsg(o.name);
                    }
                });
                audioSettingDialog.show();

                break;

            case R.id.tv_left_setting:

                leftDoubleSettingDialog = new SettingActionListDialog(this, "左耳 轻按2次", leftSettingBeans, new CallBack<SettingBean>() {
                    @Override
                    public void callBack(SettingBean o) {
                        mClient.sendMsg(o.name);
                        tvLeftSetting.setText(o.name);
                    }
                });

                leftDoubleSettingDialog.show();
                break;

            case R.id.tv_right_setting:
                leftLongSettingDialog = new SettingActionListDialog(this, "右耳 长按", leftEarLongSettingBeans, new CallBack<SettingBean>() {
                    @Override
                    public void callBack(SettingBean o) {
                        mClient.sendMsg(o.name);
                        tvRightSetting.setText(o.name);
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

    private void setDeviceInfo(DeviceBean deviceBean) {

        tvDeviceName.setText(deviceBean.getBluetoothDevice().getName());
        tvName.setText(deviceBean.getBluetoothDevice().getName());

        tvMacAddress.setText("蓝牙地址：" + deviceBean.getBluetoothDevice().getAddress());
    }

    /**
     * 初始化蓝牙
     */
    private void getConnectBT() {
        Observable.create(new ObservableOnSubscribe<BluetoothDevice>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BluetoothDevice> emitter) throws Throwable {
                // 初始化
                //获取已链接蓝牙设备

                if (null == mBluetoothadapter) {
                    ToastUtil.showToast("设备蓝牙无法使用");
                    emitter.onComplete();
                    return;
                }
                deviceBeanList.clear();
                try {
                    //是否存在连接的蓝牙设备
                    Set<BluetoothDevice> bondedDevices = mBluetoothadapter.getBondedDevices();
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
                        deviceBeanList.add(createBluetoothItem(device));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtil.showToast("您的手机系统版本不支持此验证方式");
                    }


                    @Override
                    public void onComplete() {
                        //筛选完成，展示
                        updateDeviceAdapter();
                    }
                });
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (PERMISSION_REQUEST_CODE == requestCode) {
            if (isBlueEnable()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, OPEN_BLUETOOTH_CODE);
            } else {
                getConnectBT();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPermissionData();
    }

    /**
     * 初始化已链接设备
     */
    private void initPermissionData() {
        if (EasyPermissions.hasPermissions(this, mBTPerms)) {
            //开始扫描
            if (isBlueEnable()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, OPEN_BLUETOOTH_CODE);
            } else {
                getConnectBT();
            }
        } else {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, PERMISSION_REQUEST_CODE, mBTPerms)
                            .build());
        }
    }

    /**
     * 蓝牙是否打开
     *
     * @return
     */
    private boolean isBlueEnable() {
        return mBluetoothadapter != null && mBluetoothadapter.isEnabled();
    }

    /**
     * 初始化 BluetoothAdapter
     *
     * @return
     */
    private BluetoothAdapter initBluetoothAdapter() {
        BluetoothManager mBluetoothManager = (BluetoothManager) HomeActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothadapter = mBluetoothManager.getAdapter();
        return mBluetoothadapter;
    }

    private void updateDeviceAdapter() {
        if (mDeviceListDialog != null && mDeviceListDialog.isShowing() && mDeviceListDialog.getPairedAdapter() != null) {
            mDeviceListDialog.updateData(deviceBeanList);
        }
    }

    private void toSettingMore() {
        Intent intent = new Intent(this, SettingMoreActivity.class);
        startActivity(intent);
    }


    private DeviceBean createBluetoothItem(BluetoothDevice device) {
        DeviceBean deviceBean = new DeviceBean();
        deviceBean.setBluetoothDevice(device);
        return deviceBean;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mDeviceListDialog) {
            mDeviceListDialog.dismiss();
            mDeviceListDialog = null;
        }

        if (null != mChangeNameDialog) {
            mChangeNameDialog.dismiss();
            mChangeNameDialog = null;
        }

        mClient.unListener();
        mClient.close();
    }


}
