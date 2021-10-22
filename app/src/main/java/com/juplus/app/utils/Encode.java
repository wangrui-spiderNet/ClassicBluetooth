package com.juplus.app.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class Encode {
    private static final String hexString="0123456789ABCDEF";
    public static final String  UTF_8="utf-8";
    public static final String  GBK="GBK";

    public static void main(String[] args)throws UnsupportedEncodingException {
        String str = "维保日期123qwwe";
        System.out.println("待编码字符：" + str);
        
        System.out.println("Encode:");
        String afterEncode = encode(str, "GBK");  //中文转换为16进制字符串
        System.out.println("Encode Result:"+afterEncode);
        
//        System.out.println(afterEncode);
        System.out.println("Decode:");
        String afterDecode = decode(afterEncode, "GBK");
        System.out.println("Decode Result:"+afterDecode);
        
    }
    /**
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str, String charset) throws UnsupportedEncodingException {
         //根据默认编码获取字节数组
         byte[] bytes=str.getBytes(charset);
         StringBuilder sb=new StringBuilder(bytes.length*2);
         //将字节数组中每个字节拆解成2位16进制整数
         for(int i=0;i<bytes.length;i++){
             sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
             sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
         }
         return sb.toString();
    }
    /**
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes, String charset) throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length()/2);
        //将每2位16进制整数组装成一个字节
        for(int i=0;i<bytes.length();i+=2)
            baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
        return new String(baos.toByteArray(), charset);
    }
    
    
}