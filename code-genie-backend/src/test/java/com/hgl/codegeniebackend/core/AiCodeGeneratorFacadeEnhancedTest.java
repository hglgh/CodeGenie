package com.hgl.codegeniebackend.core;

import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

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
    void testGenerateAndSaveCode() {
        File file = aiCodeGeneratorFacadeEnhanced.generateAndSaveCode("生成程序员HGL的个人博客", CodeGenTypeEnum.HTML, 1L);
        Assertions.assertNotNull(file);
    }

    @Test
    void testGenerateAndSaveCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacadeEnhanced.generateAndSaveCodeStream("生成程序员HGL的个人博客", CodeGenTypeEnum.MULTI_FILE, 1L);
        //阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 测试结果
        Assertions.assertNotNull(result);
        //拼接字符串，得到完整内容
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);

    }
}