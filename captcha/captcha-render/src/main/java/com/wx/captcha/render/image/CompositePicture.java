package com.wx.captcha.render.image;

import com.octo.captcha.image.ImageCaptcha;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 组合图片验证码
 * （12306验证码）
 *
 * @author xinquan.huangxq
 */
public class CompositePicture extends ImageCaptcha implements Serializable {

    /**
     * 序号数组
     */
    private Rectangle[] rectangles;

    protected CompositePicture(String question, BufferedImage challenge, Rectangle[] rectangles) {
        super(question, challenge);
        this.rectangles = rectangles;
    }

    @Override
    public Boolean validateResponse(Object response) {
        if (response == null || !(response instanceof Point.Double[])) {
            return Boolean.FALSE;
        }

        // 点击的点必须覆盖所有的区域块
        Point.Double[] clickPoints = (Point.Double[]) response;
        if (clickPoints.length != rectangles.length) {
            // 同一区域只能点击一次
            return Boolean.FALSE;
        }

        // 前端请求过来的是占比
        for (Point.Double point : clickPoints) {
            point.x = point.x * challenge.getWidth();
            point.y = point.y * challenge.getHeight();
        }

        // 遍历所有区域判断是否被覆盖
        for (Rectangle rectangle : rectangles) {
            boolean contain = false;
            for (Point.Double point : clickPoints) {
                contain = rectangle.contains(point);
                if (contain) {
                    break;
                }
            }
            if (!contain) {
                // 如果走完一轮没有找到需要包含的点
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }
}
