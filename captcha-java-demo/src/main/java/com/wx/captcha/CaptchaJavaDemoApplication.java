package com.wx.captcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CaptchaJavaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptchaJavaDemoApplication.class, args);
    }
}
