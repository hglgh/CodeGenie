package com.hgl.codegeniebackend.common;

import com.hgl.codegeniebackend.common.exception.ErrorCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: BaseResponse
 * Package: com.hgl.codegeniebackend.common
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/25 14:01
 */
@Data
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 返回状态码
     */
    private int code;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 返回信息
     */
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
