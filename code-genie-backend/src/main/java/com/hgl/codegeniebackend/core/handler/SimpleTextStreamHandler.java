package com.hgl.codegeniebackend.core.handler;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 简单文本流处理器 — 处理 HTML 和 MULTI_FILE 类型的流式响应
 * 使用 ChatHistoryRecorder 记录对话历史
 */
@Slf4j
@Component
public class SimpleTextStreamHandler {

    @Resource
    private ChatHistoryRecorder chatHistoryRecorder;

    /**
     * 处理传统流（HTML, MULTI_FILE）
     *
     * @param originFlux 原始流
     * @param appId      应用ID
     * @param userId     用户ID
     * @return 处理后的流
     */
    public Flux<String> handle(Flux<String> originFlux, long appId, long userId) {
        return chatHistoryRecorder.record(originFlux, appId, userId);
    }
}
