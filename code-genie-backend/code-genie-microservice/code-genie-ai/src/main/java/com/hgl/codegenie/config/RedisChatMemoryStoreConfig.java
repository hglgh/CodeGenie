package com.hgl.codegenie.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: RedisChatMemoryStoreConfig
 * Package: com.hgl.codegeniebackend.config
 * Description:
 *
 * @Author HGL
 * @Create: 2025/8/1 17:16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisChatMemoryStoreConfig {
    private String host;
    private int port;
    private String password;
    private long ttl;

    @Bean
    public RedisChatMemoryStore redisChatMemoryStore() {
        return RedisChatMemoryStore.builder()
                .host(host)
                .port(port)
                .password(password)
                .ttl(ttl)
                .build();
    }

}
