package com.juplus.app.bt;

import com.juplus.app.utils.Utils;

import java.util.Random;

import static com.juplus.app.utils.Utils.RANDOM;

public class CMDConfig {

    /*读取的命令*/
    public static final String CMD_READ_SHAKE_HANDS_01 = "C0013D";
    public static final String CMD_READ_VERIFY_02 = "C0023D";

    //芯片型号
    public static final String CMD_READ_CHIP_MODEL_03 = "C003";
    //产品型号
    public static final String CMD_READ_PRODUCT_MODEL_04 = "C004";
    //软件版本
    public static final String CMD_READ_SOFTWARE_VERSION_05 = "C005";
    //蓝牙地址
    public static final String CMD_READ_BT_ADDRESS_06 = "C006";
    public static final String CMD_READ_DEVICE_ID_07 = "C007";
    public static final String CMD_READ_PRODUCT_TYPE_08 = "C008";
    public static final String CMD_READ_POWER_STATUS_09 = "C009";
    public static final String CMD_READ_DIALOG_OP_50 = "C050";
    public static final String CMD_READ_SN_CODE_51 = "C051";
    public static final String CMD_READ_VOICE_WAKE_52 = "C052";
    public static final String CMD_READ_BT_NAME_54 = "C054";
    public static final String CMD_READ_CHECK_IN_EAR_55 = "C055";
    public static final String CMD_READ_AUTO_TYPE_56 = "C056";
    public static final String CMD_READ_GAME_MODEL_57 = "C057";
    public static final String CMD_READ_AUDIO_SPACE_58 = "C058";
    public static final String CMD_READ_NOISE_CONTROL_59 = "C059";
    public static final String CMD_READ_60 = "C060";
    //01 – 语音唤醒；
    //02 – 噪声控制（降噪/通透）；
    //03 – 噪声控制（降噪/通透/关闭）；
    public static final String CMD_READ_LONG_PRESS_LEFT_5A = "C05A";
    public static final String CMD_READ_LONG_PRESS_RIGHT_5B = "C05B";
    public static final String CMD_READ_DOUBLE_CLICK_LEFT_5C = "C05C";
    public static final String CMD_READ_DOUBLE_CLICK_RIGHT_5D = "C05D";

    public static String[] CMD_ARRAY={
            CMD_READ_CHIP_MODEL_03,
            CMD_READ_PRODUCT_MODEL_04,
            CMD_READ_SOFTWARE_VERSION_05,
            CMD_READ_BT_ADDRESS_06,
            CMD_READ_DEVICE_ID_07,
            CMD_READ_PRODUCT_TYPE_08,
            CMD_READ_POWER_STATUS_09,
            CMD_READ_DIALOG_OP_50,
            CMD_READ_SN_CODE_51,
            CMD_READ_VOICE_WAKE_52,
            CMD_READ_BT_NAME_54,
            CMD_READ_CHECK_IN_EAR_55,
            CMD_READ_AUTO_TYPE_56,
            CMD_READ_GAME_MODEL_57,
            CMD_READ_AUDIO_SPACE_58,
            CMD_READ_NOISE_CONTROL_59,
            CMD_READ_LONG_PRESS_LEFT_5A,
            CMD_READ_LONG_PRESS_RIGHT_5B,
            CMD_READ_DOUBLE_CLICK_LEFT_5C,
            CMD_READ_DOUBLE_CLICK_RIGHT_5D
    };

    /*写入命令*/
    //语音唤醒
    public static final String CMD_WRITE_VOICE_WAKE_ON = "C0620101";
    public static final String CMD_WRITE_VOICE_WAKE_OFF = "C0620100";

    //写入蓝牙地址
    public static final String CMD_WRITE_BT_NAME = "C064";

    //自动入耳检测
    public static final String CMD_WRITE_AUTO_CHECK_ON = "C0650100";
    public static final String CMD_WRITE_AUTO_CHECK_OFF = "C0650101";

    //音效设置
    //01 – 近场环绕；
    //02 – 清澈旋律；
    //03 – 现场律动；
    //04 – 宽广环绕；
    public static final String CMD_WRITE_AUDIO_TYPE = "C06601";

    //噪声控制
    //01 – 关闭；
    //02 – 降噪；
    //03 – 通透；
    public static final String CMD_WRITE_NOISE_CONTROL = "C06901";

    //长按设置
    //01 – 语音唤醒；
    //02 – 噪声控制（降噪/通透）；
    //03 – 噪声控制（降噪/通透/关闭）；
    public static final String CMD_WRITE_LONG_PRESS_LEFT = "C06A01";
    public static final String CMD_WRITE_LONG_PRESS_RIGHT = "C06B01";

    //双击设置
    //01 – 关闭；
    //02 – 语音唤醒；
    //03 – 播放/暂停；
    //04 – 下一首；
    //05 – 上一首；
    public static final String CMD_WRITE_DOUBLE_CLICK_LEFT = "C06C01";
    public static final String CMD_WRITE_DOUBLE_CLICK_RIGHT = "C06D01";

    public static final String CMD_00 = "00";
    public static final String CMD_01 = "01";
    public static final String CMD_02 = "02";
    public static final String CMD_03 = "03";
    public static final String CMD_04 = "04";
    public static final String CMD_05 = "05";
    public static final String CMD_06 = "06";
    public static final String CMD_07 = "07";
    public static final String CMD_08 = "08";
    public static final String CMD_09 = "09";
    public static final String CMD_50 = "50";
    public static final String CMD_51 = "51";
    public static final String CMD_52 = "52";
    public static final String CMD_53 = "53";
    public static final String CMD_54 = "54";
    public static final String CMD_55 = "55";
    public static final String CMD_56 = "56";
    public static final String CMD_57 = "57";
    public static final String CMD_58 = "58";
    public static final String CMD_59 = "59";
    public static final String CMD_5A = "5A";
    public static final String CMD_5B = "5B";
    public static final String CMD_5C = "5C";
    public static final String CMD_5D = "5D";
    public static final String CMD_60 = "60";
    public static final String CMD_61 = "61";
    public static final String CMD_62 = "62";
    public static final String CMD_63 = "63";
    public static final String CMD_64 = "64";
    public static final String CMD_65 = "65";
    public static final String CMD_66 = "66";
    public static final String CMD_67 = "67";
    public static final String CMD_68 = "68";
    public static final String CMD_69 = "69";
    public static final String CMD_6A = "6A";
    public static final String CMD_6B = "6B";
    public static final String CMD_6C = "6C";
    public static final String CMD_6D = "6D";


    public static final String PRODUCT_TYPE_TWS_2 = "tws公版二代";
    public static final String PRODUCT_TYPE_TWS_3 = "tws公版三代";
    public static final String PRODUCT_TYPE_TWS_S = "tws私模";
    public static final String PRODUCT_TYPE_YP3 = "yp3";


    /**
     * 获取握手命令   //8o46pJ2wRtl18Qt8KkCsDpexyrG3yd0
     *
     * @return
     */
    public static byte[] getHandshakeCmd() {
        StringBuffer cmd = new StringBuffer();
        cmd.append(CMD_READ_SHAKE_HANDS_01);
        cmd.append(getRandomNumber(61));
        return Utils.hexStringToByteArray(cmd.toString());
    }

    /**
     * 获取校验命令
     *
     * @return
     */
    public static String[] getVerificationCommand() {
        String[] s = new String[5];
        //000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f
        StringBuffer cmd = new StringBuffer();
        cmd.append(CMD_READ_VERIFY_02);
        cmd.append(getRandomNumber(11));
        s[0] = cmd.toString();
        s[1] = getRandomNumber(2); //密钥
        s[2] = getRandomNumber(32);
        //        Log.i("TAG", "getVerificationCommand: 10-2F" + s[2]);
        s[3] = getRandomNumber(16); //异或原参
        //        Log.i("TAG", "getVerificationCommand: 30-3F" + s[3]);
        byte[] bytes = Utils.hexStringToByteArray(s[2]);
        int i = Integer.parseInt(s[1], 16);
        s[4] = Utils.bytesToHexString(bytes); //未加密数据
        s[2] = Utils.bytesToHexString(Utils.encryptData(i, bytes, bytes.length));
        //        Log.i("测试", "getVerificationCommand:加密后10-2f " + s[2]);
        return s;
    }

    private static String getRandomNumber(int length) {
        char[] chars = RANDOM.toCharArray();
        long l = System.currentTimeMillis();
        Random random = new Random(l);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            stringBuffer.append(chars[random.nextInt(chars.length)]);
        }
        return Utils.stringToHexString(stringBuffer.toString());
    }
    //C0013E 485a7648564254656d74574838644247686831745453396b3571573168
    //C0013D6c44714159546170676562647350457662725530564c7565306a4a6d64503575797762334e30304768725a4c7464674e447a6c6646546b355655365435


}
