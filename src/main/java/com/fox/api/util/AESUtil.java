package com.fox.api.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static final String ENCODING = "UTF-8";
    private static final int KEY_LENGTH = 16;

    /**
     * AES 128解密
     *
     * @param str 明文
     * @param key 密码，向量同密码
     * @return 密文
     * @throws Exception
     */
    public static String decrypt(String str, String key) throws Exception {
        try {
            // 判断Key是否正确
            if (key == null) {
                return null;
            }
            key = align(key);
            byte[] raw = key.getBytes(ENCODING);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = hex2byte(str);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * @param str
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt(String str, String key) throws Exception {
        if (key == null) {
            return null;
        }
        key = align(key);
        byte[] raw = key.getBytes(ENCODING);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec(key.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(str.getBytes());
        return byte2hex(encrypted);

    }

    /**
     * byte与16进制字符串的互相转换
     *
     * @param src
     * @return
     */
    private static String byte2hex(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制字符串与byte的互相转换
     *
     * @param hexString
     * @return
     */
    private static byte[] hex2byte(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 密码长度小于16个字节时补充空字符，大于16个字节时只取前16个字节
     *
     * @param key
     * @return
     */
    private static String align(String key) throws Exception {
        if (key == null) {
            return null;
        }
        int length = key.getBytes(ENCODING).length;
        if (length > KEY_LENGTH) {
            key = key.substring(0, KEY_LENGTH);
        } else {
            int zero_size = KEY_LENGTH - length;
            for (int i = 0; i < zero_size; i++) {
                key += '\0';
            }
        }
        return key;
    }
}
