package com.wx.captcha.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xinquan.huangxq
 */
public final class MD5Util {

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String toMD5String(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (md == null || str == null) {
                return null;
            }
            byte[] byteData = md.digest(str.getBytes(Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ignored) {
            return null;
        }
    }
}
