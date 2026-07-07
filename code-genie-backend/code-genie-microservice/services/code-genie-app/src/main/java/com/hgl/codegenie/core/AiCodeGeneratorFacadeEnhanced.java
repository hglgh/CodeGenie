package com.hgl.codegenie.core;

import com.hgl.codegenie.AiCodeGeneratorService;
import com.hgl.codegenie.common.exception.BusinessException;
import com.hgl.codegenie.common.exception.ErrorCode;
import com.hgl.codegenie.common.exception.ThrowUtils;
import com.hgl.codegenie.core.parser.CodeParserExecutor;
import com.hgl.codegenie.core.pipeline.CodeGenerationStrategy;
import com.hgl.codegenie.core.saver.CodeFileSaverExecutor;
import com.hgl.codegenie.factory.AiCodeGeneratorServiceFactory;
import com.hgl.codegenie.model.HtmlCodeResult;
import com.hgl.codegenie.model.MultiFileCodeResult;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成门面 — 使用策略模式编排不同类型的代码生成流程
 *
 * @Author HGL
 */
@Slf4j
@Service
public class AiCodeGeneratorFacadeEnhanced {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    private final Map<CodeGenTypeEnum, CodeGenerationStrategy> strategyMap = new EnumMap<>(CodeGenTypeEnum.class);

    /**
     * Spring 自动注入所有 CodeGenerationStrategy 实现，按类型注册
     */
    @Resource
    public void setStrategies(List<CodeGenerationStrategy> strategies) {
        for (CodeGenerationStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedType(), strategy);
        }
        log.info("已注册代码生成策略: {}", strategyMap.keySet());
    }

    /**
     * 统一入口：根据类型生成并保存代码（流式，使用 appId）
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenType, Long appId) {
        ThrowUtils.throwIf(codeGenType == null, ErrorCode.PARAMS_ERROR, "未指定生成类型");
        AiCodeGeneratorService service = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenType);
        CodeGenerationStrategy strategy = strategyMap.get(codeGenType);
        if (strategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型：" + codeGenType.getValue());
        }
        return strategy.generateAndSaveCodeStream(service, userMessage, appId);
    }

    /**
     * 同步入口（保留向后兼容）
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "未指定生成类型");
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(htmlCodeResult, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型：" + codeGenTypeEnum.getValue());
        };
    }
}
