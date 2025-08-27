package com.hgl.codegenie.model.message;

import com.hgl.codegenie.model.enums.StreamMessageTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * ClassName: AiResponseMessage
 *
 * @Package: com.hgl.codegeniebackend.ai.model.message
 * @Description:  AI 响应消息
 * @Author HGL
 * @Create: 2025/8/6 17:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AiResponseMessage extends StreamMessage {

    private String data;

    public AiResponseMessage(String data) {
        super(StreamMessageTypeEnum.AI_RESPONSE.getValue());
        this.data = data;
    }
}
