package com.hgl.codegeniebackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: SystemController
 * Package: com.hgl.codegeniebackend.controller
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/25 9:41
 */
@RequestMapping("/system")
@RestController
public class SystemController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
