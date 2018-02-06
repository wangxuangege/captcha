package com.wx.captcha.render.compent;

import com.octo.captcha.CaptchaException;

import java.awt.image.BufferedImage;

/**
 * @author xinquan.huangxq
 */
public interface TitleToImage {

    /**
     * words包含的特殊字符
     *
     * @param words
     * @return
     * @throws CaptchaException
     */
    BufferedImage getImage(String[] words) throws CaptchaException;

    /**
     * 字体大小
     *
     * @return
     */
    int fontSize();
}
