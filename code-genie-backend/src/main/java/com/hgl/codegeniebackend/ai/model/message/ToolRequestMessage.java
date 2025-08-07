package com.hgl.codegeniebackend.ai.model.message;

import com.hgl.codegeniebackend.ai.enums.StreamMessageTypeEnum;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * ClassName: ToolRequestMessage
 *
 * @Package: com.hgl.codegeniebackend.ai.model.message
 * @Description: 工具调用消息
 * @Author HGL
 * @Create: 2025/8/6 17:47
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ToolRequestMessage extends StreamMessage {

    private String id;

    private String name;

    private String arguments;

    public ToolRequestMessage(ToolExecutionRequest toolExecutionRequest) {
        super(StreamMessageTypeEnum.TOOL_REQUEST.getValue());
        this.id = toolExecutionRequest.id();
        this.name = toolExecutionRequest.name();
        this.arguments = toolExecutionRequest.arguments();
    }
}
