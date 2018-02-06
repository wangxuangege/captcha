package com.wx.captcha.data.mongoentity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * 接入captcha应用配置
 *
 * @author xinquan.huangxq
 */
@Data
public class AccessAppConf implements Serializable {

    /**
     * 接入应用唯一标示
     */
    @Id
    private String id;

    /**
     * 接入应用名
     */
    private String name;

    /**
     * 私钥
     */
    private String secretKey;

    /**
     * 描述
     */
    private String desc;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
