package com.hgl.codegeniebackend.core.handler;

import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 流处理器执行器 — 根据代码生成类型选择合适的流处理器
 */
@Slf4j
@Component
public class StreamHandlerExecutor {

    @Resource
    private SimpleTextStreamHandler simpleTextStreamHandler;

    @Resource
    private JsonMessageStreamHandler jsonMessageStreamHandler;

    /**
     * 执行流处理并自动记录对话历史
     *
     * @param originFlux  原始流
     * @param appId       应用ID
     * @param userId      用户ID
     * @param codeGenType 代码生成类型
     * @return 处理后的流
     */
    public Flux<String> doExecute(Flux<String> originFlux, long appId, long userId, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML, MULTI_FILE -> simpleTextStreamHandler.handle(originFlux, appId, userId);
            case VUE_PROJECT -> jsonMessageStreamHandler.handle(originFlux, appId, userId);
        };
    }
}
