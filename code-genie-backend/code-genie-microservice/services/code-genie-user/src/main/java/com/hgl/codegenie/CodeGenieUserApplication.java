package com.hgl.codegenie;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName: CodeGenieUserApplication
 * @Package: com.hgl.codegenie
 * @Description:
 * @Author HGL
 * @Create: 2025/8/27 14:55
 */
@EnableDubbo
@MapperScan("com.hgl.codegenie.mapper")
@SpringBootApplication
public class CodeGenieUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeGenieUserApplication.class, args);
    }
}
