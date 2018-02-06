package com.wx.captcha.data.service;

import com.wx.captcha.data.mongoentity.AccessAppConf;

/**
 * @author xinquan.huangxq
 */
public interface AccessAppConfService {

    /**
     * 根据ID获取入网配置
     *
     * @param id
     * @return
     */
    AccessAppConf findById(String id);

    /**
     * 删除入网配置
     *
     * @param id
     * @return
     */
    void delete(String id);


    /**
     * 保存接入配置
     *
     * @param accessAppConf
     * @return
     */
    AccessAppConf save(AccessAppConf accessAppConf);
}
