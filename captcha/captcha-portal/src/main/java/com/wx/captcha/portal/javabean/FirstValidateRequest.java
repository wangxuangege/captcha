package com.wx.captcha.portal.javabean;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class FirstValidateRequest extends BaseRequest {

    /**
     * 验证码请求唯一标识
     */
    private String challengeId;

    /**
     * 用户轨迹信息
     * 轨迹信息格式比较复杂，但是前2位用来表示轨迹类型，格式如下：
     * \d{3}|**********************
     * 具体轨迹信息为***部分，|为分隔符
     */
    private String trajectory;
}
