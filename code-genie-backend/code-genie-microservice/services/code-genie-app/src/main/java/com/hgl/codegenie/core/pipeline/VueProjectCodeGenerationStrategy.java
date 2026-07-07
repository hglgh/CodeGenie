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
        TokenStream tokenStream = service.generateVueProjectCodeStream(appId, userMessage);
        return Flux.create(sink -> tokenStream
                .onPartialResponse(partialResponse -> {
                    AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                    sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                })
                .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                    ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                    sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                })
                .onToolExecuted(toolExecution -> {
                    ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                    sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                })
                .onCompleteResponse(chatResponse -> sink.complete())
                .onError(throwable -> {
                    log.error("VueProjectCodeGenerationStrategy Error: ", throwable);
                    sink.error(throwable);
                })
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
