package com.wx.captcha.render.store;

import com.wx.captcha.data.cache.hazelcast.HazelcastCache;
import com.wx.captcha.data.cache.hazelcast.HazelcastCacheAdaptor;
import com.hazelcast.core.IMap;
import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Locale;

/**
 * @author xinquan.huangxq
 */
@Service
public class HazelcastCaptchaStore implements CaptchaStore {

    @Autowired
    private CaptchaCache captchaCache;

    @Override
    public boolean hasCaptcha(String id) {
        return captchaCache.containsKey(id);
    }

    @Override
    public void storeCaptcha(String id, Captcha captcha) throws CaptchaServiceException {
        captchaCache.putToCache(id, captcha);
    }

    @Override
    public void storeCaptcha(String id, Captcha captcha, Locale locale) throws CaptchaServiceException {
        captchaCache.putToCache(id, captcha);
    }

    @Override
    public boolean removeCaptcha(String id) {
        captchaCache.removeFromCache(id);
        return true;
    }

    @Override
    public Captcha getCaptcha(String id) throws CaptchaServiceException {
        return captchaCache.getFromCache(id);
    }

    @Override
    public Locale getLocale(String id) throws CaptchaServiceException {
        // 忽略语言
        return null;
    }

    @Override
    public int getSize() {
        return captchaCache.size();
    }

    @Override
    public Collection getKeys() {
        return captchaCache.keySets();
    }

    @Override
    public void empty() {
        captchaCache.clearCache();
    }

    @Override
    public void initAndStart() {
        // 已经初始化
    }

    @Override
    public void cleanAndShutdown() {
        captchaCache.clearCache();
    }

    /**
     * 验证码缓存
     */
    @Component
    private static class CaptchaCache extends HazelcastCacheAdaptor<String, Captcha> {

        @Autowired
        private HazelcastCache hazelcastCache;

        private IMap<String, Captcha> generalCache;

        @PostConstruct
        public void init() {
            generalCache = hazelcastCache.getHazelcastInstance().getMap("captcha-cache");
        }

        @Override
        protected IMap<String, Captcha> getCache() {
            return generalCache;
        }
    }
}
