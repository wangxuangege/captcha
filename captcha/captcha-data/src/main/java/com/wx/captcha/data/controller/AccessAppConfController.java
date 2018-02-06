package com.wx.captcha.data.controller;

import com.wx.captcha.common.ResultSupport;
import com.wx.captcha.data.mongoentity.AccessAppConf;
import com.wx.captcha.data.service.AccessAppConfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.wx.captcha.data.constants.AccessAppConfResultStatus.*;

/**
 * 应用接入配置restful服务
 *
 * @author xinquan.huangxq
 */
@Slf4j
@RestController
@RequestMapping("/accessAppConf")
public class AccessAppConfController {

    @Autowired
    private AccessAppConfService accessAppConfService;

    /**
     * 获取配置信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResultSupport<AccessAppConf> query(@RequestParam(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultSupport.newResult(ACCESS_APP_CONF_ID_CANNOT_EMPTY);
        }
        try {
            AccessAppConf accessAppConf = accessAppConfService.findById(id);
            if (accessAppConf == null) {
                return ResultSupport.newResult(ACCESS_APP_CONF_NOT_EXIST);
            }
            return ResultSupport.newSuccessResult(accessAppConf);
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error("", e);
            }
            return ResultSupport.newResult(UN_DEFINE_ERR);
        }
    }

    /**
     * 删除配置信息并返回
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResultSupport<AccessAppConf> delete(@RequestParam(value = "id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultSupport.newResult(ACCESS_APP_CONF_ID_CANNOT_EMPTY);
        }
        AccessAppConf accessAppConf = accessAppConfService.findById(id);
        if (accessAppConf == null) {
            return ResultSupport.newResult(ACCESS_APP_CONF_NOT_EXIST);
        }
        try {
            accessAppConfService.delete(id);
            return ResultSupport.newSuccessResult(accessAppConf);
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error("", e);
            }
            return ResultSupport.newResult(UN_DEFINE_ERR);
        }
    }

    /**
     * 添加一条配置信息
     *
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResultSupport<AccessAppConf> create(@RequestBody AccessAppConf accessAppConf) {
        if (!StringUtils.isEmpty(accessAppConf.getId())) {
            return ResultSupport.newResult(ACCESS_APP_CONF_ID_AUTO_GENERATE);
        }
        if (StringUtils.isEmpty(accessAppConf.getName())) {
            return ResultSupport.newResult(ACCESS_APP_CONF_NAME_CANNOT_EMPTY);
        }
        if (StringUtils.isEmpty(accessAppConf.getSecretKey())) {
            return ResultSupport.newResult(ACCESS_APP_CONF_SECRET_KEY_CANNOT_EMPTY);
        }
        try {
            return ResultSupport.newSuccessResult(accessAppConfService.save(accessAppConf));
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error("", e);
            }
            return ResultSupport.newResult(UN_DEFINE_ERR);
        }
    }

    /**
     * 更新配置信息
     *
     * @param accessAppConf
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResultSupport<AccessAppConf> update(@RequestBody AccessAppConf accessAppConf) {
        if (StringUtils.isEmpty(accessAppConf.getId())) {
            return ResultSupport.newResult(ACCESS_APP_CONF_ID_CANNOT_EMPTY);
        }
        try {
            AccessAppConf originAccessAppConf = accessAppConfService.findById(accessAppConf.getId());
            if (originAccessAppConf == null) {
                return ResultSupport.newResult(ACCESS_APP_CONF_NOT_EXIST);
            }

            if (accessAppConf.getDesc() != null) {
                originAccessAppConf.setDesc(accessAppConf.getDesc());
            }
            // 名称不能为空
            if (!StringUtils.isEmpty(accessAppConf.getName())) {
                originAccessAppConf.setName(accessAppConf.getName());
            }
            // 私钥不能为空
            if (!StringUtils.isEmpty(accessAppConf.getSecretKey())) {
                 originAccessAppConf.setSecretKey(accessAppConf.getSecretKey());
            }

            return ResultSupport.newSuccessResult(accessAppConfService.save(originAccessAppConf));
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error("", e);
            }
            return ResultSupport.newResult(UN_DEFINE_ERR);
        }
    }
}
