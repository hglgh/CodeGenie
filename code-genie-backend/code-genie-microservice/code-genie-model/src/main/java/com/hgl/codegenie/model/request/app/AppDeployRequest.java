package com.hgl.codegenie.model.request.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: AppDeployRequest
 * Package: com.hgl.codegeniebackend.common.model.request.app
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/31 18:15
 */
@Data
public class AppDeployRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long appId;

    @Serial
    private static final long serialVersionUID = 1L;
}

