package com.hgl.codegenie.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;

/**
 * 异步任务配置
 * 虚拟线程由 spring.threads.virtual.enabled=true 统一启用，不手动指定 Executor
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) ->
                log.error("@Async 方法 [{}] 执行异常: {}", method.getName(), ex.getMessage(), ex);
    }
}
