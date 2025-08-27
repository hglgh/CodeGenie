package com.hgl.codegenie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName: CodeGenieUserApplication
 * @Package: com.hgl.codegenie
 * @Description:
 * @Author HGL
 * @Create: 2025/8/27 14:55
 */
@MapperScan("com.hgl.codegenie.mapper")
@SpringBootApplication
public class CodeGenieUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeGenieUserApplication.class, args);
    }
}
