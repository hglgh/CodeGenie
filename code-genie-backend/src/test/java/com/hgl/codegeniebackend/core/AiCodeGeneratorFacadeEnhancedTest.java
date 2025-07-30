package com.hgl.codegeniebackend.core;

import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

/**
 * ClassName: AiCodeGeneratorFacadeEnhancedTest
 * Package: com.hgl.codegeniebackend.core
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 17:24
 */
@SpringBootTest
class AiCodeGeneratorFacadeEnhancedTest {

    @Resource
    private AiCodeGeneratorFacadeEnhanced aiCodeGeneratorFacadeEnhanced;

    @Test
    void generateAndSaveCode() {
        File file = aiCodeGeneratorFacadeEnhanced.generateAndSaveCode("生成程序员HGL的个人博客，不超过20行代码", CodeGenTypeEnum.HTML);
        Assertions.assertNotNull(file);
    }

    @Test
    void generateAndSaveCodeStream() {
    }
}