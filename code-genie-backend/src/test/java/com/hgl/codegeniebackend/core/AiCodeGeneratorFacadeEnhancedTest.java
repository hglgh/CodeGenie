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
        Flux<String> codeStream = aiCodeGeneratorFacadeEnhanced.generateAndSaveCodeStream("生成测试demo,不超过20行代码", CodeGenTypeEnum.MULTI_FILE, 2L);
        //阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 测试结果
        Assertions.assertNotNull(result);
        //拼接字符串，得到完整内容
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);

    }

    @Test
    void generateVueProjectCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacadeEnhanced.generateAndSaveCodeStream(
                "简单的任务记录网站，‌总代码量不超过 200 行",
                CodeGenTypeEnum.VUE_PROJECT, 1L);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }

}