package com.juplus.app;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
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
import com.juplus.app.bluetooth.BluetoothHelper;
import com.juplus.app.bluetooth.interfaces.IBTBoudListener;
import com.juplus.app.bluetooth.interfaces.IBTConnectListener;
import com.juplus.app.bluetooth.interfaces.IBTScanListener;
import com.juplus.app.bluetooth.interfaces.IBTStateListener;
import com.juplus.app.bluetooth.interfaces.IBluetoothHelper;
import com.juplus.app.entity.DeviceBean;
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
    }

    @SuppressLint("StringFormatMatches")
    @Optional
    @OnClick({R.id.tv_title_name, R.id.tv_new_version})
    public void onViewClicked(View view) {
        if (SystemUtil.isFastClick()) {
            return;
        }

        switch (view.getId()) {
            case R.id.tv_title_name:

                DeviceListDialog deviceListDialog = new DeviceListDialog(this, deviceBeanList);
                deviceListDialog.show();

                break;

            case R.id.tv_name:
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
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(HomeActivity.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
//                    mSwBluetooth.setChecked(mBluetoothHelper.isEnable());
//                    mTvNameTip.setVisibility(View.GONE);
//                    mTvName.setVisibility(View.GONE);
//                    mTvPairedDeviceTip.setVisibility(View.GONE);
//                    mTvUseDeviceTip.setVisibility(View.GONE);
//                    mRecyclerPaired.setVisibility(View.GONE);
//                    mRecyclerUse.setVisibility(View.GONE);
//                    mPairedAdapter.clear();
//                    mPairedAdapter.notifyDataSetChanged();
//                    mUseAdapter.clear();
//                    mUseAdapter.notifyDataSetChanged();
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(HomeActivity.this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
//                    mSwBluetooth.setChecked(mBluetoothHelper.isEnable());
//                    mTvNameTip.setVisibility(View.VISIBLE);
//                    mTvName.setVisibility(View.VISIBLE);
//                    mTvPairedDeviceTip.setVisibility(View.VISIBLE);
//                    mTvUseDeviceTip.setVisibility(View.VISIBLE);
//                    mRecyclerPaired.setVisibility(View.VISIBLE);
//                    mRecyclerUse.setVisibility(View.VISIBLE);
                    getBondedDevices();
                    mBluetoothHelper.setDiscoverableTimeout(300);//设置可见时间
//                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                //讲一个键值对对方到Intent对象当中，用于指定可见状态的持续时间
//                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//                startActivity(discoverableIntent);
                    mBluetoothHelper.startDiscovery();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Toast.makeText(HomeActivity.this, "蓝牙 STATE_TURNING_OFF", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
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
            Toast.makeText(HomeActivity.this, "搜索结束", Toast.LENGTH_SHORT).show();

        }

        /**
         *
         * @param device
         */
        @Override
        public void onFindDevice(BluetoothDevice device) {//发现新设备
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {//已配对
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
                paierDevStateChange(DeviceBean.STATE_BONDED, dev);
                mBluetoothHelper.connect(dev);
            }
//            else if(dev.getBondState()==BluetoothDevice.BOND_BONDING){//配对中
//                useDevStateChange(DeviceBean.STATE_BONDING,dev);
//            }else{//未配对
//                DeviceBean btUseItem=findItemByList(mUseAdapter.getData(),dev);
//                if(btUseItem!=null&&btUseItem.getState()== DeviceBean.STATE_BONDING){
//                    Toast.makeText(HomeActivity.this,"请确认配对设备已打开且在通信范围内",Toast.LENGTH_SHORT).show();
//                }
//                useDevStateChange(DeviceBean.STATE_BOND_NONE,dev);
//            }
        }
    };

    //蓝牙配对监听
    private IBTConnectListener mBTConnectListener = new IBTConnectListener() {
        @Override
        public void onConnecting(BluetoothDevice bluetoothDevice) {//连接中
            paierDevStateChange(DeviceBean.STATE_CONNECTING, bluetoothDevice);
        }

        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {//连接成功
            paierDevStateChange(DeviceBean.STATE_CONNECTED, bluetoothDevice);
        }

        @Override
        public void onDisConnecting(BluetoothDevice bluetoothDevice) {//断开中
            paierDevStateChange(DeviceBean.STATE_DISCONNECTING, bluetoothDevice);
        }

        @Override
        public void onDisConnect(BluetoothDevice bluetoothDevice) {//断开
            paierDevStateChange(DeviceBean.STATE_DISCONNECTED, bluetoothDevice);
        }

        @Override
        public void onConnectedDevice(List<BluetoothDevice> devices) {//已连接设备
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothHelper.stopDiscovery();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothHelper.setBTStateListener(null);//设置打开关闭状态监听
        mBluetoothHelper.setBTScanListener(null);//设置扫描监听
        mBluetoothHelper.setBTBoudListener(null);//设置配对监听
        mBluetoothHelper.setBTConnectListener(null);//设置连接监听
        mBluetoothHelper.destroy();
    }

}
