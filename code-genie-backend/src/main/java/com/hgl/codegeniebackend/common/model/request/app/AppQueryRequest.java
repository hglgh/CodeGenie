package com.hgl.codegeniebackend.common.model.request.app;

import com.hgl.codegeniebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: AppQueryRequest
 * Package: com.hgl.codegeniebackend.common.model.request.app
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/31 15:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    /**
     * 代码生成类型（枚举）
     */
    private String codeGenType;

    /**
     * 部署标识
     */
    private String deployKey;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 创建用户id
     */
    private Long userId;

    @Serial
    private static final long serialVersionUID = 1L;
}

