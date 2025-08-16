package com.hgl.codegeniebackend;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 请别把我整破防
 */
@EnableCaching  // 开启缓存
@MapperScan("com.hgl.codegeniebackend.mapper")
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@EnableAspectJAutoProxy(exposeProxy = true)
public class CodeGenieBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeGenieBackendApplication.class, args);
    }

}
