package com.wx.captcha.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 验证码服务校验
 *
 * @author xinquan.huangxq
 */
@ConfigurationProperties(prefix = "captcha.service", locations = "classpath:config/application.properties")
@Component
public final class CaptchaVerifier {

    @Setter
    private String version;

    /**
     * 验证码ID
     */
    @Setter
    @Getter
    private String captchaId;

    /**
     * 私钥
     */
    @Setter
    private String secretKey;

    /**
     * 验证码服务网关
     */
    @Setter
    @Getter
    private String captchaServiceGateway;

    /**
     * 预初始化
     *
     * @return
     */
    public String pretreated() {
        String url = requestUrl("pretreated");

        Map<String, String> params = getCommonParams();

        JSONObject jsonObject = sendPost(url, fillSignature(params));
        if (jsonObject == null) {
            return null;
        }

        return jsonObject.getString("challengeId");
    }

    /**
     * 二次校验
     *
     * @param challengeId
     * @return
     */
    public boolean validate(String challengeId) {
        String url = requestUrl("validate/second");

        Map<String, String> params = getCommonParams();
        params.put("challengeId", challengeId);

        JSONObject jsonObject = sendPost(url, fillSignature(params));
        if (jsonObject == null) {
            // 校验失败
            return false;
        }
        return jsonObject.getBoolean("access") != null && jsonObject.getBoolean("access");
    }


    /**
     * 获取服务请求url
     *
     * @param url
     * @return
     */
    private String requestUrl(String url) {
        Preconditions.checkNotNull(captchaServiceGateway);
        Preconditions.checkArgument(url != null && url.length() > 1);

        if (captchaServiceGateway.endsWith("/")) {
            return captchaServiceGateway + url;
        } else {
            return captchaServiceGateway + "/" + url;
        }
    }

    /**
     * 获取服务请求公有参数
     *
     * @return
     */
    private Map<String, String> getCommonParams() {
        Map<String, String> params = Maps.newHashMap();
        params.put("captchaId", captchaId);
        params.put("version", version);
        params.put("timestamp", Long.toString(System.currentTimeMillis()));
        return params;
    }

    /**
     * 填充签名信息
     *
     * @param params
     * @return
     */
    private Map<String, String> fillSignature(Map<String, String> params) {
        Preconditions.checkNotNull(params);

        String signature = SignatureUtil.sign(secretKey, params);
        params.put("signature", signature);

        return params;
    }

    /**
     * 服务异常返回null
     *
     * @param url
     * @param params
     * @return
     */
    private static JSONObject sendPost(String url, Map<String, String> params) {
        String resultStr = HttpClientUtil.sendPost(url, params);
        if ("".equals(resultStr)) {
            return null;
        }
        try {
            JSONObject resultJson = JSON.parseObject(resultStr);
            if (resultJson == null) {
                return null;
            }
            Boolean success = resultJson.getBoolean("success");
            if (success == null || !success) {
                return null;
            }
            return resultJson.getJSONObject("model");
        } catch (Throwable e) {
            return null;
        }
    }
}
