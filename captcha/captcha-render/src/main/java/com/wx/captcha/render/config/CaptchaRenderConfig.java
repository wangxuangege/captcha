package com.wx.captcha.render.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xinquan.huangxq
 */
@ConfigurationProperties(prefix = "captcha.render")
@Component
@Data
public class CaptchaRenderConfig {
    /**
     * 组合图片存储的路径
     */
    private String path;

    /**
     * 水平图片张数
     */
    private int horizontalSize;

    /**
     * 竖直图片张数
     */
    private int verticalSize;

    /**
     * 总图宽度
     */
    private int width;

    /**
     * 总图高度
     */
    private int height;

    /**
     * 标题高度
     */
    private int titleHeight;
}
