package com.hgl.codegeniebackend.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: StreamMessage
 *
 * @Package: com.hgl.codegeniebackend.ai.model.message
 * @Description:  流式消息响应基类
 * @Author HGL
 * @Create: 2025/8/6 17:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamMessage {
    private String type;
}