package com.hgl.codegeniebackend.core.elegance;

import com.hgl.codegeniebackend.ai.AiCodeGeneratorService;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.ai.model.MultiFileCodeResult;
import com.hgl.codegeniebackend.core.elegance.parserenhanced.CodeParserManager;
import com.hgl.codegeniebackend.core.elegance.saverenhanced.CodeFileSaverManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * ClassName: MultiFileCodeGenerationStrategy
 * Package: com.hgl.codegeniebackend.core.elegance
 * Description:  多文件代码生成策略实现
 *
 * @Author HGL
 * @Create: 2025/8/6 14:19
 */
@Slf4j
@Component
public class MultiFileCodeGenerationStrategy extends AbstractCodeGenerationStrategy {

    @Override
    public CodeGenTypeEnum getSupportedType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    public File generateAndSaveCode(AiCodeGeneratorService aiCodeGeneratorService, String userMessage, Long appId, CodeFileSaverManager codeFileSaverManager) {
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return codeFileSaverManager.executeSaver(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE, appId);
    }

    @Override
    public Flux<String> generateAndSaveCodeStream(AiCodeGeneratorService aiCodeGeneratorService, String userMessage, Long appId,
                                                  CodeParserManager codeParserManager, CodeFileSaverManager codeFileSaverManager) {
        Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        return processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId, codeParserManager, codeFileSaverManager);
    }
}