package com.hgl.codegeniebackend.core.elegance;

import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: AiCodeGeneratorFacadeFactoryTest
 * Package: com.hgl.codegeniebackend.core.elegance
 * Description:
 *
 * @Author HGL
 * @Create: 2025/8/6 14:46
 */
@SpringBootTest
class AiCodeGeneratorFacadeFactoryTest {
    @Resource
    private AiCodeGeneratorFacadeFactory aiCodeGeneratorFacadeFactory;

    @Test
    void generateAndSaveCode() {
        Flux<String> codeStream = aiCodeGeneratorFacadeFactory.generateAndSaveCodeStream("写个测试demo,不超过20行代码", CodeGenTypeEnum.HTML, 1L);
        //阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 测试结果
        Assertions.assertNotNull(result);
        //拼接字符串，得到完整内容
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }

    @Test
    void generateAndSaveCodeStream() {
    }
}