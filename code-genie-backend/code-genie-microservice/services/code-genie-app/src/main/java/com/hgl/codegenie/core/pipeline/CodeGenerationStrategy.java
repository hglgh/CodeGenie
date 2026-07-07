package com.hgl.codegenie.core.pipeline;

import com.hgl.codegenie.AiCodeGeneratorService;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;
import reactor.core.publisher.Flux;

/**
 * 代码生成策略接口 — 每种 CodeGenType 对应一个实现
 */
public interface CodeGenerationStrategy {

    /**
     * 该策略支持的代码生成类型
     */
    CodeGenTypeEnum getSupportedType();

    /**
     * 生成代码并保存（流式）
     *
     * @param service   AI 代码生成服务
     * @param userMessage 用户提示词
     * @param appId     应用 ID
     * @return 流式响应
     */
    Flux<String> generateAndSaveCodeStream(AiCodeGeneratorService service, String userMessage, long appId);
}
