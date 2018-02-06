package com.wx.captcha.utils;

import java.util.UUID;

/**
 * @author xinquan.huangxq
 */
public final class UUIDUtil {

    /**
     * 获取随机的UUID
     *
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取没有分隔符-的UUID
     *
     * @return
     */
    public static String randomUUIDWithoutSplit() {
        return randomUUID().replace("-", "");
    }
}
