package com.wx.captcha.controller;

import com.wx.captcha.sdk.CaptchaVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * captcha-web服务网关
 *
 * @author xinquan.huangxq
 */
@RequestMapping("/captcha")
@Controller
@Slf4j
public class CaptchaDemoController {

    @Autowired
    private CaptchaVerifier captchaVerifier;

    @RequestMapping("/index")
    public String index(Model model) {
        // 预初始化，获取验证码请求唯一标示
        String challengeId = captchaVerifier.pretreated();
        if ("".equals(challengeId)) {
            model.addAttribute("msg", "获取验证码唯一标示失败");
        } else {
            model.addAttribute("challengeId", challengeId);
            model.addAttribute("captchaId", captchaVerifier.getCaptchaId());
            model.addAttribute("captchaServiceGateway", captchaVerifier.getCaptchaServiceGateway());
        }
        return "index";
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submit(Model model, String challengeId, String username) {
        // 是否通过验证码校验
        boolean isValid = captchaVerifier.validate(challengeId);
        if (isValid) {
            return username + " 通过校验";
        } else {
            return username + " 未通过校验";
        }
    }
}
