package com.wx.captcha.portal.javabean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xinquan.huangxq
 */
@Data
public abstract class BaseRequest implements Serializable {

    /**
     * 应用接入ID（验证码ID）
     */
    private String captchaId;

    /**
     * 版本
     */
    private String version;

    /**
     * 请求时间
     * @see System#currentTimeMillis()
     */
    private long timestamp;

    /**
     * 签名
     */
    private String signature;
}
