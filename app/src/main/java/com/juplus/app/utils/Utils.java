package com.juplus.app.utils;

import android.text.TextUtils;

import java.util.Random;


public class Utils {

    public static final String RANDOM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static String hexString = "0123456789abcdef";
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 获取握手命令   //8o46pJ2wRtl18Qt8KkCsDpexyrG3yd0
     *
     * @return
     */
    public static byte[] getHandshakeCmd() {
        StringBuffer cmd = new StringBuffer();
        cmd.append("C0");
        cmd.append("01");
        cmd.append("3D");
        cmd.append(getRandomNumber(61));
        return hexStringToByteArray(cmd.toString());
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
        cmd.append("C0");
        cmd.append("02");
        cmd.append("3D");
        cmd.append(getRandomNumber(11));
        s[0] = cmd.toString();
        s[1] = getRandomNumber(2); //密钥
        s[2] = getRandomNumber(32);
        //        Log.i("TAG", "getVerificationCommand: 10-2F" + s[2]);
        s[3] = getRandomNumber(16); //异或原参
        //        Log.i("TAG", "getVerificationCommand: 30-3F" + s[3]);
        byte[] bytes = hexStringToByteArray(s[2]);
        int i = Integer.parseInt(s[1], 16);
        s[4] = bytesToHexString(bytes); //未加密数据
        s[2] = bytesToHexString(encryptData(i, bytes, bytes.length));
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
        return stringToHexString(stringBuffer.toString());
    }
    //C0013E 485a7648564254656d74574838644247686831745453396b3571573168
    //C0013D6c44714159546170676562647350457662725530564c7565306a4a6d64503575797762334e30304768725a4c7464674e447a6c6646546b355655365435

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


}
