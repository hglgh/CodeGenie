package com.hgl.codegenie.core.pipeline;

import com.hgl.codegenie.AiCodeGeneratorService;
import com.hgl.codegenie.core.parser.CodeParserExecutor;
import com.hgl.codegenie.core.saver.CodeFileSaverExecutor;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * 代码生成策略基类 — 提供通用的 collect → parse → save 流程
 */
@Slf4j
public abstract class AbstractCodeGenerationStrategy implements CodeGenerationStrategy {

    /**
     * 获取底层 AI 代码流（由子类提供）
     */
    protected abstract Flux<String> getCodeStream(AiCodeGeneratorService service, String userMessage, long appId);

    /**
     * 流结束后是否执行 parse + save（默认 true，VUE_PROJECT 等工具生成类可覆盖为 false）
     */
    protected boolean shouldParseAndSave() {
        return true;
    }

    @Override
    public Flux<String> generateAndSaveCodeStream(AiCodeGeneratorService service, String userMessage, long appId) {
        Flux<String> codeStream = getCodeStream(service, userMessage, appId);
        if (!shouldParseAndSave()) {
            return codeStream;
        }
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        Object codeResult = CodeParserExecutor.executeParser(codeBuilder.toString(), getSupportedType());
                        File savedDir = CodeFileSaverExecutor.executeSaver(codeResult, getSupportedType(), appId);
                        log.info("保存成功，路径为：{}", savedDir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                })
                .doOnError(throwable -> log.error("保存代码失败：{}", throwable.getMessage()));
    }
}
