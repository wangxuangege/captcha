package com.wx.captcha.sdk;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Map;

/**
 * @author xinquan.huangxq
 */
public final class SignatureUtil {

    /**
     * 获取签名
     *
     * @param params
     * @return
     */
    public static final String sign(String secretKey, Map<String, String> params) {
        Preconditions.checkNotNull(params);

        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            sb.append(key).append(params.get(key));
        }
        sb.append(secretKey);
        return MD5Util.toMD5String(sb.toString());
    }
}
