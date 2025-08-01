package com.hgl.codegeniebackend.config.expansion;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ClassName: ServeLifecycleManager
 * Package: com.hgl.codegeniebackend.config.expansion
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/31 18:00
 */
//@Component
public class ServeLifecycleManager {

    @Resource
    private ServeDeployService serveDeployService;

    /**
     * Spring Boot 启动完成后启动 Serve 服务
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        serveDeployService.startServeService();
    }

    /**
     * Spring Boot 关闭时停止 Serve 服务
     */
    @PreDestroy
    public void onApplicationShutdown() {
        System.out.println("Shutting down Serve service...");
        serveDeployService.stopServeService();
    }
}
