package com.wx.captcha.render.compent;

import com.wx.captcha.utils.RandomUtil;
import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.TwirlFilter;
import com.octo.captcha.CaptchaException;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author xinquan.huangxq
 */
public class CompositePictureTitleToImage implements TitleToImage {

    @Override
    public BufferedImage getImage(String[] words) throws CaptchaException {
        String prefix = "请点击下图中";
        String all = "所有的";

        BufferedImage img = new BufferedImage(400, 30, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = img.createGraphics();

        Font font1 = new Font("宋体", Font.BOLD, fontSize());
        g.setFont(font1);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());

        int x = 0;
        g.setColor(Color.BLACK);
        g.drawString(prefix, x, 20);
        Dimension size = calcStringSize(prefix, font1);
        x += size.getWidth();

        g.setColor(Color.RED);
        g.drawString(all, x, 20);
        size = calcStringSize(all, font1);
        x += size.getWidth() + 5;

        Font font2 = new Font("宋体", Font.PLAIN, fontSize());
        for (int i = 0; i < words.length; ++i) {
            size = calcStringSize(words[i], font2);
            BufferedImage image = drawPicture(words[i], font2, size.width, size.height * 2, Color.BLACK, 0, size.height);

            g.drawImage(image, x, 0, null);
            x += image.getWidth();
        }
        g.dispose();

        return img;
    }

    @Override
    public int fontSize() {
        return 16;
    }


    public static BufferedImage drawPicture(String s, Font font, int width, int height, Color foreground, int startX, int startY) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2d = img.createGraphics();
        try {
            g2d.setColor(foreground);
            g2d.setFont(font);
            g2d.drawString(s, startX, startY);

            RippleFilter filter = new RippleFilter();
            int wave = RippleFilter.TRIANGLE;
            filter.setWaveType(wave);
            filter.setYAmplitude(RandomUtil.nextFloat() * 3);
            img = filter.filter(img, null);

            TwirlFilter filter2 = new TwirlFilter();
            double r = RandomUtil.nextDouble() / 2;
            if (RandomUtil.nextBoolean())
                r = -r;
            filter2.setAngle((float) r);
            filter2.setRadius(width);
            return filter2.filter(img, null);

        } finally {
            g2d.dispose();
        }
    }

    public static Dimension calcStringSize(String s, Font font) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2d = img.createGraphics();
        try {
            g2d.setFont(font);
            int sHeight = g2d.getFontMetrics().getHeight();
            int sWidth = g2d.getFontMetrics().charsWidth(s.toCharArray(), 0, s.length());
            return new Dimension(sWidth, sHeight);
        } finally {
            g2d.dispose();
        }
    }
}
