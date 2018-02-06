package com.wx.captcha.common;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 结果包装类
 *
 * @author xinquan.huangxq
 */
@Data
public final class ResultSupport<T> implements Serializable {

    /**
     * 返回结果状态
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 错误码
     */
    private String errCode;

    /**
     * 包装的结果对象，一般success=true时候，model才会填充
     */
    private T model;

    /**
     * 附加结果，因为需要强转，一般不建议使用
     */
    private Map<String, Object> extModels = Maps.newHashMapWithExpectedSize(4);

    /**
     * 创建一个成功对象
     *
     * @param model
     * @param <T>
     * @return
     */
    public static <T> ResultSupport<T> newSuccessResult(T model) {
        ResultSupport<T> result = new ResultSupport<T>();
        result.setSuccess(true);
        result.setModel(model);
        return result;
    }

    /**
     * 创建一个带错误信息的返回对象
     *
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> ResultSupport<T> newErrorResult(String errorMessage) {
        ResultSupport<T> result = new ResultSupport<T>();
        result.setSuccess(false);
        result.setErrMsg(errorMessage);
        return result;
    }

    /**
     * 创建一个带错误信息和错误码的对象
     *
     * @param errorMessage
     * @param errorCode
     * @param <T>
     * @return
     */
    public static <T> ResultSupport<T> newErrorResult(String errorMessage, String errorCode) {
        ResultSupport<T> result = new ResultSupport<T>();
        result.setSuccess(false);
        result.setErrMsg(errorMessage);
        result.setErrCode(errorCode);
        return result;
    }

    /**
     * 获得附加结果
     *
     * @param key
     * @return
     */
    public Object getExtModel(String key) {
        return extModels.get(key);
    }

    /**
     * 设置附加结果
     *
     * @param key
     * @return
     */
    public Object addExtModel(String key, Object value) {
        return extModels.put(key, value);
    }

    /**
     * 方便扩展
     * 允许将其他类型转换为ResultSupport
     *
     * @param tinyResult
     * @param <T>
     * @return
     */
    public static <T> ResultSupport<T> newResult(TinyResult tinyResult) {
        ResultSupport<T> result = new ResultSupport<T>();
        result.setSuccess(tinyResult.success());
        result.setErrMsg(tinyResult.errMsg());
        result.setErrCode(tinyResult.errCode());
        return result;
    }

    /**
     * 实现该接口
     * 能够方便扩展ResultSupport的返回对象
     */
    public interface TinyResult {

        /**
         * 是否成功
         *
         * @return
         */
        boolean success();

        /**
         * 错误码
         *
         * @return
         */
        String errCode();

        /**
         * 错误信息
         *
         * @return
         */
        String errMsg();
    }
}