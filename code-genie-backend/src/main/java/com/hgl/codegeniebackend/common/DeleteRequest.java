package com.hgl.codegeniebackend.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: DeleteRequest
 * Package: com.hgl.codegeniebackend.common
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/25 14:12
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}

