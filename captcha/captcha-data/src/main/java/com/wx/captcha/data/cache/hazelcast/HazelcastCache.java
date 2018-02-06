package com.wx.captcha.data.cache.hazelcast;

import com.wx.captcha.data.config.CaptchaDataConfig;
import com.google.common.base.Preconditions;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xinquan.huangxq
 */
@Service
public class HazelcastCache {
    private AtomicBoolean initFlag = new AtomicBoolean(false);

    private HazelcastInstance hazelcastInstance;

    @Autowired
    private CaptchaDataConfig captchaDataConfig;

    @PostConstruct
    public void init() {
        if (initFlag.compareAndSet(false, true)) {
            Config config = new ClasspathXmlConfig("hazelcast-config.xml");
            NetworkConfig network = config.getNetworkConfig();
            JoinConfig join = network.getJoin();
            join.getMulticastConfig().setEnabled(false);
            join.getTcpIpConfig().addMember(captchaDataConfig.getHazelcastMembers()).setEnabled(true);
            hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        }
    }

    @PreDestroy
    public void destroy() {
        if (hazelcastInstance != null) {
            hazelcastInstance.shutdown();
        }
    }

    public HazelcastInstance getHazelcastInstance() {
        Preconditions.checkNotNull(hazelcastInstance);

        return hazelcastInstance;
    }
}
