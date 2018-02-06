package com.wx.captcha.portal.constants;

import com.wx.captcha.common.ResultSupport;

/**
 * @author xinquan.huangxq
 */
public enum CaptchaResultStatus implements ResultSupport.TinyResult {

    SUCCESS(null),

    CAPTCHA_ID_CANNOT_BE_EMPTY("验证码接入标识不能为空"),

    VERSION_NOT_AVAILABLE("服务版本不可用"),

    CAPTCHA_ID_NOT_EXIST("验证码接入标识不存在"),

    SIGNATURE_NOT_AVAILABLE("签名信息错误"),

    CHALLENGE_ID_CANNOT_BE_EMPTY("验证码请求标识不能为空"),

    CHALLENGE_ID_NOT_EXIST("验证码请求标识不存在"),

    CHALLENGE_ILLEGAL("验证码用户轨迹校验失败"),

    UN_DEFINE_ERR("系统未定义异常"),

    ;

    private String msg;

    CaptchaResultStatus(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean success() {
        return this.equals(SUCCESS);
    }

    @Override
    public String errCode() {
        return success() ? null : name();
    }

    @Override
    public String errMsg() {
        return msg;
    }
}
