package com.wx.captcha.portal.javabean;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class FirstValidateResponse extends BaseResponse {

    /**
     * 验证码请求唯一标示
     * （jsonp回调返回，没有办法调用某验证码的回调方案，返回通过challengeId循环遍历所有该对象，然后执行回调）
     */
    private String challengeId;

    /**
     * 校验是否通过
     */
    private boolean access;
}
