package com.juplus.app.bt;

public class CMDConfig {

    /*读取的命令*/
    //芯片型号
    public static final String CMD_READ_CHIP_MODEL = "C003";
    //产品型号
    public static final String CMD_READ_PRODUCT_MODEL = "C004";
    //软件版本
    public static final String CMD_READ_SOFTWARE_VERSION = "C005";
    //蓝牙地址
    public static final String CMD_READ_BT_ADDRESS = "C006";
    public static final String CMD_READ_DEVICE_ID = "C007";
    public static final String CMD_READ_PRODUCT_TYPE = "C008";
    public static final String CMD_READ_POWER_STATUS = "C009";
    public static final String CMD_READ_DIALOG_OP = "C050";
    public static final String CMD_READ_SN_CODE = "C051";
    public static final String CMD_READ_VOICE_WAKE = "C052";
    public static final String CMD_READ_BT_NAME = "C054";
    public static final String CMD_READ_CHECK_IN_EAR = "C055";
    public static final String CMD_READ_AUTO_TYPE = "C056";
    public static final String CMD_READ_GAME_MODEL = "C057";
    public static final String CMD_READ_AUDIO_SPACE = "C058";
    public static final String CMD_READ_NOISE_CONTROL = "C059";
    //01 – 语音唤醒；
    //02 – 噪声控制（降噪/通透）；
    //03 – 噪声控制（降噪/通透/关闭）；
    public static final String CMD_READ_LONG_PRESS_LEFT = "C05A";
    public static final String CMD_READ_LONG_PRESS_RIGHT = "C05B";
    public static final String CMD_READ_DOUBLE_CLICK_LEFT = "C05C";
    public static final String CMD_READ_DOUBLE_CLICK_RIGHT = "C05D";

    public static String[] CMD_ARRAY={
            CMD_READ_CHIP_MODEL,
            CMD_READ_PRODUCT_MODEL,
            CMD_READ_SOFTWARE_VERSION,
            CMD_READ_BT_ADDRESS,
            CMD_READ_DEVICE_ID,
            CMD_READ_PRODUCT_TYPE,
            CMD_READ_POWER_STATUS,
            CMD_READ_DIALOG_OP,
            CMD_READ_SN_CODE,
            CMD_READ_VOICE_WAKE,
            CMD_READ_BT_NAME,
            CMD_READ_CHECK_IN_EAR,
            CMD_READ_AUTO_TYPE,
            CMD_READ_GAME_MODEL,
            CMD_READ_AUDIO_SPACE,
            CMD_READ_NOISE_CONTROL,
            CMD_READ_LONG_PRESS_LEFT,
            CMD_READ_LONG_PRESS_RIGHT,
            CMD_READ_DOUBLE_CLICK_LEFT,
            CMD_READ_DOUBLE_CLICK_RIGHT
    };

    /*写入命令*/
    //语音唤醒
    public static final String CMD_WRITE_VOICE_WAKE_ON = "C0620201";
    public static final String CMD_WRITE_VOICE_WAKE_OFF = "C0620200";

    //写入蓝牙地址
    public static final String CMD_WRITE_BT_NAME = "C064";

    //自动入耳检测
    public static final String CMD_WRITE_AUTO_CHECK_ON = "C0650200";
    public static final String CMD_WRITE_AUTO_CHECK_OFF = "C0650201";

    //音效设置
    //01 – 近场环绕；
    //02 – 清澈旋律；
    //03 – 现场律动；
    //04 – 宽广环绕；
    public static final String CMD_WRITE_AUDIO_TYPE = "C06602";

    //噪声控制
    //01 – 关闭；
    //02 – 降噪；
    //03 – 通透；
    public static final String CMD_WRITE_NOISE_CONTROL = "C06902";

    //长按设置
    //01 – 语音唤醒；
    //02 – 噪声控制（降噪/通透）；
    //03 – 噪声控制（降噪/通透/关闭）；
    public static final String CMD_WRITE_LONG_PRESS_LEFT = "C06A02";
    public static final String CMD_WRITE_LONG_PRESS_RIGHT = "C06B02";

    //双击设置
    //01 – 关闭；
    //02 – 语音唤醒；
    //03 – 播放/暂停；
    //04 – 下一首；
    //05 – 上一首；
    public static final String CMD_WRITE_DOUBLE_CLICK_LEFT = "C06C02";
    public static final String CMD_WRITE_DOUBLE_CLICK_RIGHT = "C06D02";

    public static final String CMD_01 = "01";
    public static final String CMD_02 = "02";
    public static final String CMD_03 = "03";
    public static final String CMD_04 = "04";
    public static final String CMD_05 = "05";



}
