package com.wx.captcha.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xinquan.huangxq
 */
@ConfigurationProperties(prefix = "captcha.data")
@Component
@Data
public class CaptchaDataConfig {

    private String hazelcastMembers;
}
