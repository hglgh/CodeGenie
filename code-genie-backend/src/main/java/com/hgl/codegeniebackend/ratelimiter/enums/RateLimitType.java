package com.hgl.codegeniebackend.ratelimiter.enums;

/**
 * @ClassName: RateLimitType
 * @Package: com.hgl.codegeniebackend.ratelimiter.enums
 * @Description:
 * @Author HGL
 * @Create: 2025/8/16 11:20
 */
public enum RateLimitType {

    /**
     * 接口级别限流
     */
    API,

    /**
     * 用户级别限流
     */
    USER,

    /**
     * IP级别限流
     */
    IP
}
