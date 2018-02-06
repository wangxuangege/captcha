package com.wx.captcha.portal.javabean;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class PreTreatedResponse extends BaseResponse {

    /**
     * 应用接入ID（验证码ID）
     */
    private String captchaId;

    /**
     * 验证码请求唯一标识
     */
    private String challengeId;
}
