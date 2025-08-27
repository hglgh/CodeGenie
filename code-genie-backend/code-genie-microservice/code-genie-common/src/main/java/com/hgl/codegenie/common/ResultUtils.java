package com.hgl.codegenie.common;

import com.hgl.codegenie.common.exception.ErrorCode;

/**
 * ClassName: ResultUtils
 * Package: com.hgl.codegeniebackend.common
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/25 14:05
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return 响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @return 响应
     */
    public static BaseResponse<?> error(int errorCode, String message) {
        return new BaseResponse<>(errorCode, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return 响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

}
