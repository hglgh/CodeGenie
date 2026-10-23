package com.hgl.codegenie.core.pipeline;

import cn.hutool.json.JSONUtil;
import com.hgl.codegenie.AiCodeGeneratorService;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;
import com.hgl.codegenie.model.message.AiResponseMessage;
import com.hgl.codegenie.model.message.ToolExecutedMessage;
import com.hgl.codegenie.model.message.ToolRequestMessage;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Vue 项目代码生成策略（工具调用，无需二次 parse/save）
 */
@Slf4j
@Component
public class VueProjectCodeGenerationStrategy extends AbstractCodeGenerationStrategy {

    @Override
    public CodeGenTypeEnum getSupportedType() {
        return CodeGenTypeEnum.VUE_PROJECT;
    }

    @Override
    protected Flux<String> getCodeStream(AiCodeGeneratorService service, String userMessage, long appId) {
        // 1. 获取 LangChain4j TokenStream（Vue 项目通过工具调用直接写文件）
        TokenStream tokenStream = service.generateVueProjectCodeStream(appId, userMessage);
        // 2. 将 TokenStream 转换为 Reactor Flux，按事件类型包装为不同的 JSON 消息推送给前端
        return Flux.create(sink -> tokenStream
                // 2.1 AI 文本响应片段（流式输出给前端展示思考过程）
                .onPartialResponse(partialResponse -> {
                    AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                    sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                })
                // 2.2 AI 发起工具调用请求（如：创建文件、写入代码等）
                .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                    ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                    sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                })
                // 2.3 工具执行完成（携带执行结果，如文件路径、执行状态）
                .onToolExecuted(toolExecution -> {
                    ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                    sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                })
                // 2.4 AI 响应全部完成，关闭流
                .onCompleteResponse(chatResponse -> sink.complete())
                // 2.5 异常处理：记录日志并向下传递错误
                .onError(throwable -> {
                    log.error("VueProjectCodeGenerationStrategy Error: ", throwable);
                    sink.error(throwable);
                })
                // 3. 启动 TokenStream 开始异步生成
                .start());
    }

    /**
     * Vue 项目由工具直接写文件，无需在流结束后 parse + save
     */
    @Override
    protected boolean shouldParseAndSave() {
        return false;
    }
}
