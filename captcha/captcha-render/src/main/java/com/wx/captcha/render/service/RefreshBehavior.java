package com.wx.captcha.render.service;

/**
 * 刷新验证码扩展行为
 *
 * @author xinquan.huangxq
 */
public interface RefreshBehavior {

    /**
     * 刷新
     *
     * @param ID
     */
    void fresh(String ID);
}
