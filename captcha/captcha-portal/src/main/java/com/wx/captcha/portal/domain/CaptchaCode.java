package com.wx.captcha.portal.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xinquan.huangxq
 */
@Data
public class CaptchaCode implements Serializable {

    /**
     * 验证码请求唯一标识
     */
    private String challengeId;

    /**
     * 应用接入ID（验证码ID）
     */
    private String captchaId;

    /**
     * 状态
     */
    private CaptchaCodeStatus status;
}
