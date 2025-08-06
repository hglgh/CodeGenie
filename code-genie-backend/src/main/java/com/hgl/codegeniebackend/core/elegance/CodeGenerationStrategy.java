package com.hgl.codegeniebackend.core.elegance;

import com.hgl.codegeniebackend.ai.AiCodeGeneratorService;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.core.elegance.parserenhanced.CodeParserManager;
import com.hgl.codegeniebackend.core.elegance.saverenhanced.CodeFileSaverManager;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * ClassName: CodeGenerationStrategy
 * Package: com.hgl.codegeniebackend.core.elegance
 * Description:  代码生成策略接口
 *
 * @Author HGL
 * @Create: 2025/8/6 14:16
 */
public interface CodeGenerationStrategy {

    /**
     * 获取支持的代码生成类型
     * @return 代码生成类型枚举
     */
    CodeGenTypeEnum getSupportedType();

    /**
     * 生成并保存代码
     * @param aiCodeGeneratorService AI代码生成服务
     * @param userMessage 用户提示词
     * @param appId 应用ID
     * @param codeFileSaverManager 代码保存管理器
     * @return 保存的目录
     */
    File generateAndSaveCode(AiCodeGeneratorService aiCodeGeneratorService, String userMessage, Long appId, CodeFileSaverManager codeFileSaverManager);

    /**
     * 流式生成并保存代码
     * @param aiCodeGeneratorService AI代码生成服务
     * @param userMessage 用户提示词
     * @param appId 应用ID
     * @param codeParserManager 代码解析管理器
     * @param codeFileSaverManager 代码保存管理器
     * @return 代码流
     */
    Flux<String> generateAndSaveCodeStream(AiCodeGeneratorService aiCodeGeneratorService, String userMessage, Long appId,
                                           CodeParserManager codeParserManager, CodeFileSaverManager codeFileSaverManager);
}
