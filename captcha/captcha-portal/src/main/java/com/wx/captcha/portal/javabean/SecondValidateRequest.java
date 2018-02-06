package com.wx.captcha.portal.javabean;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class SecondValidateRequest extends BaseRequest {

    /**
     * 验证码请求唯一标识
     */
    private String challengeId;
}
