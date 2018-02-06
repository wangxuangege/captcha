package com.wx.captcha.data.service.impl;

import com.wx.captcha.data.cache.hazelcast.HazelcastCache;
import com.wx.captcha.data.cache.hazelcast.HazelcastCacheAdaptor;
import com.wx.captcha.data.service.GeneralCacheService;
import com.google.common.base.Preconditions;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author xinquan.huangxq
 */
@Service
public class GeneralCacheServiceImpl implements GeneralCacheService {

    @Autowired
    private GeneralCache generalCache;

    @Override
    public String get(String key) {
        Preconditions.checkArgument(!StringUtils.isEmpty(key));

        return generalCache.getFromCache(key);
    }

    @Override
    public String put(String key, String value, int timeoutSecond) {
        Preconditions.checkArgument(!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value) && timeoutSecond > 0);

        return generalCache.putToCache(key, value, timeoutSecond);
    }

    @Override
    public String put(String key, String value) {
        Preconditions.checkArgument(!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value));

        return generalCache.putToCache(key, value);
    }

    @Override
    public String invalid(String key) {
        Preconditions.checkArgument(!StringUtils.isEmpty(key));

        return generalCache.removeFromCache(key);
    }


    /**
     * general 缓存
     */
    @Component
    private static class GeneralCache extends HazelcastCacheAdaptor<String, String> {

        @Autowired
        private HazelcastCache hazelcastCache;

        private IMap<String, String> generalCache;

        @PostConstruct
        public void init() {
            generalCache = hazelcastCache.getHazelcastInstance().getMap("general-cache");
        }

        @Override
        protected IMap<String, String> getCache() {
            return generalCache;
        }
    }
}
