package com.hgl.codegenie;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @ClassName: CodeGenieAppApplication
 * @Package: com.hgl.codegenie
 * @Description:
 * @Author HGL
 * @Create: 2025/8/27 16:19
 */
@EnableDubbo
@EnableCaching
@MapperScan("com.hgl.codegenie.mapper")
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
public class CodeGenieAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeGenieAppApplication.class, args);
    }
}
