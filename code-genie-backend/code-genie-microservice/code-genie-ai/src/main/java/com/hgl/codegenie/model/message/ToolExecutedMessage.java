package com.hgl.codegenie.model.message;

import com.hgl.codegenie.model.enums.StreamMessageTypeEnum;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * ClassName: ToolExecutedMessage
 *
 * @Package: com.hgl.codegeniebackend.ai.model.message
 * @Description: 工具执行结果消息
 * @Author HGL
 * @Create: 2025/8/6 17:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ToolExecutedMessage extends StreamMessage {

    private String id;

    private String name;

    private String arguments;

    private String result;

    public ToolExecutedMessage(ToolExecution toolExecution) {
        super(StreamMessageTypeEnum.TOOL_EXECUTED.getValue());
        this.id = toolExecution.request().id();
        this.name = toolExecution.request().name();
        this.arguments = toolExecution.request().arguments();
        this.result = toolExecution.result();
    }
}
