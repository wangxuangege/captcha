package com.wx.captcha.data.constants;

import com.wx.captcha.common.ResultSupport;

/**
 * @author xinquan.huangxq
 */
public enum AccessAppConfResultStatus implements ResultSupport.TinyResult {

    SUCCESS(null),

    ACCESS_APP_CONF_ID_CANNOT_EMPTY("应用接入ID不能为空"),

    ACCESS_APP_CONF_NOT_EXIST("应用接入配置不存在"),

    ACCESS_APP_CONF_ID_AUTO_GENERATE("应用接入ID自动生成，不能输入"),

    ACCESS_APP_CONF_NAME_CANNOT_EMPTY("应用接入名称不能为空"),

    ACCESS_APP_CONF_SECRET_KEY_CANNOT_EMPTY("应用接入私钥不能为空"),

    UN_DEFINE_ERR("系统未定义异常"),

    ;

    private String msg;

    AccessAppConfResultStatus(String msg) {
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
