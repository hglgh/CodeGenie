package com.hgl.codegenie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @ClassName: CodeGenieScreenshotApplication
 * @Package: com.hgl.codegenie
 * @Description:
 * @Author HGL
 * @Create: 2025/8/27 17:08
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CodeGenieScreenshotApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeGenieScreenshotApplication.class, args);
    }
}
