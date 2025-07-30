package com.hgl.codegeniebackend.ai;

import com.hgl.codegeniebackend.ai.model.HtmlCodeResult;
import com.hgl.codegeniebackend.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: AiCodeGeneratorServiceTest
 * Package: com.hgl.codegeniebackend.ai
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 13:29
 */
@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("做个程序员HGL的工作记录小工具，不超过20行代码");
        Assertions.assertNotNull(result);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult multiFileCode = aiCodeGeneratorService.generateMultiFileCode("做个程序员HGL的留言板，不超过50行代码");
        Assertions.assertNotNull(multiFileCode);
    }
}