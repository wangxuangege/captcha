package com.wx.captcha.render.image;

import com.wx.captcha.render.compent.CompositePictureTitleToImage;
import com.wx.captcha.render.compent.TitleToImage;
import com.wx.captcha.utils.RandomUtil;
import com.google.common.collect.Lists;
import com.octo.captcha.CaptchaException;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author xinquan.huangxq
 */
public class CompositePictureFactory extends ImageCaptchaFactory {
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    /**
     * 绘制title
     */
    private TitleToImage titleToImage = new CompositePictureTitleToImage();

    /**
     * 存放组合图片的位置
     */
    private String dir;

    /**
     * 水平数
     */
    private int horizontalSize;

    /**
     * 竖直数
     */
    private int verticalSize;

    /**
     * 宽
     */
    private int width;

    /**
     * 高
     */
    private int height;

    /**
     * 标题高度
     */
    private int titleHeight;

    public CompositePictureFactory(String dir, int horizontalSize, int verticalSize, int width, int height, int titleHeight) {
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            throw new CaptchaException("Invalid configuration for a CompositePictureFactory : dir need exist");
        }
        if (horizontalSize <= 0 || verticalSize <= 0) {
            throw new CaptchaException("Invalid configuration for a CompositePictureFactory : horizontalSize>0 and verticalSize>0");
        }
        if (width <= 0 || height <= 0 || titleHeight <= 0) {
            throw new CaptchaException("Invalid configuration for a CompositePictureFactory : width>0 and height>0 and titleHeight>0");
        }
        this.dir = dir;
        this.width = width;
        this.height = height;
        this.horizontalSize = horizontalSize;
        this.verticalSize = verticalSize;
        this.titleHeight = titleHeight;
    }

    @Override
    public ImageCaptcha getImageCaptcha() {
        return getImageCaptcha(Locale.getDefault());
    }

    @Override
    public ImageCaptcha getImageCaptcha(Locale locale) {
        return generateCompositePicture();
    }

    /**
     * 生成验证码
     *
     * @return
     */
    private ImageCaptcha generateCompositePicture() {
        File[] dirFiles = new File(dir).listFiles(new FileFilter() {
            @Override
            public boolean accept(File dir) {
                // 第一级必须是类目
                return dir.isDirectory();
            }
        });
        if (dirFiles == null || dirFiles.length == 0) {
            return null;
        }
        // 随机选择一个类目
        int randomCategoryIndex = RandomUtil.getNextRandom(0, dirFiles.length - 1);
        File categoryFile = dirFiles[randomCategoryIndex];
        File[] questionFiles = categoryFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File dir) {
                // 第二级必须是问题
                return dir.isDirectory();
            }
        });
        if (questionFiles == null || questionFiles.length == 0) {
            return null;
        }
        int questionSize = Math.min(questionFiles.length, verticalSize * horizontalSize * 2);
        int[] randomDirIndex = RandomUtil.getNextUnSameRandom(questionSize, 0, questionFiles.length - 1, 100);
        List<File> questionDirList = Lists.newArrayListWithCapacity(randomDirIndex.length);
        for (int i = 0; i < randomDirIndex.length; ++i) {
            questionDirList.add(questionFiles[randomDirIndex[i]]);
        }
        List<File> jpgFileList = questionDirList.stream()
                .map(e -> e.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isFile() && file.getName().endsWith(".jpg");
                    }
                }))
                .flatMap(e -> Lists.newArrayList(e).stream())
                .collect(Collectors.toList());

        // 随机选取指定数量的图片
        int[] randomJpgFileIndex = RandomUtil.getNextUnSameRandom(verticalSize * horizontalSize, 0, jpgFileList.size() - 1, 1000);
        if (randomJpgFileIndex.length < verticalSize * horizontalSize) {
            // 如果1000次随机找到的图片依旧是重的，那么返回null
            // 生产一般不会出现这种情况，因为资源会比较多
            return null;
        }
        List<File> randomJpgFiles = Lists.newArrayListWithCapacity(verticalSize * horizontalSize);
        for (int i = 0; i < verticalSize * horizontalSize; ++i) {
            randomJpgFiles.add(jpgFileList.get(randomJpgFileIndex[i]));
        }

        // 确定问题
        int[] randomQuestionIndex = RandomUtil.getNextRandom(2, 0, randomJpgFileIndex.length - 1);
        List<String> questions = Lists.newArrayListWithCapacity(randomDirIndex.length);
        for (int i = 0; i < randomQuestionIndex.length; ++i) {
            File jpgFile = randomJpgFiles.get(randomQuestionIndex[i]);
            File dirFile = jpgFile.getParentFile();
            if (!questions.contains(dirFile.getName())) {
                questions.add(dirFile.getName());
            }
        }
        List<Rectangle> rectangles = Lists.newArrayListWithCapacity(verticalSize * horizontalSize);
        int rWidth = width / horizontalSize;
        int rHeight = (height - titleHeight) / verticalSize;
        for (int i = 0; i < randomJpgFiles.size(); ++i) {
            boolean yes = false;
            if (i == randomQuestionIndex[0] || i == randomQuestionIndex[1]) {
                yes = true;
            }
            if (!yes) {
                File jpgFile = randomJpgFiles.get(i);
                File dirFile = jpgFile.getParentFile();
                if (questions.contains(dirFile.getName())) {
                    yes = true;
                }
            }
            if (yes) {
                Rectangle rectangle = new Rectangle(0, 0, rWidth, rHeight);
                rectangle.translate(i % horizontalSize * rWidth, i / horizontalSize * rHeight + titleHeight);
                rectangles.add(rectangle);
            }
        }

        // 渲染验证码
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        try {
            // 渲染title
            BufferedImage titleImage = titleToImage.getImage(questions.toArray(new String[0]));
            g.drawImage(titleImage, 0, 0, width, titleHeight, null);

            for (int i = 0; i < verticalSize * horizontalSize; ++i) {
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(randomJpgFiles.get(i));
                } catch (IOException e) {
                }
                if (bufferedImage != null) {
                    g.drawImage(bufferedImage, i % horizontalSize * rWidth, i / horizontalSize * rHeight + titleHeight, rWidth, rHeight, null);
                } else {
                    g.drawImage(toolkit.getImage(randomJpgFiles.get(i).getAbsolutePath()), i % horizontalSize * rWidth, i / horizontalSize * rHeight + titleHeight, rWidth, rHeight, null);
                }
            }
        } finally {
            g.dispose();
        }

        return new CompositePicture(null, image, rectangles.toArray(new Rectangle[0]));
    }
}
