package com.wx.captcha.portal.controller;

import com.wx.captcha.common.ResultSupport;
import com.wx.captcha.portal.config.CaptchaPortalConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Field;

/**
 * captcha管理
 *
 * @author xinquan.huangxq
 */
@Slf4j
@RequestMapping("/admin")
@Controller
public class CaptchaAdminController {

    @Autowired
    private CaptchaPortalConfig captchaPortalConfig;

    @RequestMapping(value = "/set/{key}/{value}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResultSupport set(@PathVariable("key") String key, @PathVariable("value") String value) {
        if (StringUtils.isEmpty(key)) {
            return ResultSupport.newErrorResult(String.format("验证码配置标识不能为空"));
        }
        try {
            Field field = CaptchaPortalConfig.class.getDeclaredField(key);
            // 允许设置不能访问属性的值
            field.setAccessible(true);
            String type = field.getType().toString();
            if (type.endsWith("String")) {
                field.set(captchaPortalConfig, value);
            } else if (type.endsWith("int") || type.endsWith("Integer")) {
                field.set(captchaPortalConfig, Integer.parseInt(value));
            } else if (type.endsWith("long") || type.endsWith("Long")) {
                field.set(captchaPortalConfig, Long.parseLong(value));
            } else if (type.endsWith("double") || type.endsWith("Double")) {
                field.set(captchaPortalConfig, Double.parseDouble(value));
            } else if (type.endsWith("float") || type.endsWith("Float")) {
                field.set(captchaPortalConfig, Float.parseFloat(value));
            } else {
                return ResultSupport.newErrorResult(String.format("验证码配置标识类型不支持配置，key=%s,value=%s", key, value));
            }
            if ("captchaRenderMaxTps".equals(field.getName())) {
                captchaPortalConfig.getCaptchaRenderLimiter().setRate(captchaPortalConfig.getCaptchaRenderMaxTps());
            }
            if ("captchaPretreatedMaxTps".equals(field.getName())) {
                captchaPortalConfig.getCaptchaPretreatedLimiter().setRate(captchaPortalConfig.getCaptchaPretreatedMaxTps());
            }
            return ResultSupport.newSuccessResult(String.format("验证码配置更新成功，%s=%s", key, value));
        } catch (NoSuchFieldException e) {
            return ResultSupport.newErrorResult(String.format("设置的验证码配置不存在，key=%s,value=%s", key, value));
        } catch (Throwable e) {
            return ResultSupport.newErrorResult(String.format("验证码配置设置出现异常，key=%s,value=%s", key, value));
        }
    }
}
