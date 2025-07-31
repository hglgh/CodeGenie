package com.hgl.codegeniebackend.common.model.request.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: AppAddRequest
 * Package: com.hgl.codegeniebackend.common.model.request.app
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/31 15:28
 */
@Data
public class AppAddRequest implements Serializable {

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    @Serial
    private static final long serialVersionUID = 1L;
}

