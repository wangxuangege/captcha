package com.wx.captcha.data.api;

import com.wx.captcha.data.mongoentity.AccessAppConf;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author xinquan.huangxq
 */
public interface AccessAppConfRepository extends MongoRepository<AccessAppConf, String> {
}
