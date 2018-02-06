package com.wx.captcha.render.service;

import com.wx.captcha.render.config.CaptchaRenderConfig;
import com.wx.captcha.render.image.CompositePictureFactory;
import com.wx.captcha.render.store.HazelcastCaptchaStore;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xinquan.huangxq
 */
@Service
public class CaptchaRenderService implements ImageCaptchaService, RefreshBehavior {
    private AtomicBoolean initFlag = new AtomicBoolean(false);

    @Autowired
    private ImageCaptchaService captchaService;

    @Autowired
    private HazelcastCaptchaStore hazelcastCaptchaStore;

    @Autowired
    private CaptchaRenderConfig captchaRenderConfig;

    @PostConstruct
    public void init() {
        if (initFlag.compareAndSet(false, true)) {
            captchaService = new DefaultManageableImageCaptchaService(
                    hazelcastCaptchaStore,
                    new ListImageCaptchaEngine() {
                        @Override
                        protected void buildInitialFactories() {
                            addFactory(new CompositePictureFactory(
                                    captchaRenderConfig.getPath(),
                                    captchaRenderConfig.getHorizontalSize(),
                                    captchaRenderConfig.getVerticalSize(),
                                    captchaRenderConfig.getWidth(),
                                    captchaRenderConfig.getHeight(),
                                    captchaRenderConfig.getTitleHeight()));
                        }
                    }, 180, 100000, 75000);
        }
    }

    @Override
    public BufferedImage getImageChallengeForID(String ID) throws CaptchaServiceException {
        return captchaService.getImageChallengeForID(ID);
    }

    @Override
    public BufferedImage getImageChallengeForID(String ID, Locale locale) throws CaptchaServiceException {
        return captchaService.getImageChallengeForID(ID, locale);
    }

    @Override
    public Object getChallengeForID(String ID) throws CaptchaServiceException {
        return captchaService.getChallengeForID(ID);
    }

    @Override
    public Object getChallengeForID(String ID, Locale locale) throws CaptchaServiceException {
        return captchaService.getChallengeForID(ID, locale);
    }

    @Override
    public String getQuestionForID(String ID) throws CaptchaServiceException {
        return captchaService.getQuestionForID(ID);
    }

    @Override
    public String getQuestionForID(String ID, Locale locale) throws CaptchaServiceException {
        return captchaService.getQuestionForID(ID, locale);
    }

    @Override
    public Boolean validateResponseForID(String ID, Object response) throws CaptchaServiceException {
        return captchaService.validateResponseForID(ID, response);
    }

    @Override
    public void fresh(String ID) {
        hazelcastCaptchaStore.removeCaptcha(ID);
    }
}
