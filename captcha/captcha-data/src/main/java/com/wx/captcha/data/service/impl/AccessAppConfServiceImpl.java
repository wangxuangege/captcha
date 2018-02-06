package com.wx.captcha.data.service.impl;

import com.wx.captcha.data.api.AccessAppConfRepository;
import com.wx.captcha.data.mongoentity.AccessAppConf;
import com.wx.captcha.data.service.AccessAppConfService;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

/**
 * @author xinquan.huangxq
 */
@Service
public class AccessAppConfServiceImpl implements AccessAppConfService {

    @Autowired
    private AccessAppConfRepository accessAppConfRepository;

    @Override
    public AccessAppConf findById(String id) {
        Preconditions.checkArgument(!StringUtils.isEmpty(id));

        return accessAppConfRepository.findOne(id);
    }

    @Override
    public void delete(String id) {
        Preconditions.checkArgument(!StringUtils.isEmpty(id));

        accessAppConfRepository.delete(id);
    }

    @Override
    public AccessAppConf save(AccessAppConf accessAppConf) {
        Preconditions.checkNotNull(accessAppConf);

        Date now = new Date();
        // 如果ID不存在，那么表示新添加的一条记录
        if (StringUtils.isEmpty(accessAppConf.getId())) {
            accessAppConf.setCreateTime(now);
            accessAppConf.setId(UUID.randomUUID().toString());
        }
        if (accessAppConf.getCreateTime() == null) {
            accessAppConf.setCreateTime(now);
        }
        accessAppConf.setUpdateTime(now);

        return accessAppConfRepository.save(accessAppConf);
    }
}
