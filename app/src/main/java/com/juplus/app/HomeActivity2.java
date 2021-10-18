package com.juplus.app;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.juplus.app.adapter.BtDeviceAdapter;
import com.juplus.app.bt.BTByteListener;
import com.juplus.app.bt.BTConnectListener;
import com.juplus.app.bt.BtBase;
import com.juplus.app.bt.BtClient;
import com.juplus.app.entity.DeviceBean;
import com.juplus.app.entity.SettingBean;
import com.juplus.app.utils.AssetUtil;
import com.juplus.app.utils.BtReceiver;
import com.juplus.app.utils.LogUtils;
import com.juplus.app.utils.SystemUtil;
import com.juplus.app.utils.ToastUtil;
import com.juplus.app.utils.UIUtil;
import com.juplus.app.utils.Utils;
import com.juplus.app.widget.CallBack;
import com.juplus.app.widget.ChangeNameDialog;
import com.juplus.app.widget.DeviceListDialog;
import com.juplus.app.widget.SettingActionListDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

@SuppressLint("MissingPermission")
public class HomeActivity2 extends AppCompatActivity {
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

    private Gson gson;
    private BluetoothAdapter mBluetoothadapter;
    private BtClient mClient;
    private BtReceiver mBtReceiver;

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

        mClient = new BtClient(new BTConnectListener() {
            @Override
            public void onConnected(BluetoothDevice device) {

                ToastUtil.showToast("连上了:"+device.getName());
            }

            @Override
            public void onDisConnected() {
                ToastUtil.showToast("断开了:");
            }
        }, new BTByteListener() {
            @Override
            public void onReceiveByte(int state, byte[] msg) {
                ToastUtil.showToast("收到消息:"+ Utils.bytesToHexString(msg));
            }
        });

        mBtReceiver=new BtReceiver(this, new BtReceiver.Listener() {
            @Override
            public void foundDev(BluetoothDevice dev) {
                ToastUtil.showToast("发现新设备:"+dev.getName());
            }

            @Override
            public void newDeviceConnected(BluetoothDevice dev) {
                ToastUtil.showToast("新连接设备:"+dev.getName());
            }

            @Override
            public void stateChanged(BluetoothDevice dev) {
                ToastUtil.showToast("状态发生变化设备:"+dev.getName());
            }
        });

        immersionBar = ImmersionBar.with(this);
//        immersionBar.init();

        checkSmart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                ToastUtil.showToast( "自动检测耳机：" + b);
            }
        });

        checkEar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ToastUtil.showToast( "语音唤醒：" + b);
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

                        mClient.sendByte(new byte[]{1,2,3});
                        break;

                    case R.id.rbCloseNoise:
                        rbCloseNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_close_noise_checked, 0, 0);
                        mClient.sendByte(new byte[]{4,5,6});
                        break;

                    case R.id.rbVentilateNoise:
                        rbVentilateNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_ventilate_checked, 0, 0);
                        mClient.sendByte(new byte[]{7,8,9});
                        break;
                }
            }
        });

        getBondedDevices();

    }

//    @Override
//    public void socketNotify(int state, Object obj) {
//        if (isDestroyed())
//            return;
//        String msg = null;
//        switch (state) {
//            case CONNECTED:
//                BluetoothDevice dev = (BluetoothDevice) obj;
//                msg = String.format("与%s(%s)连接成功", dev.getName(), dev.getAddress());
//                break;
//            case DISCONNECTED:
//                msg = "连接断开";
//                break;
//            case MSG:
//                msg = String.format("\n%s", obj);
//                break;
//        }
//        APP.toast(msg, 0);
//    }

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
                        mClient.connect(deviceBean.getBluetoothDevice());
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


    private void updateDeviceAdapter() {
        if (mDeviceListDialog != null && mDeviceListDialog.isShowing() && mDeviceListDialog.getPairedAdapter() != null) {
            mDeviceListDialog.updateData(deviceBeanList);
        }
    }

    private void toSettingMore() {
        Intent intent = new Intent(this, SettingMoreActivity.class);
        startActivity(intent);
    }

    /**
     * 从集合 datas 中找 dev 对应的项
     *
     * @param datas
     * @param dev
     */
    private DeviceBean findItemByList(List<DeviceBean> datas, BluetoothDevice dev) {
        if (datas == null || datas.size() < 1) {
            return null;
        }
        for (DeviceBean deviceBean : datas) {
            if (!TextUtils.isEmpty(dev.getAddress()) && dev.getAddress().equals(deviceBean.getBluetoothDevice().getAddress())) {
                return deviceBean;
            }
        }
        return null;
    }

    private DeviceBean createBluetoothItem(BluetoothDevice device) {
        DeviceBean deviceBean = new DeviceBean();
        deviceBean.setBluetoothDevice(device);
        return deviceBean;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mBluetoothadapter.startDiscovery();
        LogUtils.logBlueTooth("onResume  开始搜索");
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        mBluetoothadapter.stopDiscovery();
//    }

    /**
     * 获取已配对设备
     */
    private void getBondedDevices() {//以配对设备
        BluetoothManager   mBluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothadapter = mBluetoothManager.getAdapter();
        mBluetoothadapter.startDiscovery();

        Set<BluetoothDevice> bluetoothDeviceSet = mBluetoothadapter.getBondedDevices();
        if (bluetoothDeviceSet != null && bluetoothDeviceSet.size() > 0) {
            for (BluetoothDevice device : bluetoothDeviceSet) {
                addDevPair(DeviceBean.STATE_BONDED, device);
            }
        }
    }

    /**
     * 向已配对列表中添加设备
     *
     * @param state
     * @param dev
     */
    private void addDevPair(int state, BluetoothDevice dev) {
        DeviceBean btUseItem = findItemByList(deviceBeanList, dev);
        if (btUseItem != null) {
            btUseItem.setBluetoothDevice(dev);
        } else {
            DeviceBean bluetoothItem = createBluetoothItem(dev);
            bluetoothItem.setState(state);
            deviceBeanList.add(0, bluetoothItem);
        }

        updateDeviceAdapter();
        ToastUtil.showToast( "已配对数量:" + deviceBeanList.size());
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
