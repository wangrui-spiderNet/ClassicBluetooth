package com.juplus.app.utils;

import android.text.TextUtils;

import java.util.Random;

import static com.juplus.app.bt.CMDConfig.CMD_READ_SHAKE_HANDS_01;
import static com.juplus.app.bt.CMDConfig.CMD_READ_VERIFY_02;


public class Utils {

    public static final String RANDOM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 字符串转换为16进制字符串
     *
     * @param s
     * @return
     */
    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * byte数组转16进制字符串
     *
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native byte[] encryptData(int key, byte[] data, int length);

    /**
     * 计算出key2
     *
     * @param data
     */
    public static String getTheAccumulatedValueAnd(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        String hex = Integer.toHexString(total);
        return hex;
    }

    /**
     * 解密并验证数据
     *
     * @param data
     * @param key2
     * @param oldData
     * @return
     */
    public static boolean verificationCmd(String data, String key2, String oldData, String old2Data) {
        String substring = data.substring(32, 96);
        //        Log.i("TAG", "verificationCmd: 30-3F： " + oldData);
        //        Log.i("TAG", "verificationCmd: 10-2F： " + old2Data);
        //        Log.i("TAG", "verificationCmd: new 10-2F： " + substring);
        byte[] bytes = hexStringToByteArray(substring);
        int i = Integer.parseInt(key2, 16);
        //解密数据
        String s2 = bytesToHexString(encryptData(i, bytes, bytes.length));
        //        Log.i("TAG", "verificationCmd:解密后的10-2F： " + s2);
        //异或
        String substring1 = s2.substring(0, 32);
        String substring2 = s2.substring(32);
        byte[] bytes1 = hexStringToByteArray(substring1);
        byte[] bytes2 = hexStringToByteArray(substring2);
        byte[] bytes3 = hexStringToByteArray(oldData);
        byte[] bytes4 = new byte[bytes1.length + bytes2.length];
        for (int j = 0; j < bytes1.length; j++) {
            bytes4[j] = (byte) (bytes1[j] ^ bytes3[j]);
        }
        for (int j = 0; j < bytes2.length; j++) {
            bytes4[j + bytes2.length] = (byte) (bytes2[j] ^ bytes3[j]);
        }
        //        String s3 = bytesToHexString(encryptData(i, bytes4, bytes4.length));
        //        Log.i("TAG", "verificationCmd:异或后的数据： " + bytesToHexString(bytes4));
        return bytesToHexString(bytes4).equalsIgnoreCase(old2Data);
    }

}
