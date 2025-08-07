package com.hgl.codegeniebackend.core.handler;

import com.hgl.codegeniebackend.common.model.entity.User;
import com.hgl.codegeniebackend.common.model.enums.ChatHistoryMessageTypeEnum;
import com.hgl.codegeniebackend.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * ClassName: SimpleTextStreamHandler
 *
 * @Package: com.hgl.codegeniebackend.core.handler
 * @Description: 简单文本流处理器
 * 处理 HTML 和 MULTI_FILE 类型的流式响应
 * @Author HGL
 * @Create: 2025/8/7 8:45
 */
@Slf4j
public class SimpleTextStreamHandler {

    /**
     * 处理传统流（HTML, MULTI_FILE）
     * 直接收集完整的文本响应
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param loginUser          登录用户
     * @return 处理后的流
     */
    public Flux<String> handle(Flux<String> originFlux, ChatHistoryService chatHistoryService, long appId, User loginUser) {
        StringBuilder aiResponseBuilder = new StringBuilder();
        return originFlux
                // 收集AI响应内容
                .doOnNext(aiResponseBuilder::append)
                .doOnComplete(() -> {
                    // 流式响应完成后，添加AI消息到对话历史
                    String aiResponse = aiResponseBuilder.toString();
                    chatHistoryService.addChatMessage(appId, aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                })
                .doOnError(error -> {
                    // 如果AI回复失败，也要记录错误消息
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    chatHistoryService.addChatMessage(appId, errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                });
    }
}
