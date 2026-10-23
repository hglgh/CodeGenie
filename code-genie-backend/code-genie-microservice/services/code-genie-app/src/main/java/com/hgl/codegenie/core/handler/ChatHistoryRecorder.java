package com.hgl.codegenie.core.handler;

import com.hgl.codegenie.model.enums.ChatHistoryMessageTypeEnum;
import com.hgl.codegenie.service.ChatHistoryService;
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
                    try {
                        chatHistoryService.addChatMessage(appId, aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(), userId);
                    } catch (Exception e) {
                        // 回调内抛异常会溢出为 onErrorDropped，只记日志，不影响流正常结束
                        log.error("记录 AI 回复到对话历史失败, appId={}, userId={}", appId, userId, e);
                    }
                })
                .doOnError(error -> {
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    try {
                        chatHistoryService.addChatMessage(appId, errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(), userId);
                    } catch (Exception e) {
                        // 吞掉记录异常，避免覆盖原始 error
                        log.error("记录 AI 失败信息到对话历史失败, appId={}, userId={}", appId, userId, e);
                    }
                });
    }
}
