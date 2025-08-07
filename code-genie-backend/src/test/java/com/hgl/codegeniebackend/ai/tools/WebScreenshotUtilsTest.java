package com.hgl.codegeniebackend.ai.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ClassName: WebScreenshotUtilsTest
 *
 * @Package: com.hgl.codegeniebackend.ai.tools
 * @Description:
 * @Author HGL
 * @Create: 2025/8/7 15:49
 */
class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String testUrl = "https://spring.io";
        String webPageScreenshot = WebScreenshotUtils.saveWebPageScreenshot(testUrl);
        Assertions.assertNotNull(webPageScreenshot);
    }
}