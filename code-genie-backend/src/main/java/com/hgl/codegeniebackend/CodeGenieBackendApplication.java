package com.hgl.codegeniebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 请别把我整破防
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class CodeGenieBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeGenieBackendApplication.class, args);
    }

}
