package com.hgl.codegeniebackend.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName: MonitorContext
 * @Package: com.hgl.codegeniebackend.monitor
 * @Description:
 * @Author HGL
 * @Create: 2025/8/18 17:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorContext implements Serializable {

    private String userId;

    private String appId;

    @Serial
    private static final long serialVersionUID = 1L;
}
