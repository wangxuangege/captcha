package com.wx.captcha.portal.config;

import com.google.common.util.concurrent.RateLimiter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xinquan.huangxq
 */
@ConfigurationProperties(prefix = "captcha.portal")
@Component
@Data
public class CaptchaPortalConfig {

    /**
     * 验证码有效过期时间
     * (单位s）
     */
    private int captchaAvailableTimeout = 300;

    /**
     * captcha渲染最高tps
     */
    private int captchaRenderMaxTps = 60;

    /**
     * captcha渲染超时时间
     */
    private int captchaRenderTimeout = 500;

    /**
     * captcha渲染限流
     */
    private RateLimiter captchaRenderLimiter = RateLimiter.create(captchaRenderMaxTps);

    /**
     * 预初始化最高tps
     */
    private int captchaPretreatedMaxTps = 60;

    /**
     * 预初始化超时时间
     */
    private int captchaPretreatedTimeout = 500;

    /**
     * 预初始化限流
     */
    private RateLimiter captchaPretreatedLimiter = RateLimiter.create(captchaPretreatedMaxTps);
}
