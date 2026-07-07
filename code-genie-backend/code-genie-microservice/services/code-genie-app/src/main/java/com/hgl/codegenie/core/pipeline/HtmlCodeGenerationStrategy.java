package com.hgl.codegenie.core.pipeline;

import com.hgl.codegenie.AiCodeGeneratorService;
import com.hgl.codegenie.model.enums.CodeGenTypeEnum;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * HTML 单文件代码生成策略
 */
@Component
public class HtmlCodeGenerationStrategy extends AbstractCodeGenerationStrategy {

    @Override
    public CodeGenTypeEnum getSupportedType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected Flux<String> getCodeStream(AiCodeGeneratorService service, String userMessage, long appId) {
        return service.generateHtmlCodeStream(userMessage);
    }
}
