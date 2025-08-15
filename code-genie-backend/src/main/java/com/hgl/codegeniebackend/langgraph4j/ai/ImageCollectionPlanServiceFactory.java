package com.hgl.codegeniebackend.langgraph4j.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: ImageCollectionPlanServiceFactory
 * @Package: com.hgl.codegeniebackend.langgraph4j.ai
 * @Description: 图片收集计划服务工厂
 * @Author HGL
 * @Create: 2025/8/15 14:35
 */
@Configuration
public class ImageCollectionPlanServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Bean
    public ImageCollectionPlanService createImageCollectionPlanService() {
        return AiServices.builder(ImageCollectionPlanService.class)
                .chatModel(chatModel)
                .build();
    }
}

