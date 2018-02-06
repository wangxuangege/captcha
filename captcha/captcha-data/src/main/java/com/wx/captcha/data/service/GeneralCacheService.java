package com.wx.captcha.data.service;

/**
 * @author xinquan.huangxq
 */
public interface GeneralCacheService {

    /**
     * 获取
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 存储
     *
     * @param key
     * @param value
     * @param timeoutSecond 失效时间
     * @return
     */
    String put(String key, String value, int timeoutSecond);

    /**
     * 存储
     *
     * @param key
     * @param value
     * @return
     */
    String put(String key, String value);

    /**
     * 失效
     *
     * @param key
     * @return
     */
    String invalid(String key);
}
