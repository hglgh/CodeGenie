package com.hgl.codegeniebackend.core.parserenhanced;

import com.hgl.codegeniebackend.core.elegance.parserenhanced.CodeParserManager;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: CodeParserManagerTest
 * Package: com.hgl.codegeniebackend.core.parserenhanced
 * Description:
 *
 * @Author HGL
 * @Create: 2025/8/6 11:23
 */
@SpringBootTest
class CodeParserManagerTest {

    @Resource
    private CodeParserManager codeParserManager;
    @Test
    void executeParser() {
        codeParserManager.executeParser("123", null);
    }
}