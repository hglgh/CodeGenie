package com.hgl.codegeniebackend.ai.enums;

import lombok.Getter;

/**
 * ClassName: StreamMessageTypeEnum
 *
 * @Package: com.hgl.codegeniebackend.ai.enums
 * @Description:  流式消息类型枚举
 * @Author HGL
 * @Create: 2025/8/6 17:47
 */
@Getter
public enum StreamMessageTypeEnum {

    AI_RESPONSE("ai_response", "AI响应"),
    TOOL_REQUEST("tool_request", "工具请求"),
    TOOL_EXECUTED("tool_executed", "工具执行结果");

    private final String value;
    private final String text;

    StreamMessageTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static StreamMessageTypeEnum getEnumByValue(String value) {
        for (StreamMessageTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
