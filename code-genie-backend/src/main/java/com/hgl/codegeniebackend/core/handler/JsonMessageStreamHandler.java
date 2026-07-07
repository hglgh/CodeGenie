package com.hgl.codegeniebackend.core.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hgl.codegeniebackend.ai.enums.StreamMessageTypeEnum;
import com.hgl.codegeniebackend.ai.model.message.AiResponseMessage;
import com.hgl.codegeniebackend.ai.model.message.StreamMessage;
import com.hgl.codegeniebackend.ai.model.message.ToolExecutedMessage;
import com.hgl.codegeniebackend.ai.model.message.ToolRequestMessage;
import com.hgl.codegeniebackend.ai.tools.BaseTool;
import com.hgl.codegeniebackend.ai.tools.ToolManager;
import com.hgl.codegeniebackend.common.constant.AppConstant;
import com.hgl.codegeniebackend.core.builder.VueProjectBuilder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;

/**
 * JSON 消息流处理器 — 处理 VUE_PROJECT 类型的复杂流式响应
 * 历史记录委托给 ChatHistoryRecorder，Vue 构建在 doOnComplete 中触发
 */
@Slf4j
@Component
public class JsonMessageStreamHandler {

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Resource
    private ToolManager toolManager;

    @Resource
    private ChatHistoryRecorder chatHistoryRecorder;

    /**
     * 处理 TokenStream（VUE_PROJECT）
     *
     * @param originFlux 原始流
     * @param appId      应用ID
     * @param userId     用户ID
     * @return 处理后的流
     */
    public Flux<String> handle(Flux<String> originFlux, long appId, long userId) {
        Set<String> seenToolIds = new HashSet<>();
        Flux<String> processedFlux = originFlux
                .mapNotNull(chunk -> handleJsonMessageChunk(chunk, seenToolIds))
                .filter(StrUtil::isNotEmpty);
        return chatHistoryRecorder.record(processedFlux, appId, userId)
                .doOnComplete(() -> {
                    // 异步构建 Vue 项目
                    String projectPath = AppConstant.CODE_OUTPUT_ROOT_DIR + "/vue_project_" + appId;
                    vueProjectBuilder.buildProject(projectPath);
                });
    }

    /**
     * 解析 JSON 消息块，格式化为前端可展示的内容
     */
    private String handleJsonMessageChunk(String chunk, Set<String> seenToolIds) {
        if (!chunk.trim().startsWith("{")) {
            log.warn("接收到非JSON格式的数据块: {}", chunk);
            return "";
        }
        StreamMessage streamMessage = JSONUtil.toBean(chunk, StreamMessage.class);
        StreamMessageTypeEnum typeEnum = StreamMessageTypeEnum.getEnumByValue(streamMessage.getType());
        return switch (typeEnum) {
            case AI_RESPONSE -> {
                AiResponseMessage aiMessage = JSONUtil.toBean(chunk, AiResponseMessage.class);
                yield aiMessage.getData();
            }
            case TOOL_REQUEST -> {
                ToolRequestMessage toolRequestMessage = JSONUtil.toBean(chunk, ToolRequestMessage.class);
                String toolId = toolRequestMessage.getId();
                String toolName = toolRequestMessage.getName();
                if (toolId != null && !seenToolIds.contains(toolId)) {
                    seenToolIds.add(toolId);
                    BaseTool tool = toolManager.getTool(toolName);
                    yield tool.generateToolRequestResponse();
                } else {
                    yield "";
                }
            }
            case TOOL_EXECUTED -> {
                ToolExecutedMessage toolExecutedMessage = JSONUtil.toBean(chunk, ToolExecutedMessage.class);
                JSONObject jsonObject = JSONUtil.parseObj(toolExecutedMessage.getArguments());
                BaseTool tool = toolManager.getTool(toolExecutedMessage.getName());
                String result = tool.generateToolExecutedResult(jsonObject);
                yield String.format("\n\n%s\n\n", result);
            }
            case null -> {
                log.error("无法解析消息类型: {}", chunk);
                yield "";
            }
            default -> {
                log.error("不支持的消息类型: {}", typeEnum);
                yield "";
            }
        };
    }
}
