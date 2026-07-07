package com.hgl.codegeniebackend.core.handler;

import com.hgl.codegeniebackend.common.model.enums.ChatHistoryMessageTypeEnum;
import com.hgl.codegeniebackend.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 对话历史记录组件 — 从流处理器中提取的通用历史记录逻辑
 */
@Slf4j
@Component
public class ChatHistoryRecorder {

    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * 包装流，在完成时自动记录 AI 回复到对话历史
     *
     * @param stream  原始流
     * @param appId   应用 ID
     * @param userId  用户 ID
     * @return 包装后的流
     */
    public Flux<String> record(Flux<String> stream, long appId, long userId) {
        StringBuilder collected = new StringBuilder();
        return stream
                .doOnNext(collected::append)
                .doOnComplete(() -> {
                    String aiResponse = collected.toString();
                    chatHistoryService.addChatMessage(appId, aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(), userId);
                })
                .doOnError(error -> {
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    chatHistoryService.addChatMessage(appId, errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(), userId);
                });
    }
}
