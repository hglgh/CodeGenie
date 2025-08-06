package com.hgl.codegeniebackend.core.elegance;

import com.hgl.codegeniebackend.ai.AiCodeGeneratorService;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.ai.model.HtmlCodeResult;
import com.hgl.codegeniebackend.core.elegance.parserenhanced.CodeParserManager;
import com.hgl.codegeniebackend.core.elegance.saverenhanced.CodeFileSaverManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * ClassName: HtmlCodeGenerationStrategy
 * Package: com.hgl.codegeniebackend.core.elegance
 * Description:   HTML代码生成策略实现
 *
 * @Author HGL
 * @Create: 2025/8/6 14:17
 */
@Slf4j
@Component
public class HtmlCodeGenerationStrategy extends AbstractCodeGenerationStrategy {

    @Override
    public CodeGenTypeEnum getSupportedType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    public File generateAndSaveCode(AiCodeGeneratorService aiCodeGeneratorService, String userMessage, Long appId, CodeFileSaverManager codeFileSaverManager) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return codeFileSaverManager.executeSaver(htmlCodeResult, CodeGenTypeEnum.HTML, appId);
    }

    @Override
    public Flux<String> generateAndSaveCodeStream(AiCodeGeneratorService aiCodeGeneratorService, String userMessage, Long appId,
                                                  CodeParserManager codeParserManager, CodeFileSaverManager codeFileSaverManager) {
        Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        return processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId, codeParserManager, codeFileSaverManager);
    }
}