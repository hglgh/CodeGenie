package com.hgl.codegenie.core.handler;

import com.hgl.codegenie.model.entity.User;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;
import com.hgl.codegenie.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * ClassName: StreamHandlerExecutor
 *
 * @Package: com.hgl.codegenie.core.handler
 * @Description: 流处理器执行器
 * 根据代码生成类型创建合适的流处理器：
 * <ol>
 *     <li>传统的 Flux<String> 流（HTML、MULTI_FILE） -> SimpleTextStreamHandler</li>
 *     <li>TokenStream 格式的复杂流（VUE_PROJECT） -> JsonMessageStreamHandler</li>
 * </ol>
 * @Author HGL
 * @Create: 2025/8/7 9:26
 */
@Slf4j
@Component
public class StreamHandlerExecutor {

    @Resource
    private JsonMessageStreamHandler jsonMessageStreamHandler;

    /**
     * 创建流处理器并处理聊天历史记录
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param loginUser          登录用户
     * @param codeGenType        代码生成类型
     * @return 处理后的流
     */
    public Flux<String> doExecute(Flux<String> originFlux, ChatHistoryService chatHistoryService, long appId, User loginUser, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML, MULTI_FILE ->
                // 简单文本处理器不需要依赖注入
                    new SimpleTextStreamHandler().handle(originFlux, chatHistoryService, appId, loginUser);
            case VUE_PROJECT ->
                // 使用注入的组件实例
                    jsonMessageStreamHandler.handle(originFlux, chatHistoryService, appId, loginUser);
        };
    }
}
