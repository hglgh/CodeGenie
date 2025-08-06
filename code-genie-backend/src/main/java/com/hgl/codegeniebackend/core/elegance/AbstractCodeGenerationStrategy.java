package com.hgl.codegeniebackend.core.elegance;

import com.hgl.codegeniebackend.ai.AiCodeGeneratorService;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.core.elegance.parserenhanced.CodeParserManager;
import com.hgl.codegeniebackend.core.elegance.saverenhanced.CodeFileSaverManager;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * ClassName: AbstractCodeGenerationStrategy
 * Package: com.hgl.codegeniebackend.core.elegance
 * Description:  代码生成策略抽象基类，包含通用实现
 *
 * @Author HGL
 * @Create: 2025/8/6 14:40
 */
@Slf4j
public abstract class AbstractCodeGenerationStrategy implements CodeGenerationStrategy {

    /**
     * 通用的流式处理方法
     * @param codeStream 代码流
     * @param codeGenType 代码生成类型
     * @param appId 应用ID
     * @param codeParserManager 代码解析管理器
     * @param codeFileSaverManager 代码保存管理器
     * @return 处理后的代码流
     */
    protected Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId,
                                             CodeParserManager codeParserManager, CodeFileSaverManager codeFileSaverManager) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        Object codeResult = codeParserManager.executeParser(codeBuilder.toString(), codeGenType);
                        File savedDir = codeFileSaverManager.executeSaver(codeResult, codeGenType, appId);
                        log.info("保存成功，路径为：{}", savedDir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage(), e);
                    }
                })
                .doOnError(throwable -> log.error("保存代码失败：{}", throwable.getMessage(), throwable));
    }

    @Override
    public File generateAndSaveCode(AiCodeGeneratorService aiCodeGeneratorService, String userMessage, Long appId, CodeFileSaverManager codeFileSaverManager) {
        throw new UnsupportedOperationException("子类必须实现 generateAndSaveCode 方法");
    }

    @Override
    public Flux<String> generateAndSaveCodeStream(AiCodeGeneratorService aiCodeGeneratorService, String userMessage, Long appId,
                                                  CodeParserManager codeParserManager, CodeFileSaverManager codeFileSaverManager) {
        throw new UnsupportedOperationException("子类必须实现 generateAndSaveCodeStream 方法");
    }
}