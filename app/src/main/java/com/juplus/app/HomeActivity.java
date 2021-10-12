package com.juplus.app;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
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

import com.gyf.immersionbar.ImmersionBar;
import com.juplus.app.adapter.BluetoothAdapter;
import com.juplus.app.bluetooth.BluetoothHelper;
import com.juplus.app.bluetooth.interfaces.IBTBoudListener;
import com.juplus.app.bluetooth.interfaces.IBTConnectListener;
import com.juplus.app.bluetooth.interfaces.IBTScanListener;
import com.juplus.app.bluetooth.interfaces.IBTStateListener;
import com.juplus.app.bluetooth.interfaces.IBluetoothHelper;
import com.juplus.app.entity.DeviceBean;
import com.juplus.app.utils.LogUtils;
import com.juplus.app.utils.SystemUtil;
import com.juplus.app.utils.ToastUtil;
import com.juplus.app.widget.DeviceListDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

@SuppressLint("MissingPermission")
public class HomeActivity extends AppCompatActivity {
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
    private IBluetoothHelper mBluetoothHelper;
    private List<DeviceBean> deviceBeanList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        immersionBar = ImmersionBar.with(this);
//        immersionBar.init();

        checkSmart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                ToastUtil.showToast(MyApplication.getInstance(), "自动检测耳机：" + b);
            }
        });

        checkEar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ToastUtil.showToast(MyApplication.getInstance(), "语音唤醒：" + b);
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
                        break;

                    case R.id.rbCloseNoise:
                        rbCloseNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_close_noise_checked, 0, 0);
                        break;

                    case R.id.rbVentilateNoise:
                        rbVentilateNoise.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_ventilate_checked, 0, 0);
                        break;
                }
            }
        });

        mBluetoothHelper = new BluetoothHelper();
        mBluetoothHelper.setBTStateListener(mBTStateListener);//设置打开关闭状态监听
        mBluetoothHelper.setBTScanListener(mBTScanListener);//设置扫描监听
        mBluetoothHelper.setBTBoudListener(mBTBoudListener);//设置配对监听
        mBluetoothHelper.setBTConnectListener(mBTConnectListener);//设置连接监听
        mBluetoothHelper.init(this);

        getBondedDevices();
        mBluetoothHelper.getConnectedDevices();
    }

    @SuppressLint("StringFormatMatches")
    @Optional
    @OnClick({R.id.tv_title_name, R.id.tv_new_version, R.id.tv_name})
    public void onViewClicked(View view) {
        if (SystemUtil.isFastClick()) {
            return;
        }

        switch (view.getId()) {
            case R.id.tv_title_name:

                mDeviceListDialog = new DeviceListDialog(this, deviceBeanList, new BluetoothAdapter.ItemClickListener() {
                    @Override
                    public void onItemClickListener(DeviceBean deviceBean) {

                    }
                });

                mDeviceListDialog.show();

                break;

            case R.id.tv_name:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.check_ear:
                break;

            case R.id.check_smart_wake:
                break;

            case R.id.tv_audio_type:
                break;

            case R.id.tv_left_setting:
                break;

            case R.id.tv_right_setting:
                break;

            case R.id.tv_new_version:
                toSettingMore();
                break;

            default:
                break;

        }
    }

    private void updateDeviceAdapter(){
        if(mDeviceListDialog!=null&&mDeviceListDialog.isShowing()&&mDeviceListDialog.getPairedAdapter()!=null){
            mDeviceListDialog.updateData(deviceBeanList);
        }
    }

    private void toSettingMore() {
        Intent intent = new Intent(this, SettingMoreActivity.class);
        startActivity(intent);
    }

    //蓝牙状态监听
    private IBTStateListener mBTStateListener = new IBTStateListener() {

        /**
         * 蓝牙开关状态
         * int STATE_OFF = 10; //蓝牙关闭
         * int STATE_ON = 12; //蓝牙打开
         * int STATE_TURNING_OFF = 13; //蓝牙正在关闭
         * int STATE_TURNING_ON = 11; //蓝牙正在打开
         */
        @Override
        public void onStateChange(int state) {
            switch (state) {
                case android.bluetooth.BluetoothAdapter.STATE_OFF:
                    deviceBeanList.clear();
                    updateDeviceAdapter();
                    Toast.makeText(HomeActivity.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                    break;
                case android.bluetooth.BluetoothAdapter.STATE_ON:
                    Toast.makeText(HomeActivity.this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                    getBondedDevices();
//                    mBluetoothHelper.setDiscoverableTimeout(300);//设置可见时间
                    mBluetoothHelper.startDiscovery();
                    break;
                case android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF:
                    Toast.makeText(HomeActivity.this, "蓝牙 STATE_TURNING_OFF", Toast.LENGTH_SHORT).show();
                    break;
                case android.bluetooth.BluetoothAdapter.STATE_TURNING_ON:
                    Toast.makeText(HomeActivity.this, "蓝牙 STATE_TURNING_ON", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //蓝牙搜索监听
    private IBTScanListener mBTScanListener = new IBTScanListener() {
        @Override
        public void onScanStart() {//搜索开始
            Toast.makeText(HomeActivity.this, "搜索开始", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onScanStop(List<BluetoothDevice> deviceList) {//搜索结束

            Toast.makeText(HomeActivity.this, "搜索全部数量:" + (deviceList != null ? deviceList.size() : 0), Toast.LENGTH_SHORT).show();
        }

        /**
         *
         * @param device
         */
        @Override
        public void onFindDevice(BluetoothDevice device) {//发现新设备
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {//已配对

                LogUtils.logBlueTooth("onFindDevice  已配对:" + (device == null ? 0 : device.getName()));
                addDevPair(DeviceBean.STATE_BONDED, device);
            }
//            else{
//                addDevUse(device);
//            }
        }
    };

    //蓝牙配对监听
    private IBTBoudListener mBTBoudListener = new IBTBoudListener() {

        /**
         * 设备配对状态改变
         * int BOND_NONE = 10; //配对没有成功
         * int BOND_BONDING = 11; //配对中
         * int BOND_BONDED = 12; //配对成功
         */
        @Override
        public void onBondStateChange(BluetoothDevice dev) {
            if (dev.getBondState() == BluetoothDevice.BOND_BONDED) {//已配对

                LogUtils.logBlueTooth("已配对 :" + (dev == null ? 0 : dev.getName()));

                paierDevStateChange(DeviceBean.STATE_BONDED, dev);

                LogUtils.logBlueTooth("连接 :" + (dev == null ? 0 : dev.getName()));
                mBluetoothHelper.connect(dev);
            }
        }
    };

    //蓝牙配对监听
    private IBTConnectListener mBTConnectListener = new IBTConnectListener() {
        @Override
        public void onConnecting(BluetoothDevice bluetoothDevice) {//连接中
            LogUtils.logBlueTooth("onConnecting :" + (bluetoothDevice == null ? 0 : bluetoothDevice.getName()));

            paierDevStateChange(DeviceBean.STATE_CONNECTING, bluetoothDevice);
        }

        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {//连接成功
            LogUtils.logBlueTooth("onConnected :" + (bluetoothDevice == null ? 0 : bluetoothDevice.getName()));

            paierDevStateChange(DeviceBean.STATE_CONNECTED, bluetoothDevice);
        }

        @Override
        public void onDisConnecting(BluetoothDevice bluetoothDevice) {//断开中
            LogUtils.logBlueTooth("onDisConnecting :" + (bluetoothDevice == null ? 0 : bluetoothDevice.getName()));
            paierDevStateChange(DeviceBean.STATE_DISCONNECTING, bluetoothDevice);
        }

        @Override
        public void onDisConnect(BluetoothDevice bluetoothDevice) {//断开
            LogUtils.logBlueTooth("onDisConnect :" + (bluetoothDevice == null ? 0 : bluetoothDevice.getName()));
            paierDevStateChange(DeviceBean.STATE_DISCONNECTED, bluetoothDevice);
        }

        @Override
        public void onConnectedDevice(List<BluetoothDevice> devices) {//已连接设备

            LogUtils.logBlueTooth("onConnectedDevice :" + (devices == null ? 0 : devices.size()));

            if (devices == null || devices.size() < 1) {
                return;
            }
            for (BluetoothDevice dev : devices) {
                DeviceBean btUseItem = findItemByList(deviceBeanList, dev);
                if (btUseItem != null) {
                    btUseItem.setBluetoothDevice(dev);
                    if (mBluetoothHelper.isConnected(dev)) {
                        btUseItem.setState(DeviceBean.STATE_CONNECTED);
                    } else if (btUseItem.getState() != DeviceBean.STATE_CONNECTED) {
                        btUseItem.setState(DeviceBean.STATE_DISCONNECTED);
                    }
                } else {
                    DeviceBean bluetoothItem = createBluetoothItem(dev);
                    if (mBluetoothHelper.isConnected(dev)) {
                        bluetoothItem.setState(DeviceBean.STATE_CONNECTED);
                    } else {
                        btUseItem.setState(DeviceBean.STATE_DISCONNECTED);
                    }
                    deviceBeanList.add(0, bluetoothItem);
                }
            }

            updateDeviceAdapter();
        }
    };

    /**
     * 配对设备列表发生改变
     *
     * @param state
     * @param dev
     */
    private void paierDevStateChange(int state, BluetoothDevice dev) {
        DeviceBean btUseItem = findItemByList(deviceBeanList, dev);
        DeviceBean btPaireItem = findItemByList(deviceBeanList, dev);
        if (btUseItem != null) {
            btUseItem.setState(state);
            btUseItem.setBluetoothDevice(dev);
            if (btPaireItem != null) {
                deviceBeanList.remove(btPaireItem);
            }
            deviceBeanList.add(0, btUseItem);
        } else if (btPaireItem != null) {
            btPaireItem.setState(state);
            btPaireItem.setBluetoothDevice(dev);
        } else {
            DeviceBean bluetoothItem = createBluetoothItem(dev);
            bluetoothItem.setState(state);
            deviceBeanList.add(0, bluetoothItem);
        }

        updateDeviceAdapter();
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
        mBluetoothHelper.startDiscovery();
        LogUtils.logBlueTooth("onResume  开始搜索");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mBluetoothHelper.stopDiscovery();
    }

    /**
     * 获取已配对设备
     */
    private void getBondedDevices() {//以配对设备
        Set<BluetoothDevice> bluetoothDeviceSet = mBluetoothHelper.getBondedDevices();
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

        ToastUtil.showToast(this, "已配对数量:" + deviceBeanList.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null!=mDeviceListDialog){
            mDeviceListDialog.dismiss();
            mDeviceListDialog=null;
        }

        mBluetoothHelper.stopDiscovery();
        mBluetoothHelper.setBTStateListener(null);//设置打开关闭状态监听
        mBluetoothHelper.setBTScanListener(null);//设置扫描监听
        mBluetoothHelper.setBTBoudListener(null);//设置配对监听
        mBluetoothHelper.setBTConnectListener(null);//设置连接监听
        mBluetoothHelper.destroy();


    }

}
