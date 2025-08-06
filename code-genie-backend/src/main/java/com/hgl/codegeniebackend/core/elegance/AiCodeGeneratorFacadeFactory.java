package com.hgl.codegeniebackend.core.elegance;

import com.hgl.codegeniebackend.ai.AiCodeGeneratorService;
import com.hgl.codegeniebackend.ai.AiCodeGeneratorServiceFactory;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;
import com.hgl.codegeniebackend.core.elegance.parserenhanced.CodeParserManager;
import com.hgl.codegeniebackend.core.elegance.saverenhanced.CodeFileSaverManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.Map;

/**
 * ClassName: AiCodeGeneratorFacadeFactory
 * Package: com.hgl.codegeniebackend.core.elegance
 * Description:
 *
 * @Author HGL
 * @Create: 2025/8/6 14:19
 */
@Slf4j
@Service
public class AiCodeGeneratorFacadeFactory {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    @Resource
    private CodeFileSaverManager codeFileSaverManager;

    @Resource
    private CodeParserManager codeParserManager;

    @Resource
    private Map<String, CodeGenerationStrategy> strategyMap;

    /**
     * 统一入口：根据类型生成并保存代码 (使用appId)
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用ID
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "未指定生成类型");
        CodeGenerationStrategy strategy = getStrategy(codeGenTypeEnum);
        AiCodeGeneratorService aiCodeGeneratorService = getAiCodeGeneratorService(appId);
        
        log.info("开始生成代码，类型: {}, 应用ID: {}", codeGenTypeEnum.getValue(), appId);
        File result = strategy.generateAndSaveCode(aiCodeGeneratorService, userMessage, appId, codeFileSaverManager);
        log.info("代码生成并保存完成");
        return result;
    }

    /**
     * 统一入口：根据类型生成并保存代码(流式,使用appId)
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用ID
     * @return 代码流
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "未指定生成类型");
        CodeGenerationStrategy strategy = getStrategy(codeGenTypeEnum);
        AiCodeGeneratorService aiCodeGeneratorService = getAiCodeGeneratorService(appId);
        
        log.info("开始流式生成代码，类型: {}, 应用ID: {}", codeGenTypeEnum.getValue(), appId);
        return strategy.generateAndSaveCodeStream(aiCodeGeneratorService, userMessage, appId, codeParserManager, codeFileSaverManager)
                .doOnComplete(() -> log.info("流式代码生成并保存完成"))
                .doOnError(throwable -> log.error("流式代码生成失败: {}", throwable.getMessage(), throwable));
    }

    /**
     * 获取代码生成策略
     * @param codeGenTypeEnum 代码生成类型
     * @return 对应的策略实现
     */
    private CodeGenerationStrategy getStrategy(CodeGenTypeEnum codeGenTypeEnum) {
        for (CodeGenerationStrategy strategy : strategyMap.values()) {
            if (strategy.getSupportedType() == codeGenTypeEnum) {
                log.debug("找到匹配的策略: {}", strategy.getClass().getSimpleName());
                return strategy;
            }
        }
        log.error("不支持的生成类型: {}", codeGenTypeEnum.getValue());
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型：" + codeGenTypeEnum.getValue());
    }

    /**
     * 获取AI代码生成服务实例
     *
     * @param appId 应用ID
     * @return AI代码生成服务实例
     */
    private AiCodeGeneratorService getAiCodeGeneratorService(Long appId) {
        log.debug("获取AI代码生成服务实例，应用ID: {}", appId);
        return aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
    }
}
