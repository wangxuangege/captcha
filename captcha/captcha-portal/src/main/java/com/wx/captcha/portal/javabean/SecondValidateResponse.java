package com.wx.captcha.portal.javabean;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class SecondValidateResponse extends BaseResponse {

    /**
     * 校验是否通过
     */
    private boolean access;
}
