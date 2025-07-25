package com.hgl.codegeniebackend.common.exception;

import lombok.Getter;

/**
 * ClassName: BusinessException
 * Package: com.hgl.codegeniebackend.common.exception
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/25 13:39
 */
@Getter
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
