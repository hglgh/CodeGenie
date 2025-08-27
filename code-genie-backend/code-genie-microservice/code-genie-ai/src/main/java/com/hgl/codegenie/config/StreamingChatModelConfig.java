package com.hgl.codegenie.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @ClassName: StreamingChatModelConfig
 * @Package: com.hgl.codegeniebackend.config
 * @Description: 流式对话模型配置
 * @Author HGL
 * @Create: 2025/8/15 16:25
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.streaming-chat-model")
public class StreamingChatModelConfig {
    private String baseUrl;

    private String apiKey;

    private String modelName;

    private Integer maxTokens;

    private Double temperature;

    private boolean logRequests;

    private boolean logResponses;


    @Bean
    @Scope("prototype")
    public StreamingChatModel streamingChatModelPrototype() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
    }
}
