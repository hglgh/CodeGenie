package com.hgl.codegenie.model.request.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: AppUpdateRequest
 * Package: com.hgl.codegeniebackend.common.model.request.app
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/31 15:39
 */
@Data
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    @Serial
    private static final long serialVersionUID = 1L;
}

