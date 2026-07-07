package com.hgl.codegenie.core.pipeline;

import com.hgl.codegenie.AiCodeGeneratorService;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 多文件代码生成策略（HTML + CSS + JS）
 */
@Component
public class MultiFileCodeGenerationStrategy extends AbstractCodeGenerationStrategy {

    @Override
    public CodeGenTypeEnum getSupportedType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected Flux<String> getCodeStream(AiCodeGeneratorService service, String userMessage, long appId) {
        return service.generateMultiFileCodeStream(userMessage);
    }
}
