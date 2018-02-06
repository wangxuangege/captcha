package com.wx.captcha.data.cache.hazelcast;

import com.hazelcast.core.IMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * hazelcast cache适配
 *
 * @param <I>
 * @param <T>
 */
public abstract class HazelcastCacheAdaptor<I, T> {

    protected abstract IMap<I, T> getCache();

    public boolean containsKey(I key) {
        return getCache().containsKey(key);
    }

    public T getFromCache(I key) {
        return getCache().get(key);
    }

    public T putToCache(I key, T value) {
        return getCache().put(key, value);
    }

    public T putToCache(I key, T value, int timeoutSecond) {
        return getCache().put(key, value, timeoutSecond, TimeUnit.SECONDS);
    }

    public T removeFromCache(I key) {
        return getCache().remove(key);
    }

    public Set<Map.Entry<I, T>> getEntrySet() {
        return getCache().entrySet();
    }

    public void clearCache() {
        getCache().clear();
    }

    public void putAllToCache(Map<I, T> map) {
        getCache().putAll(map);
    }

    public int size() {
        return getCache().size();
    }

    public Set<I> keySets() {
        return getCache().keySet();
    }
}
