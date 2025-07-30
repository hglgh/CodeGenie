package com.hgl.codegeniebackend.ai.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * ClassName: CodeGenTypeEnum
 * Package: com.hgl.codegeniebackend.ai.enums
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 14:06
 */
@Getter
public enum CodeGenTypeEnum {

    HTML("原生 HTML 模式", "html"),
    MULTI_FILE("原生多文件模式", "multi_file");

    private final String text;
    private final String value;

    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static CodeGenTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (CodeGenTypeEnum anEnum : CodeGenTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}

