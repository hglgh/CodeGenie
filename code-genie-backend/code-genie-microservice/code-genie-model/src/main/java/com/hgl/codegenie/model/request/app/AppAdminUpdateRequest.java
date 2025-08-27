package com.hgl.codegenie.model.request.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: AppAdminUpdateRequest
 * Package: com.hgl.codegeniebackend.common.model.request.app
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/31 16:32
 */
@Data
public class AppAdminUpdateRequest implements Serializable {

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
     * 优先级
     */
    private Integer priority;

    @Serial
    private static final long serialVersionUID = 1L;
}

