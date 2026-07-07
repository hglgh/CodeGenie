package com.hgl.codegeniebackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CodeGenie 统一配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "codegenie")
public class CodeGenieProperties {

    private Security security = new Security();
    private Deploy deploy = new Deploy();
    private Ai ai = new Ai();

    @Data
    public static class Security {
        private String passwordSalt = "CHANGE_ME";
        private String defaultPassword = "12345678";
    }

    @Data
    public static class Deploy {
        private String codeOutputRootDir;
        private String codeDeployRootDir;
        private String screenshotRootDir;
        private String deployHost = "http://localhost";
        private int deployKeyLength = 6;
    }

    @Data
    public static class Ai {
        private int chatHistoryLoadCount = 20;
        private int chatMemoryMaxMessages = 100;
    }
}
