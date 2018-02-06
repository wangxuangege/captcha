package com.wx.captcha.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

/**
 * 对象签名
 *
 * @author xinquan.huangxq
 */
public final class MD5SignatureUtil {

    /**
     * 返回对象签名信息
     * 遍历类的所有层次，若基类某属性子类也存在，使用子类的属性覆盖基类的
     *
     * @param secretKey
     * @param request
     * @param <T>
     * @return
     */
    public static final <T> String sign(String secretKey, T request) {
        Map<String, String> params = Maps.newHashMap();

        Stack<Class<?>> classStack = new Stack<>();
        classStack.push(request.getClass());

        Class<?> tmpClass = request.getClass();
        // 保证所有基类入栈
        while (!tmpClass.getSuperclass().equals(Object.class)) {
            classStack.push(tmpClass.getSuperclass());
            tmpClass = tmpClass.getSuperclass();
        }
        // 从基类倒着遍历
        // 若基类有属性，子类也存在，使用子类的属性覆盖基类的属性
        while (!classStack.empty()) {
            tmpClass = classStack.pop();
            // 获取非static的属性，签名需要包含这个信息
            Field[] fields = tmpClass.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    // 忽略static成员和key为签名字段的值
                    if (Modifier.isStatic(field.getModifiers()) ||
                            "signature".equals(field.getName())) {
                        continue;
                    }
                    field.setAccessible(true);
                    try {
                        Object fieldValue = field.get(request);
                        if (fieldValue != null) {
                            params.put(field.getName(), fieldValue.toString());
                        }
                    } catch (IllegalAccessException e) {
                        // 已经设置为accessible，不可能抛出此类异常
                    }
                }
            }
        }

        // 返回签名
        return sign(secretKey, params);
    }

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
