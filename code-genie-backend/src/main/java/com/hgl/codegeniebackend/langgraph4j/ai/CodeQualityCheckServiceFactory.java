package com.hgl.codegeniebackend.langgraph4j.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: CodeQualityCheckServiceFactory
 * @Package: com.hgl.codegeniebackend.langgraph4j.ai
 * @Description:
 * @Author HGL
 * @Create: 2025/8/15 13:45
 */
@Slf4j
@Configuration
public class CodeQualityCheckServiceFactory {

    @Resource
    private ChatModel chatModel;

    /**
     * 创建代码质量检查 AI 服务
     */
    @Bean
    public CodeQualityCheckService createCodeQualityCheckService() {
        return AiServices.builder(CodeQualityCheckService.class)
                .chatModel(chatModel)
                .build();
    }
}
